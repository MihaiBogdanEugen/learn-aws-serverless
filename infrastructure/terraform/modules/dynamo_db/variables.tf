variable name {
  type = string
}

variable billing_mode {
  default = "PROVISIONED"
  type    = string
}

variable read_capacity {
  default = 5
  type    = number
}

variable write_capacity {
  default = 5
  type    = number
}

variable hash_key_name {
  type    = string
  default = "id"
}

variable hash_key_type {
  type    = string
  default = "S"
}

variable enable_encryption_at_rest {
  default = false
}