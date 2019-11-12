provider "aws" {
  region     = "us-west-2"
  access_key = "${var.AWS_ACCESS_KEY}"
  secret_key = "${var.AWS_SECRET_KEY}"
}

resource "aws_instance" "aws_btd" {
  ami = "ami-082b5a644766e0e6f"
  instance_type = "t2.micro"


  #Reading the subnet_id from variables.tf
  subnet_id = "subnet-e02dc38b"

  #Security group
  #security_groups = ["sg-ccb3ebb"]
}