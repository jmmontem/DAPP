package com.example.mainapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


public class ReplyMessageAdapter extends RecyclerView.Adapter
{
    Context context;
    ArrayList<Message> messages;
    FirebaseDatabase database;

    ItemTouchHelper itemTouchHelper;

    public ReplyMessageAdapter(Context context, FirebaseDatabase database)
    {

        this.context = context;
        this.messages = new ArrayList<Message>();
        this.database = database;
    }




    public void updateReplyMessage(String newMessage, DatabaseReference myRefParent)
    {
        DatabaseReference myRef = myRefParent.push();
        myRef.child("message").setValue(newMessage);
        myRef.child("ranking").setValue(0);
        myRef.child("id").setValue(myRef.getKey());
        Message msg = new Message(newMessage, myRef.getKey(), 0);
        msg.setParentID(myRefParent.getKey());
        messages.add(msg);
        notifyDataSetChanged();
    }

    public void setItemTouch(MessageViewHolder holder)
    {
        holder.recyclerViewReplyMessage.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerViewReplyMessage.setAdapter(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteReply(this));
        itemTouchHelper.attachToRecyclerView(holder.recyclerViewReplyMessage);
    }

    public void updateOnLoad(Message msg, DatabaseReference myRefParent)
    {
        msg.setParentID(myRefParent.getKey());
        messages.add(msg);
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
            messages.get(position).setRanking(value);

            DatabaseReference myRef = database.getReference("replyMessages");
            myRef.child(messages.get(position).getParentID()).child(messages.get(position).getId()).child("ranking").setValue(value);
            sortMessages();
        }
    }

    public void deleteMessage(int position)
    {

        System.out.println("Position: " + position);
        System.out.println("Message: " + messages.toString());
        DatabaseReference myRef = database.getReference("replyMessages");
        myRef.child(messages.get(position).getParentID()).child(messages.get(position).getId()).removeValue();
        messages.remove(position);
        sortMessages();

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reply_view, parent, false);
        ReplyViewHolder replyViewHolder = new ReplyViewHolder(view);
        return replyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position)
    {
        ((ReplyViewHolder)holder).message.setText(this.messages.get(position).getMessage());
        ((ReplyViewHolder)holder).ranking.setText(this.messages.get(position).getRanking().toString());

        ((ReplyViewHolder)holder).ranking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = holder.getAdapterPosition();
                updateRanking(value);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return messages.size();
    }
}


class ReplyViewHolder extends RecyclerView.ViewHolder
{
    public TextView message;
    public TextView ranking;
    public ReplyViewHolder(View view)
    {
        super(view);
        message = view.findViewById(R.id.replyMessage);
        ranking = view.findViewById(R.id.replyRanking);
    }

}
