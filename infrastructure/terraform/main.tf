terraform {
  required_version = "0.12.20"
}

provider aws {
  region  = var.aws_region
  version = "~> 2.47"
}

provider archive {
  version = "~> 1.3.0"
}

locals {
  add_movies_lambda_dist_filename               = "../../app/movies-stats/functions/add-movies/build/distributions/add-movies.zip"
  add_movies_lambda_layer_dist_filename         = "../../app/movies-stats/functions/add-movies/build/distributions/add-movies-layer.zip"
  add_stat_lambda_dist_filename                 = "../../app/movies-stats/functions/add-stat/build/distributions/add-stat.zip"
  add_stat_lambda_layer_dist_filename           = "../../app/movies-stats/functions/add-stat/build/distributions/add-stat-layer.zip"
  get_movie_and_stat_lambda_dist_filename       = "../../app/movies-stats/functions/get-movie-and-stat/build/distributions/get-movie-and-stat.zip"
  get_movie_and_stat_lambda_layer_dist_filename = "../../app/movies-stats/functions/get-movie-and-stat/build/distributions/get-movie-and-stat-layer.zip"
}

############################################################################

module movies_table {
  source = "./modules/dynamo_db"
  name   = "movies"
}

module stats_table {
  source = "./modules/dynamo_db"
  name   = "stats"
}

############################################################################

module movies_bucket {
  source = "./modules/s3"
  name   = "upload-movies-list"
}

############################################################################

data aws_iam_policy_document iam_assume_role_policy {
  statement {
    effect = "Allow"
    principals {
      identifiers = [
        "lambda.amazonaws.com"
      ]
      type = "Service"
    }
    actions = [
      "sts:AssumeRole"
    ]
  }
}

data aws_iam_policy_document add_movies_lambda_iam_policy_document {
  statement {
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = [
      "*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "xray:PutTraceSegments",
      "xray:PutTelemetryRecords",
      "xray:GetSamplingRules",
      "xray:GetSamplingTargets",
      "xray:GetSamplingStatisticSummaries"
    ]
    resources = [
      "*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "s3:ListBucket",
      "s3:GetBucketLocation"
    ]
    resources = [
      module.movies_bucket.arn
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "s3:GetObject",
      "s3:GetObjectAcl",
      "s3:GetObjectVersion"
    ]
    resources = [
      "${module.movies_bucket.arn}/*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "dynamodb:BatchWriteItem",
      "dynamodb:PutItem",
      "dynamodb:UpdateItem"
    ]
    resources = [
      module.movies_table.arn
    ]
  }
}

data aws_iam_policy_document add_stat_lambda_iam_policy_document {
  statement {
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = [
      "*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "xray:PutTraceSegments",
      "xray:PutTelemetryRecords",
      "xray:GetSamplingRules",
      "xray:GetSamplingTargets",
      "xray:GetSamplingStatisticSummaries"
    ]
    resources = [
      "*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "dynamodb:BatchWriteItem",
      "dynamodb:PutItem",
      "dynamodb:UpdateItem"
    ]
    resources = [
      module.stats_table.arn
    ]
  }
}

data aws_iam_policy_document get_movie_and_stat_lambda_iam_policy_document {
  statement {
    effect = "Allow"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents"
    ]
    resources = [
      "*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "xray:PutTraceSegments",
      "xray:PutTelemetryRecords",
      "xray:GetSamplingRules",
      "xray:GetSamplingTargets",
      "xray:GetSamplingStatisticSummaries"
    ]
    resources = [
      "*"
    ]
  }
  statement {
    effect = "Allow"
    actions = [
      "dynamodb:BatchGetItem",
      "dynamodb:GetItem",
      "dynamodb:Query",
      "dynamodb:Scan"
    ]
    resources = [
      module.movies_table.arn,
      module.stats_table.arn
    ]
  }
}

############################################################################

