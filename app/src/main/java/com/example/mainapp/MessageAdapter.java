package com.example.mainapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class MessageAdapter extends RecyclerView.Adapter
{

    Context context;
    ArrayList<Message> messages;
    FirebaseDatabase database;



    public MessageAdapter ( Context context , FirebaseDatabase database)
    {
        this.context = context;
        this.messages = new ArrayList<Message>();
        this.database = database;
    }


    public void updateMainMessages(String newMessage) {

        // Add the main Message to the database
        DatabaseReference myRef = database.getReference("mainMessages").push();
        myRef.child("message").setValue(newMessage);
        myRef.child("ranking").setValue(0);
        myRef.child("id").setValue(myRef.getKey());

        Message msg = new Message(newMessage, myRef.getKey(), 0);
        messages.add(msg);
        messages.get(messages.size() - 1).setReplyAdapter(new ReplyMessageAdapter(context, database));
        notifyDataSetChanged();
    }

    public void updateOnLoad(Message msg)
    {
        messages.add(msg);
        messages.get(messages.size() - 1).setReplyAdapter(new ReplyMessageAdapter(context, database));
        notifyDataSetChanged();
    }

    public void sortMessages()
    {
        Collections.sort(messages, Collections.reverseOrder());
        notifyDataSetChanged();
    }

    public void updateRanking(int position)
    {
        if (position < messages.size())
        {
            int value = messages.get(position).getRanking() + 1;

            DatabaseReference myRef = database.getReference("mainMessages");
            myRef.child(messages.get(position).getId()).child("ranking").setValue(value);

            messages.get(position).setRanking(value);
            sortMessages();
        }
    }

    public void deleteMessage(int position)
    {
        DatabaseReference myRef = database.getReference();
        myRef.child("mainMessages").child(messages.get(position).getId()).removeValue();
        myRef.child("replyMessages").child(messages.get(position).getId()).removeValue();
        messages.remove(position);
        sortMessages();
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(view);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position)
    {
        ((MessageViewHolder)holder).message.setText(messages.get(position).getMessage());
        ((MessageViewHolder)holder).ranking.setText(messages.get(position).getRanking().toString());

        messages.get(position).getReplyAdapter().setItemTouch((MessageViewHolder) holder);

        ((MessageViewHolder)holder).ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRanking(((MessageViewHolder)holder).getAdapterPosition());
            }
        });



    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }

}
