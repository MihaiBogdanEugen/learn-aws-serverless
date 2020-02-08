variable function_arn {
  type = string
}

variable region {
  type = string
}

variable account_id {
  type = string
}


variable api_gw_id {
  type = string
}

variable method_http_verb {
  type = string
}

variable resource_path {
  type = string
}

variable depends_on_function {
  type = any
}

variable depends_on_api_gw {
  type = any
}