package com.example.mainapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
{
    public TextView message;
    public TextView ranking;
    public ImageButton replyButton;
    public RecyclerView recyclerViewReplyMessage;
    public Context context;

    private int replyRequestCode = 2;


    public MessageViewHolder(View view)
    {
        super(view);
        message = (TextView) view.findViewById(R.id.messagePost);
        ranking = (TextView) view.findViewById(R.id.rankNum);
        replyButton = (ImageButton) view.findViewById(R.id.replyPost);
        replyButton.setOnClickListener(this);
        recyclerViewReplyMessage = (RecyclerView) view.findViewById(R.id.replyMessageBoard);
        context = view.getContext();
    }

    @Override
    public void onClick(View v) {

        int index = getAdapterPosition();
        Intent intent = new Intent(context, InputMessage.class);
        intent.putExtra("INDEX", index);
        ((Activity)context).startActivityForResult(intent, replyRequestCode);

    }


}
