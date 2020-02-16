import logging
import os
import boto3

from aws_xray_sdk.core import patch_all

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

    for record in event["records"]:

        bucket_name = record["s3"]["bucket"]["name"]
        logger.info(f"bucket_name = {bucket_name}")

        if movies_bucket.lower() != bucket_name.lower():
            continue

        key = record["s3"]["object"]["key"]
        logger.info(f"key = {key}")

        s3_object = s3.Object(bucket_name, key)
        body = s3_object.get()["Body"]

        for line in body._raw_stream:

            logger.info(f"line = {line}")

            table = dynamoDB.Table(movies_table)

            with table.batch_writer() as batch:
                batch.put_item(Item=get_item(line))


def get_item(line):

    parts = line.split(",")

    item = {}

    if parts[0]:
        item["id"] = {
            "S": parts[0]
        }

    if parts[1]:
        item["name"] = {
            "S": parts[1]
        }

    if parts[2]:
        item["country_of_origin"] = {
            "S": parts[2]
        }

    if parts[3]:
        item["production_date"] = {
            "S": parts[3]
        }

    if parts[4]:
        item["budget"] = {
            "N": parts[4]
        }

    return item
