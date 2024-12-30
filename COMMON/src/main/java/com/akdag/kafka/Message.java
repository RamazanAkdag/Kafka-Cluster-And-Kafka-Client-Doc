package com.akdag.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {

    private String id;

    private String content;

    public Message() {}

    public Message(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}