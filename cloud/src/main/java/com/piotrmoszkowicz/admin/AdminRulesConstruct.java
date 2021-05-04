package com.piotrmoszkowicz.admin;

import software.amazon.awscdk.core.Construct;

import software.amazon.awscdk.services.appsync.*;

import software.amazon.awscdk.services.dynamodb.Table;

public class AdminRulesConstruct extends Construct {
    public AdminRulesConstruct(final Construct scope, final String id, final GraphqlApi graphqlApi, final Table dynamoTable) {
        super(scope, id);

        // ========================================================================
        // Resource: AWS Appsync Data Source
        // ========================================================================

        // Purpose: DynamoDB Data Source
        var adminRulesDataSource = new DynamoDbDataSource(this, "AdminRulesDataSource", DynamoDbDataSourceProps.builder()
                .api(graphqlApi)
                .table(dynamoTable)
                .build()
        );

        dynamoTable.grantReadWriteData(adminRulesDataSource);

        // ========================================================================
        // Resource: AWS Appsync Resolver
        // ========================================================================

        // Purpose: Add new rule
        var addNewRuleResolver = new Resolver(this, "addNewRuleResolver", ResolverProps.builder()
                .api(graphqlApi)
                .dataSource(adminRulesDataSource)
                .fieldName("addNewRule")
                .requestMappingTemplate(MappingTemplate.fromFile("./src/main/java/com/piotrmoszkowicz/admin/templates/addNewRule.request.vtl"))
                .responseMappingTemplate(MappingTemplate.fromFile("./src/main/java/com/piotrmoszkowicz/admin/templates/addNewRule.response.vtl"))
                .typeName("Mutation")
                .build()
        );

        // ========================================================================
        // Resource: AWS Appsync Resolver
        // ========================================================================

        // Purpose: Delete rule
        var deleteRuleResolver = new Resolver(this, "deleteRuleResolver", ResolverProps.builder()
                .api(graphqlApi)
                .dataSource(adminRulesDataSource)
                .fieldName("deleteRule")
                .requestMappingTemplate(MappingTemplate.fromFile("./src/main/java/com/piotrmoszkowicz/admin/templates/deleteRule.request.vtl"))
                .responseMappingTemplate(MappingTemplate.fromString("true"))
                .typeName("Mutation")
                .build()
        );

        // ========================================================================
        // Resource: AWS Appsync Resolver
        // ========================================================================

        // Purpose: Get rules
        var getRulesResolver = new Resolver(this, "getRulesResolver", ResolverProps.builder()
                .api(graphqlApi)
                .dataSource(adminRulesDataSource)
                .fieldName("getRules")
                .requestMappingTemplate(MappingTemplate.fromFile("./src/main/java/com/piotrmoszkowicz/admin/templates/getRules.request.vtl"))
                .responseMappingTemplate(MappingTemplate.dynamoDbResultList())
                .typeName("Query")
                .build()
        );
    }
}
