#!/bin/bash
/spark/bin/spark-submit \
	--master ${SPARK_MASTER} \
	/opt/spark-apps/server.py