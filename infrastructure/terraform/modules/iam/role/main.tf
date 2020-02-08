resource aws_iam_role role {
  name               = var.role_name
  assume_role_policy = var.assume_role_policy_json
}

resource aws_iam_policy policy {
  name   = var.policy_name
  policy = var.policy_json
}

resource aws_iam_role_policy_attachment role_policy_attachment {
  role       = aws_iam_role.role.name
  policy_arn = aws_iam_policy.policy.arn
  depends_on = [aws_iam_role.role, aws_iam_policy.policy]
}
