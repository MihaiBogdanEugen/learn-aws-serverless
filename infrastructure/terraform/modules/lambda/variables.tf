variable description {
  type = string
}

variable filename {
  type = string
}

variable source_code_hash {
  type = string
}

variable function_name {
  type = string
}

variable handler {
  type = string
}

variable memory_size {
  type    = number
  default = 2048
}

variable timeout {
  type    = number
  default = 300
}

variable runtime {
  type = string
}

variable role {
  type = string
}

variable "env" {
  type = map(string)
}

variable "tracing_config_mode" {
  type    = string
  default = "Active"
}


variable layer_name {
  type = string
}

variable layer_filename {
  type = string
}

variable layer_source_code_hash {
  type = string
}

variable provisioned_concurrent_executions {
  type = number
}