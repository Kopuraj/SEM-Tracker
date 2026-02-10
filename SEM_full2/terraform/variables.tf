# Variables for Terraform Configuration

variable "aws_region" {
  description = "AWS region where resources are deployed"
  type        = string
  default     = "ap-south-1" # Mumbai region, change if different
}

variable "project_name" {
  description = "Project name for resource tagging"
  type        = string
  default     = "SEM-Tracker"
}

variable "environment" {
  description = "Environment name (Production, Staging, Development)"
  type        = string
  default     = "Production"
}

variable "instance_type" {
  description = "EC2 instance type (must be Free Tier eligible)"
  type        = string
  default     = "t3.micro" # Free Tier eligible, change to t2.micro if needed
  
  validation {
    condition     = contains(["t2.micro", "t3.micro"], var.instance_type)
    error_message = "Instance type must be t2.micro or t3.micro for Free Tier eligibility."
  }
}

variable "ec2_ami_id" {
  description = "AMI ID of your existing EC2 instance (get from AWS Console)"
  type        = string
  default     = "ami-0dee22c13ea7a9a67" # Ubuntu 22.04 LTS in ap-south-1, update with your actual AMI
}

variable "key_pair_name" {
  description = "Name of the SSH key pair for EC2 access"
  type        = string
  default     = "your-key-pair-name" # Replace with your actual key pair name
}

# Cost tracking variables
variable "owner" {
  description = "Owner of the resources for cost allocation"
  type        = string
  default     = "SEM-Tracker-Team"
}

variable "cost_center" {
  description = "Cost center for billing purposes"
  type        = string
  default     = "Development"
}
