package com.piotrmoszkowicz.messenger;

import software.amazon.awscdk.core.*;

import software.amazon.awscdk.services.apigatewayv2.*;
import software.amazon.awscdk.services.apigatewayv2.integrations.*;
import software.amazon.awscdk.services.lambda.*;
import software.amazon.awscdk.services.lambda.eventsources.*;
import software.amazon.awscdk.services.sqs.*;
import software.amazon.awscdk.services.ssm.*;

import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.Map;
import java.util.List;

public class MessengerConstruct extends Construct {
    public MessengerConstruct(final Construct scope, final String id, final Table dynamoTable) {
        super(scope, id);

        // ========================================================================
        // Resource: AWS Parameter Store
        // ========================================================================

        // Purpose: Get secrets for Messenger handling
        String messengerToken = StringParameter.fromStringParameterAttributes(this, "SSM-MessengerToken", StringParameterAttributes.builder()
                .parameterName("/facebook/messenger-token")
                .build()
        ).getStringValue();

        String messengerVerifier = StringParameter.fromStringParameterAttributes(this, "SSM-MessengerVerifier", StringParameterAttributes.builder()
                .parameterName("/facebook/messenger-verifier")
                .build()
        ).getStringValue();

        // ========================================================================
        // Resource: AWS Api Gateway
        // ========================================================================

        // Purpose: Create HTTP endpoints for Webhook
        var httpApi = new HttpApi(this, "MessengerApiGateway", HttpApiProps.builder()
                .createDefaultStage(true)
                .build()
        );

        // ========================================================================
        // Resource: AWS SQS
        // ========================================================================

        // Purpose: Queues for webhook handling

        var messengerDeadLetterQueue = new Queue(this, "MessengerDQL", QueueProps.builder()
                .queueName("MessengerDeadLetterQueue")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build()
        );

        var messengerQueue = new Queue(this, "MessengerQueue", QueueProps.builder()
                .deadLetterQueue(DeadLetterQueue.builder()
                        .queue(messengerDeadLetterQueue)
                        .maxReceiveCount(5)
                        .build()
                )
                .queueName("MessengerQueue")
                .removalPolicy(RemovalPolicy.DESTROY)
                .build()
        );

        // ========================================================================
        // Resource: AWS Lambda
        // ========================================================================

        // Purpose: Incoming request handling

        var messengerIncomingHandlerLambda = new Function(this, "MessengerIncomingHandlerLambda", FunctionProps.builder()
                .code(Code.fromAsset("../lambda/target/lambda-0.1.jar"))
                .handler("com.piotrmoszkowicz.messenger.IncomingHandler")
                .runtime(Runtime.JAVA_8)
                .environment(Map.of(
                        "QUEUE_URL", messengerQueue.getQueueUrl(),
                        "VERIFIER_TOKEN", messengerVerifier
                ))
                .memorySize(512)
                .timeout(Duration.seconds(30))
                .build()
        );

        messengerQueue.grantSendMessages(messengerIncomingHandlerLambda);

        var messengerIncomingHandlerIntegration = new LambdaProxyIntegration(LambdaProxyIntegrationProps.builder()
                .handler(messengerIncomingHandlerLambda)
                .build()
        );

        httpApi.addRoutes(AddRoutesOptions.builder()
                .integration(messengerIncomingHandlerIntegration)
                .methods(List.of(HttpMethod.GET, HttpMethod.POST))
                .path("/message")
                .build()
        );

        // ========================================================================
        // Resource: AWS Lambda
        // ========================================================================

        // Purpose: Outgoing request handling

        var messengerOutgoingHandlerLambda = new Function(this, "MessengerOutgoingHandlerLambda", FunctionProps.builder()
                .code(Code.fromAsset("../lambda/target/lambda-0.1.jar"))
                .handler("com.piotrmoszkowicz.messenger.OutgoingHandler")
                .runtime(Runtime.JAVA_8)
                .environment(Map.of(
                        "DYNAMO_TABLE", dynamoTable.getTableName(),
                        "MESSENGER_TOKEN", messengerToken
                ))
                .memorySize(512)
                .timeout(Duration.seconds(30))
                .build()
        );

        dynamoTable.grantReadData(messengerOutgoingHandlerLambda);
        messengerQueue.grantConsumeMessages(messengerOutgoingHandlerLambda);

        messengerOutgoingHandlerLambda.addEventSource(
                new SqsEventSource(messengerQueue, SqsEventSourceProps.builder()
                        .batchSize(1)
                        .build()
                )
        );

        // ========================================================================
        // Exports
        // ========================================================================
        new CfnOutput(this, "MessengerApiGatewayExport", CfnOutputProps.builder()
                .exportName("messengerApiGateway")
                .value(httpApi.getUrl())
                .build()
        );
    }
}
