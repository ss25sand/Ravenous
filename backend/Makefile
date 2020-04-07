data:
	unzip ./data/yelp_business.csv.zip && \
	unzip ./data/yelp_review_01.csv.zip && \
	unzip ./data/yelp_review_02.csv.zip && \
	cat ./data/yelp_review_01.csv ./data/yelp_review_02.csv > ./data/yelp_review.csv

base:
	docker build -t backend_spark-base ./spark-base

build:
	docker-compose build

run:
	docker-compose up -d --scale spark-worker=3

stop:
	docker-compose down
