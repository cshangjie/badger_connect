package com.example.badgerconnect.Model;

public class Message {

    private String Date;
    private String Mid;
    private String Text;
    private String sender;

    public Message() {}
    //create a new message structure
//        {
//            "Date": "01/08/2023",
//                "Mid": "mid2",
//                "Text": "This is the second message",
//                "sender": "4sv3fHtFAlNbye7BPwnVHYjqgRr2"
//        }
    public Message(String date,  String text, String sender) {
        this.Date = date;

        this.Text = text;
        this.sender = sender;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }


    public String getText() {
        return Text;
    }

    public void setText(String text) {
        this.Text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
