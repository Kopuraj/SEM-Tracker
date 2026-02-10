# Terraform Configuration for SEM-Tracker
# This manages your EXISTING AWS infrastructure (imports, doesn't create new)

terraform {
  required_version = ">= 1.0"
  
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

# AWS Provider Configuration
provider "aws" {
  region = var.aws_region
  
  default_tags {
    tags = {
      Project     = var.project_name
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

# Data source to get latest Ubuntu AMI (for reference)
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# Security Group for SEM-Tracker
# NOTE: This will be imported from your existing security group
resource "aws_security_group" "sem_tracker_sg" {
  name        = "sem-tracker-security-group"
  description = "Security group for SEM-Tracker application"
  
  # SSH access
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "SSH access"
  }
  
  # HTTP access
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTP access"
  }
  
  # HTTPS access
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "HTTPS access"
  }
  
  # Backend API (if needed)
  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Backend API"
  }
  
  # Outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
    description = "Allow all outbound"
  }
  
  tags = {
    Name = "SEM-Tracker-SG"
  }
}

# EC2 Instance - SEM-Tracker Production Server
# IMPORTANT: This resource will be IMPORTED from your existing instance
# It will NOT create a new instance
resource "aws_instance" "sem_tracker_ec2" {
  # These values should match your EXISTING instance
  ami           = var.ec2_ami_id
  instance_type = var.instance_type
  key_name      = var.key_pair_name
  
  vpc_security_group_ids = [aws_security_group.sem_tracker_sg.id]
  
  # Root volume configuration (Free Tier: up to 30 GB)
  root_block_device {
    volume_size           = 30
    volume_type           = "gp3"
    delete_on_termination = true
    
    tags = {
      Name = "SEM-Tracker-Root-Volume"
    }
  }
  
  # User data script (only runs on first boot, won't affect existing instance)
  user_data = <<-EOF
              #!/bin/bash
              # This script only runs on NEW instances
              # Since we're importing, this won't execute
              echo "Instance managed by Terraform"
              EOF
  
  # Enable detailed monitoring (Free Tier includes basic monitoring)
  monitoring = false
  
  tags = {
    Name        = "SEM-Tracker-Production"
    Application = "SEM-Tracker"
    Component   = "Application-Server"
  }
  
  lifecycle {
    # Prevent accidental destruction of the instance
    prevent_destroy = true
    
    # Ignore changes to user_data after import
    ignore_changes = [user_data, ami]
  }
}

# Elastic IP (if you're using one)
# Comment this out if you're not using an Elastic IP
# resource "aws_eip" "sem_tracker_eip" {
#   instance = aws_instance.sem_tracker_ec2.id
#   domain   = "vpc"
#   
#   tags = {
#     Name = "SEM-Tracker-EIP"
#   }
# }
