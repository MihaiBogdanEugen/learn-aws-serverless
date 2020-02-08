## help: Prints this help message
help:
	@echo "Usage: \n"
	@sed -n 's/^##//p' ${MAKEFILE_LIST} | column -t -s ':' |  sed -e 's/^/ /'

## clean: Clean the files and directories generated during build
clean:
	cd app/movies-stats && ./gradlew clean

## test: Run the tests
test: clean
	cd app/movies-stats && ./gradlew test

## package: Build and package the source code into an uber-jar
package: test
	cd app/movies-stats && \
	./gradlew :functions:add-movies:build && \
	./gradlew :functions:add-stat:build && \
	./gradlew :functions:get-movie-and-stat:build

## format: Rewrites config files to canonical format
fmt: check-terraform
	cd infrastructure/terraform && terraform fmt -recursive

## validate: Validates the Terraform files
validate: check-terraform init fmt
	cd infrastructure/terraform && terraform validate

## init: Initialize a Terraform working directory
init: check-terraform
	cd infrastructure/terraform && terraform init

## plan: Generate and show an execution plan
plan: check-terraform init validate package
	cd infrastructure/terraform && terraform plan

## apply: Build or change infrastructure
apply: check-terraform plan
	cd infrastructure/terraform && terraform apply -auto-approve

## destroy: Destroy Terraform-managed infrastructure
destroy: check-terraform init
	cd infrastructure/terraform && terraform destroy -auto-approve

## check-terraform: Locate terraform in the current user's path (checking if it is installed or not)
check-terraform:
ifeq (, $(shell which terraform))
	$(error "terraform is NOT installed correctly. More information: https://www.terraform.io/downloads.html")
endif

.PHONY: help clean test package check-updates fmt validate init plan apply destroy check-terraform