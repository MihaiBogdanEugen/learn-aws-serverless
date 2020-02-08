resource aws_s3_bucket_notification bucket_notification {
  bucket = var.bucket_id
  lambda_function {
    lambda_function_arn = var.function_arn
    events              = ["s3:ObjectCreated:*"]
    filter_suffix       = ".${var.file_extension}"
  }

  depends_on = [var.depends_on_bucket, var.depends_on_function]
}