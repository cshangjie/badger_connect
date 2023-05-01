package com.example.badgerconnect.Model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chat {


    private String sender;
    private String receiver;
    private String message;
    private String date;


    // Add this constructor to deserialize from JSON string
    public Chat(String json) throws IOException {
        System.out.println("json1 is " + json);
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        mapper.setDateFormat(dateFormat);
        Chat message = mapper.readValue("\""+json + "\"", Chat.class);
        this.date = message.date;
        this.sender = message.sender;
        this.message = message.message;
    }

    public Chat(String sender, String receiver, String message, String date) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date=date;
    }


    public Chat(String sender, String Text, String date) {
        this.sender = sender;
        this.receiver = "N/A";
        this.message = Text;
        this.date=date;
    }

    public Chat() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
