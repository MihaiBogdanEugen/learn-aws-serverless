resource aws_api_gateway_rest_api rest_api {
  name        = var.name
  description = var.description
  endpoint_configuration {
    types = ["REGIONAL"]
  }
}