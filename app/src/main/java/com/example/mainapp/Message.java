package com.example.mainapp;

import java.util.ArrayList;

public class Message implements Comparable{

    private String message;
    private String id;
    private Integer ranking;
    private ReplyMessageAdapter replyAdapter = null;
    private String parentID = null;

    public Message() {

    }

    public Message(String message, String id, int ranking)
    {
        this.message = message;
        this.id = id;
        this.ranking = ranking;
    }

    @Override
    public String toString()
    {
        return ("Message: " + message + " " + "Ranking: " + ranking);
    }

    public String getMessage() {
        return message;
    }

    public String getId()
    {
        return this.id;
    }

    public Integer getRanking()
    {
        return ranking;
    }

    public String getParentID()
    {
        return parentID;
    }

    public ReplyMessageAdapter getReplyAdapter()
    {
        return replyAdapter;
    }


    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setReplyAdapter(ReplyMessageAdapter adapter)
    {
        replyAdapter = adapter;
    }

    public void setRanking(Integer ranking)
    {
        this.ranking = ranking;
    }

    public void setParentID(String parentID)
    {
        this.parentID = parentID;
    }

    @Override
    public int compareTo(Object o) {
        return this.getRanking() - ((Message)o).getRanking();
    }

}
