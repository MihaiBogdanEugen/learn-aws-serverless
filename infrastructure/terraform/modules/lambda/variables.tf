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
  default = 256
}

variable timeout {
  type    = number
  default = 60
}

variable runtime {
  type    = string
  default = "java11"
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