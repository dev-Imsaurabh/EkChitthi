package com.mac.ekchitthi.Letter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.mac.ekchitthi.R;

public class ReadLetterActivity extends AppCompatActivity {
    private EditText et_read;
    private String letter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_letter);
        letter = getIntent().getStringExtra("letter");

        et_read=findViewById(R.id.et_read);

        if(!letter.equals("")){
            et_read.setText(letter);


        }

    }
}