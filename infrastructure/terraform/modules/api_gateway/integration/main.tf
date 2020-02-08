resource aws_api_gateway_integration integration {
  type                    = "AWS_PROXY"
  integration_http_method = "POST"
  rest_api_id             = var.rest_api_id
  resource_id             = var.resource_id
  http_method             = var.http_method
  uri                     = var.function_invoke_arn
  depends_on              = [var.depends_on_method]
}