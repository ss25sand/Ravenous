data_files:
	unzip ./data/yelp_business.csv.zip -d ./data && \
	unzip ./data/yelp_review_01.csv.zip -d ./data && \
	unzip ./data/yelp_review_02.csv.zip -d ./data && \
	cat ./data/yelp_review_01.csv ./data/yelp_review_02.csv > ./data/yelp_review.csv

base:
	docker build -t backend_spark-base ./spark-base

build:
	docker-compose build

run:
	docker-compose up --scale spark-worker=3

stop:
	docker-compose down
