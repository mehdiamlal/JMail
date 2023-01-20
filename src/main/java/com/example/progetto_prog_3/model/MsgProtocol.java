package com.example.progetto_prog_3.model;


import java.io.Serializable;

public class MsgProtocol<T> implements Serializable {

    public enum MsgAction {
        SEND_EMAIL_REQUEST,
        SEND_EMAIL_RESPONSE,
        GET_INBOX_FOR_USER_IN_REQUEST,
        GET_INBOX_FOR_USER_IN_RESPONSE,
        GET_INBOX_FOR_USER_OUT_REQUEST,
        GET_INBOX_FOR_USER_OUT_RESPONSE,
        GET_NOTIFICATION_FOR_USER_REQUEST,
        GET_NOTIFICATION_FOR_USER_RESPONSE,
        REMOVE_EMAIL_REQUEST,
        REMOVE_EMAIL_RESPONSE
    }

    public enum MsgError {
        NO_ERROR,
        WRONG_EMAIL,
        EMAIL_DOESNT_EXIST,
        WRONG_SENDER_EMAIL
    }

    private final T msg;
    private final MsgAction action;
    private final MsgError error;

    public MsgProtocol(T msg, MsgAction action, MsgError error) {
        this.msg = msg;
        this.action = action;
        this.error = error;
    }

    public MsgProtocol(T msg,MsgAction action){
        this.msg = msg;
        this.action = action;
        this.error = MsgError.NO_ERROR;
    }

    public T getMsg() {
        return msg;
    }

    public MsgAction getAction() {
        return action;
    }

    public MsgError getError() {
        return error;
    }
}
