import json
import logging
import os
import boto3

from aws_xray_sdk.core import patch_all

patch_all()

logger = logging.getLogger()
logger.setLevel(logging.INFO)

dynamoDB = boto3.resource("dynamodb")


def handle_request(event, context):
    logger.info(f"RemainingTimeInMillis {context.get_remaining_time_in_millis()}")

    request_http_method = event["httpMethod"]
    if request_http_method.lower() != "get":
        logger.info(f"Unsupported http method {request_http_method}")
        return {
            "statusCode": 405
        }

    if "id" not in event["pathParameters"]:
        logger.info("Missing parameter id")
        return {
            "statusCode": 400
        }

    identifier = event["pathParameters"]["id"]
    logger.info(f"Retrieving movie with the identifier {identifier}")

    try:
        movie_and_stat = get_movie_and_stat_by_id(identifier, os.environ["MOVIES_TABLE"], os.environ["STATS_TABLE"])

        if movie_and_stat is None:
            return {
                "statusCode": 404,
            }
        else:
            return {
                "statusCode": 200,
                "body": json.dumps(movie_and_stat)
            }
    except Exception as e:
        logger.error(f"Error while retrieving movie {identifier} from DynamoDB, error: {e}")
        return {
            "statusCode": 500
        }


def get_movie_and_stat_by_id(identifier, movies_table, stats_table):
    movie = get_record_by_id(identifier, movies_table)
    stat = get_record_by_id(identifier, stats_table)

    if movie is None and stat is None:
        return None

    if movie is None:
        return stat

    if stat is None:
        return movie

    movie.update(stat)
    return movie


def get_record_by_id(identifier, table):
    table = dynamoDB.Table(table)
    response = table.get_item(
        Key={
            "id": identifier
        },
    )
    
    if "Item" in response:
        return response["Item"]

    return None
