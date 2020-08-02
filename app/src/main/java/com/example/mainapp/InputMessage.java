package com.example.mainapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;

public class InputMessage extends AppCompatActivity {

    private ArrayList<String> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_message);
    }

    public void switchInputToMessageBoard(View view)
    {
        Intent returnIntent = getIntent();
        EditText currentMessage = (EditText)findViewById(R.id.messageContentInput);
        String sample = currentMessage.getText().toString();
        returnIntent.putExtra("MSG", sample);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


}
