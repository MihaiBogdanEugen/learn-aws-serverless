resource aws_lambda_permission lambda_permission {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  principal     = "s3.amazonaws.com"
  function_name = var.function_arn
  source_arn    = var.bucket_arn
  depends_on    = [var.depends_on_function, var.depends_on_bucket]
}