# Artifacts included
1. Source code in maven project
2. OpenAPI spec in spec folder
3. Postman collection in spec folder
4. Dockerfile
5. Deployment yaml for Kubernetes

# Run all tests including integration tests

> 		./mvnw verify

# Deliberate choices (mainly due to time constraints):

1. Did not add extra columns such as name, description, etc.
1. Denormalized table for discount - alternatives are to normalize, 
keep variable data (discount-specific columns) as a JSON object, or use noSQL
1. Did not add authentication or authorization
1. Used in-mem DB for simplicity
1. Did not standardize design for returned objects.
1. Separation-of-concerns via DTOs is demonstrated for the controller layer only. The same can be implemented for each layer with the corresponding mappers.
1. Code coverage is at 98%. A large number of integration tests are included. Mock testing is demonstrated. More tests can be added. 

# Containerize

## Build docker image

>		./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=pawan/rest-discount

## Run image

>		docker run -dp 8080:8080 pawan/rest-discount

## Run pre-built image (from docker hub)

Note that the tag is different due to docker hub user ID.

>		docker run -dp 8080:8080 ilaichi/rest-discount

## Verify

View pre-loaded discounts at http://localhost:8080/api/v1/discounts (use hostname/IP if not localhost)

## Stop container

>		docker stop <container_id>

# Run on Kubernetes

## Deploy 
>		kubectl create namespace demo

>		kubectl apply -f rest-discount.yaml

The service is exposed on nodeport 30001

## Verify

View pre-loaded discounts at http://localhost:30001/api/v1/discounts (Use node IP if not localhost)

## Clean up

>		kubectl get all -n demo

>		kubectl delete service rest-discount-entrypoint -n demo

>		kubectl delete deploy rest-discount -n demo
