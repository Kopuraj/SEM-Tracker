# Quick Start Script for Terraform Setup
# Run this script after installing Terraform and AWS CLI

Write-Host "=== SEM-Tracker Terraform Setup ===" -ForegroundColor Cyan
Write-Host ""

# Check prerequisites
Write-Host "Checking prerequisites..." -ForegroundColor Yellow

# Check Terraform
try {
    $tfVersion = terraform --version 2>&1 | Select-Object -First 1
    Write-Host "✓ Terraform installed: $tfVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ Terraform not found. Please install from: https://www.terraform.io/downloads" -ForegroundColor Red
    Write-Host "  Or run: choco install terraform" -ForegroundColor Yellow
    exit 1
}

# Check AWS CLI
try {
    $awsVersion = aws --version 2>&1
    Write-Host "✓ AWS CLI installed: $awsVersion" -ForegroundColor Green
} catch {
    Write-Host "✗ AWS CLI not found. Please install from: https://aws.amazon.com/cli/" -ForegroundColor Red
    Write-Host "  Or run: choco install awscli" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "=== Next Steps ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Configure AWS CLI (if not done):" -ForegroundColor Yellow
Write-Host "   aws configure" -ForegroundColor White
Write-Host ""
Write-Host "2. Gather your AWS resource information:" -ForegroundColor Yellow
Write-Host "   - EC2 Instance ID (from AWS Console)" -ForegroundColor White
Write-Host "   - AMI ID" -ForegroundColor White
Write-Host "   - Instance Type (t2.micro or t3.micro)" -ForegroundColor White
Write-Host "   - Key Pair Name" -ForegroundColor White
Write-Host "   - Security Group ID" -ForegroundColor White
Write-Host "   - AWS Region" -ForegroundColor White
Write-Host ""
Write-Host "3. Copy and configure terraform.tfvars:" -ForegroundColor Yellow
Write-Host "   cd terraform" -ForegroundColor White
Write-Host "   Copy-Item terraform.tfvars.example terraform.tfvars" -ForegroundColor White
Write-Host "   notepad terraform.tfvars" -ForegroundColor White
Write-Host ""
Write-Host "4. Initialize Terraform:" -ForegroundColor Yellow
Write-Host "   terraform init" -ForegroundColor White
Write-Host ""
Write-Host "5. Import existing resources:" -ForegroundColor Yellow
Write-Host "   terraform import aws_instance.sem_tracker_ec2 i-YOUR-INSTANCE-ID" -ForegroundColor White
Write-Host "   terraform import aws_security_group.sem_tracker_sg sg-YOUR-SG-ID" -ForegroundColor White
Write-Host ""
Write-Host "6. Verify (should show 'No changes'):" -ForegroundColor Yellow
Write-Host "   terraform plan" -ForegroundColor White
Write-Host ""
Write-Host "See terraform/README.md for detailed instructions!" -ForegroundColor Green
