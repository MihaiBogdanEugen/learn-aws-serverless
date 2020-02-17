import json
import logging
import os
import boto3

from aws_xray_sdk.core import patch_all
from decimal import Decimal

patch_all()

logger = logging.getLogger()
logger.setLevel(logging.INFO)

dynamoDB = boto3.resource('dynamodb')
s3 = boto3.resource('s3')


def handle_request(event, context):
    logger.info(f"fn_add_movies started remaining_time_in_millis = {context.get_remaining_time_in_millis()}")

    try:
        upload_movies(event, os.environ["MOVIES_BUCKET"], os.environ["MOVIES_TABLE"])
        return {
            "statusCode": 200,
        }
    except Exception as e:
        logger.error(f"Error uploading movies: {e}")
        return {
            "statusCode": 500
        }


def upload_movies(event, movies_bucket, movies_table):

    for record in event["Records"]:

        bucket_name = record["s3"]["bucket"]["name"]
        logger.info(f"bucket_name = {bucket_name}")

        if movies_bucket.lower() != bucket_name.lower():
            continue

        key = record["s3"]["object"]["key"]
        logger.info(f"key = {key}")

        s3_object = s3.Object(bucket_name, key)
        body = s3_object.get()["Body"]

        for line in body._raw_stream:

            str_line = line.decode("utf-8")
            logger.info(f"line = {str_line}")

            table = dynamoDB.Table(movies_table)

            with table.batch_writer() as batch:
                batch.put_item(Item=get_item(str_line))


def get_item(line):

    parts = line.split(",")

    item = {}

    if parts[0]:
        item["id"] = parts[0]

    if parts[1]:
        item["name"] = parts[1]

    if parts[2]:
        item["country_of_origin"] = parts[2]

    if parts[3]:
        item["production_date"] = parts[3]

    if parts[4]:
        item["budget"] = float(parts[4])

    return json.loads(json.dumps(item), parse_float=Decimal)
