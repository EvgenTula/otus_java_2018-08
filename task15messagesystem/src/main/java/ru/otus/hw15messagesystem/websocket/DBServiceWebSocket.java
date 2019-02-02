package ru.otus.hw15messagesystem.websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ru.otus.hw15messagesystem.hibernate.DBService;
import ru.otus.hw15messagesystem.hibernate.datasets.UserDataSetHibernate;
import ru.otus.hw15messagesystem.hibernate.dbservice.DBServiceHibernateImpl;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@WebSocket
public class DBServiceWebSocket {
    private Set<DBServiceWebSocket> userList;
    private DBService dbService;

    private Session session;

    public DBServiceWebSocket(DBService dbService, Set<DBServiceWebSocket> userList) {
        this.dbService = dbService;
        this.userList = userList;
    }

    @OnWebSocketMessage
    public void onMessage(String data) throws IOException {
        Gson gson = new Gson();
        UserDataSetHibernate newUser = gson.fromJson(data, UserDataSetHibernate.class);
        dbService.save(newUser);
        List<UserDataSetHibernate> dbUserList = ((DBServiceHibernateImpl)this.dbService).userGetAllList();
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String gsonString = gson.toJson(dbUserList);
        for (DBServiceWebSocket item: userList) {
            item.getSession().getRemote().sendString(gsonString);
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) throws IOException {
        userList.add(this);
        setSession(session);
        List<UserDataSetHibernate> dbUserList = ((DBServiceHibernateImpl)this.dbService).userGetAllList();
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        this.session.getRemote().sendString(gson.toJson(dbUserList));
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        this.userList.remove(this);
    }
}
