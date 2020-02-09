provider "aws" {
  region     = "eu-central-1"
  access_key = "${var.AWS_ACCESS_KEY}"
  secret_key = "${var.AWS_SECRET_KEY}"
}

resource "aws_instance" "aws_btd" {
  ami = "ami-07cda0db070313c52"
  instance_type = "t2.micro"
  #security_groups   = ["extensions-buildAgents"]
}