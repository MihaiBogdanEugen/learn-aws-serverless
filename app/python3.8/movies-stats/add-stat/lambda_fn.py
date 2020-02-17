import json
import logging
import os
import boto3

from aws_xray_sdk.core import patch_all
from decimal import Decimal

patch_all()

logger = logging.getLogger()
logger.setLevel(logging.INFO)

dynamoDB = boto3.resource("dynamodb")


def handle_request(event, context):
    logger.info(f"RemainingTimeInMillis {context.get_remaining_time_in_millis()}")

    request_http_method = event["httpMethod"]
    if request_http_method.lower() != "patch":
        logger.info(f"Unsupported http method {request_http_method}")
        return {
            "statusCode": 405
        }

    body = event["body"]
    if not body:
        logger.info("Empty body")
        return {
            "statusCode": 400
        }

    if "id" not in event["pathParameters"]:
        logger.info("Missing parameter id")
        return {
            "statusCode": 400
        }

    identifier = event["pathParameters"]["id"]
    logger.info(f"Patching movie with the identifier {identifier}")

    try:
        save_stat(identifier, json.loads(body), os.environ["STATS_TABLE"])
        return {
            "statusCode": 200
        }
    except Exception as e:
        logger.error(f"Error while updating stat {body} in DynamoDB, error: {e}")
        return {
            "statusCode": 500
        }


def save_stat(identifier, body, stats_table):
    update_expression = ""
    expression_attribute_values = {}

    if "direct_to_streaming" in body:
        update_expression += " direct_to_streaming = :direct_to_streaming,"
        expression_attribute_values[":direct_to_streaming"] = body["direct_to_streaming"]

    if "rotten_tomatoes_rating" in body:
        update_expression += " rotten_tomatoes_rating = :rotten_tomatoes_rating,"
        expression_attribute_values[":rotten_tomatoes_rating"] = int(body["rotten_tomatoes_rating"])

    if "imdb_rating" in body:
        update_expression += " imdb_rating = :imdb_rating,"
        expression_attribute_values[":imdb_rating"] = int(body["imdb_rating"])

    if "box_office" in body:
        update_expression += " box_office = :box_office,"
        expression_attribute_values[":box_office"] = float(body["box_office"])

    if "release_date" in body:
        update_expression += " release_date = :release_date,"
        expression_attribute_values[":release_date"] = body["release_date"]

    if not update_expression:
        return

    update_expression = "SET" + update_expression
    update_expression = update_expression[:-1]

    expression_attribute_values = json.loads(json.dumps(expression_attribute_values), parse_float=Decimal)

    logger.info(f"UpdateExpression: {update_expression}")
    logger.info(f"ExpressionAttributeValues: {expression_attribute_values}")

    table = dynamoDB.Table(stats_table)
    return table.update_item(
        Key={
            "id": identifier
        },
        UpdateExpression=update_expression,
        ExpressionAttributeValues=expression_attribute_values
    )
