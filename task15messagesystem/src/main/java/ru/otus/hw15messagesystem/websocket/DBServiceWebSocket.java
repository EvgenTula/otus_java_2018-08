package ru.otus.hw15messagesystem.websocket;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import ru.otus.hw15messagesystem.frontend.Frontend;
import ru.otus.hw15messagesystem.hibernate.DBService;
import ru.otus.hw15messagesystem.hibernate.datasets.PhoneDataSetHibernate;
import ru.otus.hw15messagesystem.hibernate.datasets.UserDataSetHibernate;
import ru.otus.hw15messagesystem.hibernate.dbservice.DBServiceHibernateImpl;
import ru.otus.hw15messagesystem.messagesystem.Address;
import ru.otus.hw15messagesystem.messagesystem.MessageSystem;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@WebSocket
public class DBServiceWebSocket {

    private MessageSystem messageSystem;
    private Set<DBServiceWebSocket> userList;
    private DBService dbService;

    private Session session;

    public DBServiceWebSocket(MessageSystem messageSystem,DBService dbService, Set<DBServiceWebSocket> userList) {
        this.messageSystem = messageSystem;
        this.dbService = dbService;
        this.userList = userList;
    }

    @OnWebSocketMessage
    public void onMessage(String data) throws IOException {
        Gson gson = createGsonWithFilter();
        UserDataSetHibernate newUser = gson.fromJson(data, UserDataSetHibernate.class);
        for (PhoneDataSetHibernate phone : newUser.getPhoneList()) {
            phone.setUserDataSet(newUser);
        }
        dbService.save(newUser);
        List<UserDataSetHibernate> dbUserList = ((DBServiceHibernateImpl)this.dbService).userGetAllList();
        for (DBServiceWebSocket item: userList) {
            item.getSession().getRemote().sendString(gson.toJson(dbUserList));
        }
    }

    @OnWebSocketConnect
    public void onOpen(Session session) throws IOException {
        System.out.println(session.getRemoteAddress().toString());

        this.messageSystem.addAddress(new Frontend(session,new Address("1")));

        userList.add(this);
        setSession(session);
        List<UserDataSetHibernate> dbUserList = ((DBServiceHibernateImpl)this.dbService).userGetAllList();
        Gson gson = createGsonWithFilter();
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

    private Gson createGsonWithFilter() {
        return new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                boolean shouldSkipField = fieldAttributes.getDeclaredClass().equals(UserDataSetHibernate.class);
                return shouldSkipField;
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).create();
    }
}
