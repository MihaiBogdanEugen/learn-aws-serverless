## help: Prints this help message
help:
	@echo "Usage: \n"
	@sed -n 's/^##//p' ${MAKEFILE_LIST} | column -t -s ':' |  sed -e 's/^/ /'

## clean: Clean the files and directories generated during build
clean:
	rm -rfd infrastructure/terraform/.terraform/ && \
	rm -f infrastructure/terraform/terraform.tfstate && \
	rm -f infrastructure/terraform/terraform.tfstate.backup && \
	cd app/movies-stats && ./gradlew clean

## test: Run the tests
test:
	cd app/movies-stats && \
	./gradlew clean && \
	./gradlew test

## package: Build and package the source code into an uber-zip
package: test
	cd app/movies-stats && \
	./gradlew :add-movies:build && \
	./gradlew :add-stat:build && \
	./gradlew :get-movie-and-stat:build

## format: Rewrites Terraform config files to canonical format
fmt: check-terraform
	cd infrastructure/terraform && terraform fmt -recursive

## init: Initialize a Terraform working directory
init: check-terraform
	cd infrastructure/terraform && terraform init

## validate: Validates the Terraform files
validate: package check-terraform
	cd infrastructure/terraform && terraform validate

## plan: Generate and show a Terraform execution plan
plan: check-terraform
	cd infrastructure/terraform && terraform plan

## apply: Build or change Terraform infrastructure
apply: check-tf-var-aws-region check-tf-var-aws-account-id check-terraform
	cd infrastructure/terraform && terraform apply -auto-approve

## destroy: Destroy Terraform-managed infrastructure
destroy: check-terraform
	cd infrastructure/terraform && terraform destroy -auto-approve

## output: Read an output from a Terraform state file
output: check-terraform
	cd infrastructure/terraform && terraform output

## check-terraform: Locate terraform in the current user's path (checking if it is installed or not)
check-terraform:
ifeq (, $(shell which terraform))
	$(error "terraform is NOT installed correctly. More information: https://www.terraform.io/downloads.html")
endif

## check-tf-var-aws-region: Ensure the TF_VAR_aws_region environment variable is defined
check-tf-var-aws-region:
ifndef TF_VAR_aws_region
	$(error "TF_VAR_aws_region is undefined")
endif

## check-tf-var-aws-account-id: Ensure the TF_VAR_aws_account_id environment variable is defined
check-tf-var-aws-account-id:
ifndef TF_VAR_aws_account_id
	$(error "TF_VAR_aws_account_id is undefined")
endif

.PHONY: help clean test package fmt init validate plan apply destroy output check-terraform check-tf-var-aws-region check-tf-var-aws-account-id