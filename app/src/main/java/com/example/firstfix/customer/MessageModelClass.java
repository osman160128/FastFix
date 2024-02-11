package com.example.firstfix.customer;

public class MessageModelClass {
    String message,type,from;

    public MessageModelClass(){

    }

    public MessageModelClass(String message, String type, String from) {
        this.message = message;
        this.type = type;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
