package com.example.mainapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MessageBoard extends AppCompatActivity
{

    private RecyclerView recyclerViewMainMessage;
    private RecyclerView.Adapter messageAdapter;
    private RecyclerView.LayoutManager myLayoutManager;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private int msgRequestCode = 1;
    private int replyRequestCode = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_board);
        recyclerViewMainMessage = (RecyclerView) findViewById(R.id.recyclerView);
        myLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewMainMessage.setLayoutManager(myLayoutManager);
        messageAdapter = new MessageAdapter(this, database);
        recyclerViewMainMessage.setAdapter(messageAdapter);
        recyclerViewMainMessage.setNestedScrollingEnabled(true);
        // Swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDelete((MessageAdapter) messageAdapter));
        itemTouchHelper.attachToRecyclerView(recyclerViewMainMessage);


        DatabaseReference rf = database.getReference();
        loadDataBase();
    }

    public void loadDataBase()
    {
        DatabaseReference rf = database.getReference();
        rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (ds.getKey().equals("mainMessages"))
                    {
                        for (DataSnapshot mainDs : ds.getChildren())
                        {
                            Message msg = mainDs.getValue(Message.class);
                            ((MessageAdapter) messageAdapter).updateOnLoad(msg);
                        }
                        ((MessageAdapter) messageAdapter).sortMessages();
                    }
                    else if (ds.getKey().equals("replyMessages"))
                    {
                        for (DataSnapshot subDs : ds.getChildren())
                        {
                            ArrayList<Message> mainMessages = ((MessageAdapter) messageAdapter).messages;
                            for (int i = 0; i < mainMessages.size(); i++)
                            {

                                if (mainMessages.get(i).getId().equals(subDs.getKey()))
                                {
                                    for (DataSnapshot rDs : subDs.getChildren())
                                    {
                                        Message msg = rDs.getValue(Message.class);
                                        ((MessageAdapter)messageAdapter).messages.get(i).getReplyAdapter().updateOnLoad(msg, subDs.getRef());
                                    }

                                    ((MessageAdapter)messageAdapter).messages.get(i).getReplyAdapter().sortMessages();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void switchToInputMessage(View view, int requestCode)
    {
        Intent intent = new Intent(this, InputMessage.class);
        startActivityForResult(intent, requestCode);
    }

    public void switchToRoot(View view)
    {
        finish();
    }

    public void addMainMessage(View view)
    {

        switchToInputMessage(view, msgRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == msgRequestCode)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String msg = data.getStringExtra("MSG");
                ((MessageAdapter) messageAdapter).updateMainMessages(msg);
            }

        }
        else if (requestCode == replyRequestCode) {
            if (resultCode == Activity.RESULT_OK) {

                int index = data.getIntExtra("INDEX", -1);
                String input = data.getStringExtra("MSG");

                database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("replyMessages");
                myRef = myRef.child(((MessageAdapter) messageAdapter).messages.get(index).getId());
                ((MessageAdapter) messageAdapter).messages.get(index).getReplyAdapter().updateReplyMessage(input, myRef);

            }
        }

    }

}