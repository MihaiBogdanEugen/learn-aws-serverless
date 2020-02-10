resource aws_lambda_permission lambda_permission {
  statement_id  = "AllowExecutionFromApiGateway"
  action        = "lambda:InvokeFunction"
  principal     = "apigateway.amazonaws.com"
  function_name = var.function_arn
  qualifier     = "live"
  source_arn    = "arn:aws:execute-api:${var.region}:${var.account_id}:${var.api_gw_id}/*/${var.method_http_verb}${var.resource_path}"
  depends_on    = [var.depends_on_function, var.depends_on_api_gw]
}