package com.piotrmoszkowicz.messenger;

import com.amazonaws.AmazonServiceException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.piotrmoszkowicz.messenger.helpers.MessengerSendMessage;
import com.piotrmoszkowicz.messenger.helpers.SqsMessengerEvent;

public class OutgoingHandler implements RequestHandler<SQSEvent, Object> {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Lambda execution started!");

        String messengerResponseText = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        SqsMessengerEvent messengerEvent = gson.fromJson(event.getRecords().get(0).getBody(), SqsMessengerEvent.class);
        logger.log("Msg: " + messengerEvent.getMessage() + " SNDR: " + messengerEvent.getSenderId());

        try {
            Map<String, AttributeValue> key = new HashMap<>();
            key.put("PK", new AttributeValue("RULES"));
            key.put("SK", new AttributeValue("RULE#" + messengerEvent.getMessage()));

            GetItemRequest request = new GetItemRequest()
                    .withKey(key)
                    .withTableName(System.getenv("DYNAMO_TABLE"));

            Map<String, AttributeValue> rule = dynamoDb.getItem(request).getItem();

            if (rule != null) {
                logger.log("Found rule in DB");
                messengerResponseText = rule.get("outgoingText").getS();
            }
        } catch (Exception e) {
            logger.log(e.getMessage());
        }

        logger.log("Sending response...");
        MessengerSendMessage responseToMessenger = new MessengerSendMessage("RESPONSE", messengerEvent.getSenderId(), messengerResponseText);

        try {
            logger.log("Trying POST request...");
            URL url = new URL("https://graph.facebook.com/v10.0/me/messages?access_token=" + System.getenv("MESSENGER_TOKEN"));
            HttpURLConnection con = (HttpURLConnection)url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = gson.toJson(responseToMessenger).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                logger.log("MESSENGER RESPONSE " + response);
            }
        } catch (Exception e) {
            logger.log(e.getMessage());
        }


        return "";
    }
}
