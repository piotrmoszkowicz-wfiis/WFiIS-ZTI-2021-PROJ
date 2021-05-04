package com.piotrmoszkowicz.common;

import com.piotrmoszkowicz.admin.AdminRulesConstruct;
import software.amazon.awscdk.core.CfnOutput;
import software.amazon.awscdk.core.CfnOutputProps;
import software.amazon.awscdk.core.Construct;

import software.amazon.awscdk.services.appsync.*;

import software.amazon.awscdk.services.cognito.UserPool;
import software.amazon.awscdk.services.dynamodb.Table;

public class AppSyncConstruct extends Construct {
    public AppSyncConstruct(final Construct scope, final String id, final UserPool userPool, final Table dynamoTable) {
        super(scope, id);

        var graphQlApi = new GraphqlApi(this, "GraphQLApu", GraphqlApiProps.builder()
                .authorizationConfig(AuthorizationConfig.builder()
                        .defaultAuthorization(AuthorizationMode.builder()
                                .authorizationType(AuthorizationType.USER_POOL)
                                .userPoolConfig(UserPoolConfig.builder()
                                        .userPool(userPool)
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .logConfig(LogConfig.builder()
                        .excludeVerboseContent(false)
                        .fieldLogLevel(FieldLogLevel.ALL)
                        .build()
                )
                .schema(Schema.fromAsset("./src/main/resources/schema.graphql"))
                .name("Messenger-GraphQLApi")
                .xrayEnabled(true)
                .build()
        );

        var adminRulesConstruct = new AdminRulesConstruct(this, "AdminRulesConstruct", graphQlApi, dynamoTable);

        // ========================================================================
        // Exports
        // ========================================================================
        new CfnOutput(this, "GraphQLUrlExport", CfnOutputProps.builder()
                .exportName("graphqlUrl")
                .value(graphQlApi.getGraphqlUrl())
                .build()
        );

        new CfnOutput(this, "GraphQLRegionExport", CfnOutputProps.builder()
                .exportName("graphqlRegion")
                .value("eu-central-1")
                .build()
        );
    }
}
