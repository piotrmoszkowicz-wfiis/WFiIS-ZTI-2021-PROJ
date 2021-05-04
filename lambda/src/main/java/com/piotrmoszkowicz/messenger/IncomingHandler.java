package com.piotrmoszkowicz.messenger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import com.piotrmoszkowicz.messenger.helpers.MessengerBody;
import com.piotrmoszkowicz.messenger.helpers.SqsMessengerEvent;

public class IncomingHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Lambda execution started!");

        logger.log(gson.toJson(input));

        String requestMethod = (String)((Map<String, Object>)((Map<String, Object>)input.get("requestContext")).get("http")).get("method");
        logger.log("Method: " + requestMethod);

        HashMap<String, String> headers = new HashMap<>();

        if (requestMethod.equals("GET")) {
            Map<String, String> queryParams = (Map<String, String>)input.get("queryStringParameters");

            String mode = queryParams.get("hub.mode");
            String token = queryParams.get("hub.verify_token");
            String challenge = queryParams.get("hub.challenge");

            if (!mode.isEmpty() && !token.isEmpty()) {
                if (mode.equals("subscribe") && token.equals(System.getenv("VERIFIER_TOKEN"))) {
                    logger.log("Webhook verified!");
                    return new GatewayResponse(challenge, headers, 200);
                }
            }

            return new GatewayResponse("", headers, 403);
        } else if (requestMethod.equals("POST")) {
            String body = (String)input.get("body");
            logger.log("Request body: " + body);

            MessengerBody bodyParsed = gson.fromJson(body, MessengerBody.class);

            if (bodyParsed.object.equals("page")) {
                List<SqsMessengerEvent> sqsEvents = bodyParsed.entry.stream()
                        .flatMap(e -> e.messaging.stream()
                            .map(msg -> new SqsMessengerEvent(msg.sender.get("id"), msg.message.text))
                        ).collect(Collectors.toList());

                List<SendMessageBatchRequestEntry> sqsMessageEntries = sqsEvents.stream()
                        .map(e -> new SendMessageBatchRequestEntry(UUID.randomUUID().toString(), gson.toJson(e)))
                        .collect(Collectors.toList());

                SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest()
                        .withQueueUrl(System.getenv("QUEUE_URL"))
                        .withEntries(sqsMessageEntries);

                sqs.sendMessageBatch(sendMessageBatchRequest);

                return new GatewayResponse("EVENT_RECEIVED", headers, 200);
            }

            return new GatewayResponse("EVENT_RECEIVED", headers, 404);
        }
        return new GatewayResponse("Can't handle request", headers, 500);
    }
}
