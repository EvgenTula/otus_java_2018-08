package ru.otus.hw16messageserver.server.messageserver.messagesystem.message;

import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import ru.otus.hw16messageserver.server.messageserver.messagesystem.Address;
import ru.otus.hw16messageserver.server.messageserver.messagesystem.Member;

import javax.xml.crypto.Data;

public abstract class Message {

    private static final String CLASS_NAME = "className";
    private static final String DATA = "data";

    private Address from;
    private Address to;
    private String data;

    public Message(Address from, Address to, String data) {
        this.from = from;
        this.to = to;
        this.data = data;
    }

    public Address getFrom() {
        return this.from;
    }

    public Address getTo() {
        return to;
    }

    public String getData() {
        return this.data;
    }

    public abstract void exec(Member sender);

    public String getJsonObject() {
        Gson gson = new Gson();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("className", this.getClass().getName());
        jsonObject.put("data", gson.toJson(this));
        return jsonObject.toString();
    }
}
