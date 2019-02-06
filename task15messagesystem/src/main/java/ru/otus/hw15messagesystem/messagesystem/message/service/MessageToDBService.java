package ru.otus.hw15messagesystem.messagesystem.message.service;

import ru.otus.hw15messagesystem.hibernate.DBService;
import ru.otus.hw15messagesystem.messagesystem.Address;
import ru.otus.hw15messagesystem.messagesystem.Message;
import ru.otus.hw15messagesystem.messagesystem.Sender;

public abstract class MessageToDBService extends Message {

    public MessageToDBService(Sender from, Sender to) {
        super(from, to);
    }

    @Override
    public void exec(Sender sender) {
        if (sender instanceof DBService) {
            exec((DBService)sender);
        }
    }

    public abstract void exec(DBService dbService);
}