module add_movies_lambda_role {
  source                  = "./modules/iam/role"
  role_name               = "add_movies_lambda_role"
  assume_role_policy_json = data.aws_iam_policy_document.iam_assume_role_policy.json
  policy_name             = "add_movies_lambda_policy"
  policy_json             = data.aws_iam_policy_document.add_movies_lambda_iam_policy_document.json
}

module add_stat_lambda_role {
  source                  = "./modules/iam/role"
  role_name               = "add_stat_lambda_role"
  assume_role_policy_json = data.aws_iam_policy_document.iam_assume_role_policy.json
  policy_name             = "add_stat_lambda_policy"
  policy_json             = data.aws_iam_policy_document.add_stat_lambda_iam_policy_document.json
}

module get_movie_and_stat_lambda_role {
  source                  = "./modules/iam/role"
  role_name               = "get_movie_and_stat_lambda_role"
  assume_role_policy_json = data.aws_iam_policy_document.iam_assume_role_policy.json
  policy_name             = "get_movie_and_stat_lambda_policy"
  policy_json             = data.aws_iam_policy_document.get_movie_and_stat_lambda_iam_policy_document.json
}

############################################################################

module add_movies_lambda {
  source                 = "./modules/lambda"
  description            = "Read movies from an S3 file and dump them into the DynamoDB table"
  filename               = local.add_movies_lambda_dist_filename
  layer_filename         = local.add_movies_lambda_layer_dist_filename
  source_code_hash       = filebase64sha256(local.add_movies_lambda_dist_filename)
  layer_source_code_hash = filebase64sha256(local.add_movies_lambda_layer_dist_filename)
  function_name          = "fn_add_movies"
  layer_name             = "fn_add_movies_layer"
  handler                = "de.mbe.tutorials.aws.serverless.moviesstats.functions.addmovies.FnAddMovies::handleRequest"
  role                   = module.add_movies_lambda_role.arn
  env = {
    MOVIES_BUCKET = module.movies_bucket.name
    MOVIES_TABLE  = module.movies_table.name
  }
}

module add_stat_lambda {
  source                 = "./modules/lambda"
  description            = "Receive a PATCH request from the API GW and save the resource in the DynamoDB table"
  filename               = local.add_stat_lambda_dist_filename
  layer_filename         = local.add_stat_lambda_layer_dist_filename
  source_code_hash       = filebase64sha256(local.add_stat_lambda_dist_filename)
  layer_source_code_hash = filebase64sha256(local.add_stat_lambda_layer_dist_filename)
  function_name          = "fn_add_stat"
  layer_name             = "fn_add_stat_layer"
  handler                = "de.mbe.tutorials.aws.serverless.moviesstats.functions.addstat.FnAddStat::handleRequest"
  role                   = module.add_stat_lambda_role.arn
  env = {
    STATS_TABLE = module.stats_table.name
  }
}

module get_movie_and_stat_lambda {
  source                 = "./modules/lambda"
  description            = "Receive a GET request from the API GW and retreive the resource from the DynamoDB table"
  filename               = local.get_movie_and_stat_lambda_dist_filename
  layer_filename         = local.get_movie_and_stat_lambda_layer_dist_filename
  source_code_hash       = filebase64sha256(local.get_movie_and_stat_lambda_dist_filename)
  layer_source_code_hash = filebase64sha256(local.get_movie_and_stat_lambda_layer_dist_filename)
  function_name          = "fn_get_movie_and_stat"
  layer_name             = "fn_get_movie_and_stat_layer"
  handler                = "de.mbe.tutorials.aws.serverless.moviesstats.functions.getmovieandstat.FnGetMovieAndStat::handleRequest"
  role                   = module.get_movie_and_stat_lambda_role.arn
  env = {
    MOVIES_TABLE = module.movies_table.name
    STATS_TABLE  = module.stats_table.name
  }
}

############################################################################

module movies_stats_api_gw {
  source      = "./modules/api_gateway/rest_api"
  name        = "movies_stats_api"
  description = "This is the API for the MoviesStats project"
}

