from pyspark.ml.feature import StringIndexer, IndexToString
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.ml.recommendation import ALS
from pyspark.sql import SparkSession, SQLContext, Row

import os
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class RecommendationEngine:
    """A business recommendation engine
    """

    def __train_model(self):
        """Train the ALS model with the current dataset
        """
        logger.info('Training the ALS model...')
        als = ALS(rank=2, maxIter=5, regParam=0.3, seed=3, nonnegative=True, userCol='user_index',
                itemCol='business_index', ratingCol='stars', coldStartStrategy='drop')
        self.model = als.fit(self.rating_DF)
        logger.info('ALS model built!')

    def __train_location_filtered_DF(self, city, state):
        """Filters business and review data based on location and train the ALS model
        """
        # Join data together to form filtered rating data
        rating_data = self.review_DF\
            .join(self.business_DF, 'business_id')
        filtered_rating_DF = rating_data\
            .select('user_id', 'business_id', 'stars')\
            .where(rating_data.city == city)\
            .where(rating_data.state == state)

        # Convert string ids into indexes
        user_string_indexer = StringIndexer(inputCol='user_id', outputCol='user_index')
        business_string_indexer = StringIndexer(inputCol='business_id', outputCol='business_index')
        user_indexed = user_string_indexer\
            .fit(filtered_rating_DF)\
            .transform(filtered_rating_DF)
        reindexed_rating_DF = business_string_indexer\
            .fit(user_indexed)\
            .transform(user_indexed)
        self.user_id_index_mapping = reindexed_rating_DF\
            .select('user_id', 'user_index')
        self.business_id_index_mapping = reindexed_rating_DF\
            .select('business_id', 'business_index')
        self.rating_DF = reindexed_rating_DF\
            .select('user_index', 'business_index', 'stars')

        # Train the model
        self.__train_model()

    def get_top_ratings(self, user_id, business_count, city, state):
        """Recommends up to business_count top unrated businesses to user_id
        """
        # Train location filtered dataframe
        self.__train_location_filtered_DF(city, state)

        # Convert user_id to user_index
        user_index = self.user_id_index_mapping\
            .select('user_index')\
            .where(self.user_id_index_mapping.user_id == user_id)\
            .collect()[0]\
            .user_index

        # Convert index to dataframe
        user_index_col_DF = self.sqlContext\
            .createDataFrame([Row(user_index=user_index)])

        # Get recommendations
        user_recs = self.model\
            .recommendForUserSubset(user_index_col_DF, business_count)

        # Convert business_index to business_id
        user_recs_with_index = self.sqlContext\
            .createDataFrame(user_recs.collect()[0].recommendations)
        user_recs_with_id = user_recs_with_index\
            .join(self.business_id_index_mapping, 'business_index')\
            .select('business_id')
        
        # Return set of results
        return list(set(map(lambda r: r.business_id, user_recs_with_id.collect())))

    def add_rating(self, rating):
        """Add additional business ratings in the format (user_id, business_id, stars)
        """
        # Convert ratings to an DF
        new_rating_DF = self.sqlContext\
            .createDataFrame([Row(**rating)])\
            .select('user_id', 'business_id', 'stars')

        # TODO: Update review data
        # new_rating_DF.write.csv(self.dataset_path, mode='append')

        # Add new ratings to the existing ones
        self.review_DF = self.review_DF.union(new_rating_DF)

    def __init__(self, sc, dataset_path):
        """Init the recommendation engine given a Spark context and a dataset path
        """
        logger.info('Starting up the Recommendation Engine: ')
        self.spark = SparkSession(sc)
        self.sqlContext = SQLContext(sc)

        # Load review data for later use
        logger.info('Loading Review data...')
        review_file_path = os.path.join(dataset_path, 'yelp_review.csv')
        self.review_DF = self.spark.read\
            .format('csv')\
            .options(header='true', inferSchema='true')\
            .load(review_file_path)

        # Load business data for later use
        logger.info('Loading business data...')
        business_file_path = os.path.join(dataset_path, 'yelp_business.csv')
        self.business_DF = self.spark.read\
            .format('csv')\
            .options(header='true', inferSchema='true')\
            .load(business_file_path)
