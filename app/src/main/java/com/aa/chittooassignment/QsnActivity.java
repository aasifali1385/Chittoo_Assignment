package com.aa.chittooassignment;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QsnActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qsn);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView timer = findViewById(R.id.timertxt);
        EditText answer = findViewById(R.id.ans);
        Button submit = findViewById(R.id.submit);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText("00:"+ String.format("%02d", millisUntilFinished/1000));
                long time = 30-(millisUntilFinished/1000);
                progressBar.setProgress((int)(time*100/30));
            }
            public void onFinish() {
                answer.setEnabled(false);
                submit.setVisibility(View.VISIBLE);
            }
        }.start();


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(QsnActivity.this, answer.getText().toString(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}