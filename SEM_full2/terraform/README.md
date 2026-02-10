# SEM-Tracker Terraform Configuration

This directory contains Terraform infrastructure-as-code for the SEM-Tracker project running on AWS.

## ⚠️ IMPORTANT: This Manages EXISTING Infrastructure

**This Terraform configuration is designed to IMPORT and manage your existing AWS resources, NOT create new ones.**

Your existing setup:
- EC2 Instance: `65.2.6.115`
- Running application via Jenkins CI/CD
- Docker containers (backend + frontend)

## Prerequisites

Before using Terraform, you need to install:

### 1. Install Terraform

**Windows (PowerShell - Run as Administrator):**
```powershell
# Using Chocolatey (recommended)
choco install terraform

# OR download manually from: https://www.terraform.io/downloads
# Extract and add to PATH
```

**Verify installation:**
```powershell
terraform --version
# Should show: Terraform v1.x.x
```

### 2. Install AWS CLI

**Windows:**
```powershell
# Download from: https://aws.amazon.com/cli/
# Or using Chocolatey:
choco install awscli
```

**Configure AWS CLI:**
```powershell
aws configure
# Enter your:
# - AWS Access Key ID
# - AWS Secret Access Key
# - Default region (e.g., ap-south-1)
# - Output format: json
```

### 3. Gather Your AWS Resource Information

Log into AWS Console and collect:

1. **EC2 Instance ID**
   - Go to EC2 Dashboard → Instances
   - Find instance with IP `65.2.6.115`
   - Copy Instance ID (format: `i-0123456789abcdef0`)

2. **AMI ID**
   - In EC2 Instance details, find "AMI ID"
   - Copy it (format: `ami-0123456789abcdef0`)

3. **Instance Type**
   - Check "Instance type" (should be `t2.micro` or `t3.micro`)

4. **Key Pair Name**
   - Find "Key pair name" in instance details

5. **Security Group ID**
   - Find "Security groups" in instance details
   - Copy the Security Group ID (format: `sg-0123456789abcdef0`)

6. **AWS Region**
   - Check which region your instance is in (e.g., `ap-south-1`, `us-east-1`)

## Setup Instructions

### Step 1: Configure Your Values

1. Copy the example file:
   ```powershell
   cd terraform
   Copy-Item terraform.tfvars.example terraform.tfvars
   ```

2. Edit `terraform.tfvars` and replace the placeholder values:
   ```hcl
   aws_region    = "ap-south-1"           # Your actual region
   instance_type = "t3.micro"             # or t2.micro
   ec2_ami_id    = "ami-YOUR-ACTUAL-AMI"  # From AWS Console
   key_pair_name = "your-key-name"        # From AWS Console
   ```

### Step 2: Initialize Terraform

```powershell
cd terraform
terraform init
```

This downloads the AWS provider plugin.

### Step 3: Import Existing Resources

**CRITICAL: This step connects Terraform to your EXISTING resources without creating new ones.**

```powershell
# Import EC2 instance
terraform import aws_instance.sem_tracker_ec2 i-YOUR-INSTANCE-ID

# Import security group
terraform import aws_security_group.sem_tracker_sg sg-YOUR-SECURITY-GROUP-ID
```

### Step 4: Verify No Changes

```powershell
terraform plan
```

**Expected output:**
```
No changes. Your infrastructure matches the configuration.
```

**⚠️ If you see "will create" or "will modify":**
- STOP immediately
- Review `terraform.tfvars` - values might not match your actual resources
- Ask for help before proceeding

### Step 5: View Your Infrastructure

```powershell
terraform show
terraform output
```

This displays your managed infrastructure and cost tracking tags.

## Cost Tracking

After importing, your resources will be tagged with:
- `Project = "SEM-Tracker"`
- `Environment = "Production"`
- `ManagedBy = "Terraform"`

To view costs in AWS:
1. Go to AWS Cost Explorer
2. Enable "Cost Allocation Tags"
3. Filter by tag: `Project = SEM-Tracker`
4. See monthly breakdown by service

## Safety Features

The configuration includes:
- **`prevent_destroy = true`**: Prevents accidental deletion of your EC2 instance
- **Instance type validation**: Only allows Free Tier eligible types (t2.micro, t3.micro)
- **Default tags**: Automatically adds cost tracking tags to all resources

## Free Tier Compliance

Your setup stays within AWS Free Tier:
- ✅ 1 instance running 24/7 = 744 hours/month (under 750 hour limit)
- ✅ t3.micro or t2.micro instance type (Free Tier eligible)
- ✅ Up to 30 GB storage (Free Tier: 30 GB)
- ⚠️ Monitor data transfer (Free Tier: 15 GB outbound/month)

## Common Commands

```powershell
# View current infrastructure state
terraform show

# View outputs (IP, instance ID, etc.)
terraform output

# Check for configuration changes
terraform plan

# Refresh state from AWS (doesn't make changes)
terraform refresh

# Format Terraform files
terraform fmt
```

## Important Notes

1. **Never run `terraform apply`** until you've successfully imported and verified with `terraform plan`
2. **Add `terraform.tfvars` to `.gitignore`** - it contains sensitive information
3. **Keep `terraform.tfstate` secure** - it contains your infrastructure details
4. **Regular backups** - Back up `.tfstate` files (they're in `.terraform/` directory)

## Troubleshooting

**Error: "Resource already exists"**
- You tried to apply without importing first
- Solution: Use `terraform import` commands above

**Error: "Provider configuration not found"**
- Run `terraform init` first

**Error: "Error acquiring state lock"**
- Another Terraform process is running
- Wait for it to complete or delete `.terraform.tfstate.lock.info`

## Next Steps

After successful import:
1. Enable Cost Explorer in AWS Console
2. Set up budget alerts ($10/month recommended)
3. Review security group rules
4. Document any future infrastructure changes in this Terraform config

## Files in This Directory

- `main.tf` - Main infrastructure configuration
- `variables.tf` - Variable definitions
- `outputs.tf` - Output values after Terraform runs
- `terraform.tfvars` - Your specific values (DON'T commit to Git!)
- `terraform.tfvars.example` - Template for reference
- `.terraform/` - Terraform working directory (auto-generated)
- `terraform.tfstate` - Current infrastructure state (auto-generated, keep secure)

## Support

If you encounter issues:
1. Check `terraform plan` output carefully
2. Verify all values in `terraform.tfvars` match AWS Console
3. Ensure AWS CLI is configured: `aws ec2 describe-instances`
4. Review Terraform logs with: `TF_LOG=DEBUG terraform plan`
