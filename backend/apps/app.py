from flask import Blueprint
main = Blueprint('main', __name__)
 
import json
from engine import RecommendationEngine
 
import logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
 
from flask import Flask, request
 
@main.route('/<string:user_id>/ratings/top/<int:count>', methods=['GET'])
def top_ratings(user_id, count):
    logger.debug('User %s TOP ratings requested', user_id)
    city = request.args['city']
    state = request.args['state']
    top_rating_list = recommendation_engine.get_top_ratings(user_id, count, city, state)
    return json.dumps(top_rating_list)
 
@main.route('/<string:user_id>/rating', methods = ['POST'])
def add_rating(user_id):
    # get the ratings from the Flask POST request object
    rating = request.json
    # add them to the model using then engine API
    recommendation_engine.add_rating(rating)
    return ('', 204)

def create_app(spark_context, dataset_path):
    global recommendation_engine 

    recommendation_engine = RecommendationEngine(spark_context, dataset_path)    
    
    app = Flask(__name__)
    app.register_blueprint(main)
    return app 