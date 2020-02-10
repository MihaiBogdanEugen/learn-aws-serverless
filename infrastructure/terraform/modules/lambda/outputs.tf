output name {
  value = aws_lambda_function.lambda.function_name
}

output id {
  value = aws_lambda_function.lambda.id
}

output arn {
  value = aws_lambda_function.lambda.arn
}

output invoke_arn {
  value = aws_lambda_alias.lambda_alias.invoke_arn
}
