package com.example.carla.xhosabot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();

        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            intent.putExtra("USER", getIntent().getStringExtra("USER"));
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
