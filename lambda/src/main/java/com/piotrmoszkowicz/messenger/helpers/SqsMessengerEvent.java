package com.piotrmoszkowicz.messenger.helpers;

public class SqsMessengerEvent {
    private final String senderId;
    private final String message;

    public SqsMessengerEvent(String senderId, String message) {
        this.senderId = senderId;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }
}
