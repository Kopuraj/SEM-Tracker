# Outputs - Display important information after Terraform runs

output "ec2_instance_id" {
  description = "ID of the EC2 instance"
  value       = aws_instance.sem_tracker_ec2.id
}

output "ec2_public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = aws_instance.sem_tracker_ec2.public_ip
}

output "ec2_instance_type" {
  description = "Instance type (for Free Tier verification)"
  value       = aws_instance.sem_tracker_ec2.instance_type
}

output "security_group_id" {
  description = "Security group ID"
  value       = aws_security_group.sem_tracker_sg.id
}

output "cost_tracking_tags" {
  description = "Tags applied for cost tracking"
  value = {
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

# Estimated monthly cost reminder
output "free_tier_reminder" {
  description = "Free Tier eligibility reminder"
  value       = "Instance type ${aws_instance.sem_tracker_ec2.instance_type} is Free Tier eligible for 750 hours/month"
}
