package com.uqac.sellet;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.Map;

public class Message {
    String author;
    Timestamp timestamp;
    String content;

    Message(String author, Timestamp timestamp, String content){
        this.author = author;
        this.timestamp = timestamp;
        this.content = content;
    }

    Map<String, Object> toMap(){
        Map<String, Object> h = new HashMap<>();
        h.put("author", author);
        h.put("timestamp", timestamp);
        h.put("content", content);
        return h;
    }
}