module movies_resource {
  source      = "./modules/api_gateway/resource"
  rest_api_id = module.movies_stats_api_gw.id
  parent_id   = module.movies_stats_api_gw.root_resource_id
  path_part   = "movies"
}

module movie_resource {
  source      = "./modules/api_gateway/resource"
  rest_api_id = module.movies_stats_api_gw.id
  parent_id   = module.movies_resource.id
  path_part   = "{id}"
}

module get_movie_and_stat_request_method {
  source        = "./modules/api_gateway/method"
  rest_api_id   = module.movies_stats_api_gw.id
  resource_id   = module.movie_resource.id
  http_method   = "GET"
  authorization = "NONE"
}

module add_stat_request_method {
  source        = "./modules/api_gateway/method"
  rest_api_id   = module.movies_stats_api_gw.id
  resource_id   = module.movie_resource.id
  http_method   = "PATCH"
  authorization = "NONE"
}

module get_movie_and_stat_request_integration {
  source              = "./modules/api_gateway/integration"
  rest_api_id         = module.movies_stats_api_gw.id
  resource_id         = module.movie_resource.id
  http_method         = module.get_movie_and_stat_request_method.http_method
  function_invoke_arn = module.get_movie_and_stat_lambda.invoke_arn
  depends_on_method   = module.get_movie_and_stat_request_method
}

module add_stat_request_integration {
  source              = "./modules/api_gateway/integration"
  rest_api_id         = module.movies_stats_api_gw.id
  resource_id         = module.movie_resource.id
  http_method         = module.add_stat_request_method.http_method
  function_invoke_arn = module.add_stat_lambda.invoke_arn
  depends_on_method   = module.add_stat_request_method
}

module movies_stats_api_deployment {
  source                   = "./modules/api_gateway/deployment"
  rest_api_id              = module.movies_stats_api_gw.id
  stage_name               = "prod"
  depends_on_integration_1 = module.get_movie_and_stat_request_integration
  depends_on_integration_2 = module.add_stat_request_integration
}

############################################################################

module allow_movies_bucket_to_invoke_add_movies_lambda {
  source              = "./modules/lambda_permission/allow_execution_from_s3_bucket"
  bucket_arn          = module.movies_bucket.arn
  function_arn        = module.add_movies_lambda.arn
  depends_on_bucket   = module.movies_bucket
  depends_on_function = module.add_movies_lambda
}

module movies_bucket_notification {
  source              = "./modules/s3_notification/object_created"
  bucket_id           = module.movies_bucket.id
  function_arn        = module.add_movies_lambda.arn
  file_extension      = "csv"
  depends_on_function = module.add_movies_lambda
  depends_on_bucket   = module.movies_bucket
}

############################################################################

module allow_movies_stats_api_gw_to_invoke_get_movie_and_stat_lambda {
  source              = "./modules/lambda_permission/allow_execution_from_api_gateway"
  region              = var.aws_region
  account_id          = var.aws_account_id
  api_gw_id           = module.movies_stats_api_gw.id
  resource_path       = module.movie_resource.path
  function_arn        = module.get_movie_and_stat_lambda.arn
  method_http_verb    = module.get_movie_and_stat_request_method.http_method
  depends_on_function = module.get_movie_and_stat_lambda
  depends_on_api_gw   = module.movies_stats_api_gw
}

module allow_movies_stats_api_gw_to_invoke_add_stat_lambda {
  source              = "./modules/lambda_permission/allow_execution_from_api_gateway"
  region              = var.aws_region
  account_id          = var.aws_account_id
  api_gw_id           = module.movies_stats_api_gw.id
  resource_path       = module.movie_resource.path
  function_arn        = module.add_stat_lambda.arn
  method_http_verb    = module.add_stat_request_method.http_method
  depends_on_function = module.add_stat_lambda
  depends_on_api_gw   = module.movies_stats_api_gw
}

############################################################################

