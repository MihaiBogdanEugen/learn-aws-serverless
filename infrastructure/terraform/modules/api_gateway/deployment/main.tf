resource aws_api_gateway_deployment deployment {
  rest_api_id = var.rest_api_id
  stage_name  = var.stage_name
  depends_on  = [var.depends_on_integration_1, var.depends_on_integration_2]
}