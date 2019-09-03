provider "aws" {
  region     = "us-west-2"
  access_key = "${var.AWS_ACCESS_KEY}"
  secret_key = "${var.AWS_SECRET_KEY}"
  #shared_credentials_file = "creds"
  #profile                 = "appdces"
}

resource "aws_instance" "aws_btd" {
  ami = "ami-082b5a644766e0e6f"
  instance_type = "t2.micro"
  tags {
    Name = "btd-ec2"
   }

  #Reading the subnet_id from variables.tf
  #subnet_id = "subnet-3c3bbb75"

  #Security group
  #security_groups = ["sg-ccb3ebb"]
}