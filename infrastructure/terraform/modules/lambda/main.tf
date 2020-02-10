resource aws_lambda_function lambda {
  function_name    = var.function_name
  description      = var.description
  role             = var.role
  runtime          = var.runtime
  memory_size      = var.memory_size
  timeout          = var.timeout
  handler          = var.handler
  filename         = var.filename
  source_code_hash = base64sha256(var.source_code_hash)
  publish          = true
  layers           = [aws_lambda_layer_version.lambda_layer.arn]

  environment {
    variables = var.env
  }

  tracing_config {
    mode = var.tracing_config_mode
  }
}

resource aws_lambda_layer_version lambda_layer {
  filename            = var.layer_filename
  layer_name          = var.layer_name
  source_code_hash    = base64sha256(var.layer_source_code_hash)
  compatible_runtimes = [var.runtime]
}

resource aws_lambda_alias lambda_alias {
  depends_on       = [aws_lambda_function.lambda]
  name             = "live"
  description      = "Alias for ${var.function_name}"
  function_name    = aws_lambda_function.lambda.function_name
  function_version = aws_lambda_function.lambda.version

  lifecycle {
    ignore_changes = [
      function_version,
      routing_config
    ]
  }
}

resource aws_lambda_provisioned_concurrency_config provisioned_concurrency {
  function_name                     = aws_lambda_function.lambda.function_name
  provisioned_concurrent_executions = var.provisioned_concurrent_executions
  qualifier                         = aws_lambda_alias.lambda_alias.name
}