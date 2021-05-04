package com.piotrmoszkowicz.common;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.RemovalPolicy;

import software.amazon.awscdk.services.dynamodb.*;

public class DynamoDBConstruct extends Construct {
    public final Table dynamoTable;

    public DynamoDBConstruct(final Construct scope, final String id) {
        super(scope, id);

        dynamoTable = new Table(this, "DynamoTable", TableProps.builder()
                .partitionKey(Attribute.builder()
                        .name("PK")
                        .type(AttributeType.STRING)
                        .build()
                )
                .sortKey(Attribute.builder()
                        .name("SK")
                        .type(AttributeType.STRING)
                        .build()
                )
                .removalPolicy(RemovalPolicy.DESTROY)
                .tableName("DynamoTable")
                .build()
        );
    }
}
