# Serverless AWS Java GraphQL

Starter repo for an AWS Lambda GraphQL server built 
in Java with Serverless.

### Prerequisites

1. Java 11
2. [Gradle](https://gradle.org/install/)
3. [Serverless](https://www.serverless.com/framework/docs/providers/aws/guide/installation/)

## Building
To build the service, run the following command:
`./gradlew build`

This will place a ZIP archive in `./build/distributions`. If you change the name of the repo,
make sure you update the name of the ZIP in serverless.yml since gradle
uses the current directory name to name the ZIP.

## Deploying
Make sure your AWS keys are in scope:
```bash
export AWS_ACCESS_KEY_ID=<IAM Key>
export AWS_SECRET_ACCESS_KEY=<IAM Secret>
```

Deploy with
`serverless deploy`. This will output something like
```text
Serverless: Stack update finished...
Service Information
service: aws-java-graphql-lambda
stage: dev
region: us-east-1
stack: aws-java-graphql-lambda-dev
resources: 17
api keys:
  None
endpoints:
  ANY - https://<hash>.execute-api.us-east-1.amazonaws.com/dev/graphql
  GET - https://<hash>.execute-api.us-east-1.amazonaws.com/dev/playground
functions:
  graphql: aws-java-graphql-lambda-dev-graphql
  playground: aws-java-graphql-lambda-dev-playground
layers:
  None
Serverless: Removing old service artifacts from S3...
```

This will create two functions, one for the GraphQL playground
and one for the actual API endpoint. When you open the 
playground, make sure that the endpoint in the
URL input is /graphql.