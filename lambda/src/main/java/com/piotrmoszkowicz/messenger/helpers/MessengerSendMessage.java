package com.piotrmoszkowicz.messenger.helpers;

import java.util.HashMap;
import java.util.Map;

public class MessengerSendMessage {
    public String messaging_type;
    public Map<String, String> recipient;
    public Map<String, String> message;

    public MessengerSendMessage(String messagingType, String recipient, String message) {
        this.messaging_type = messagingType;
        this.recipient = new HashMap<>();
        this.recipient.put("id", recipient);
        this.message = new HashMap<>();
        this.message.put("text", message);
    }
}
