variable function_arn {
  type = string
}

variable bucket_arn {
  type = string
}

variable depends_on_function {
  type = any
}

variable depends_on_bucket {
  type = any
}