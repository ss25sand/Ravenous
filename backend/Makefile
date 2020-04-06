base:
	docker build -t backend_spark-base ./spark-base

build:
	docker-compose build

run:
	docker-compose up -d --scale spark-worker=3

stop:
	docker-compose down
