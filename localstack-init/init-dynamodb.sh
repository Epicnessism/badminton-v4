#!/bin/bash

echo "Creating DynamoDB table: badmintonDb"

awslocal dynamodb create-table \
    --table-name badmintonDb \
    --attribute-definitions \
        AttributeName=PK,AttributeType=S \
        AttributeName=SK,AttributeType=S \
        AttributeName=gsiPk,AttributeType=S \
        AttributeName=gsiSk,AttributeType=S \
    --key-schema \
        AttributeName=PK,KeyType=HASH \
        AttributeName=SK,KeyType=RANGE \
    --global-secondary-indexes \
        '[
            {
                "IndexName": "name-index",
                "KeySchema": [
                    {"AttributeName": "gsiPk", "KeyType": "HASH"},
                    {"AttributeName": "gsiSk", "KeyType": "RANGE"}
                ],
                "Projection": {"ProjectionType": "ALL"},
                "ProvisionedThroughput": {"ReadCapacityUnits": 5, "WriteCapacityUnits": 5}
            }
        ]' \
    --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
    --region us-east-2

echo "DynamoDB table created successfully!"

awslocal dynamodb list-tables --region us-east-2
