package com.piotrmoszkowicz.messenger.helpers;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class MessengerBody {
    public static class MessengerEntry {
        public static class Messaging {
            public static class Message {
                public String mid;
                public String text;
            }

            public Map<String, String> sender;
            public Map<String, String> recipient;
            public BigInteger timestamp;
            public Message message;
        }

        public String id;
        public BigInteger time;
        public List<Messaging> messaging;
    }

    public String object;
    public List<MessengerEntry> entry;
}
