package com.example.braintrainer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

public class GameOverActivity extends AppCompatActivity {

    private TextView textViewGameResult;
    private SharedPreferences preferences;
    private int bestAttempt;
    private int currentAttempt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("rightAttempt")) {
            currentAttempt = intent.getIntExtra("rightAttempt", 0);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        bestAttempt = preferences.getInt("max", 0);
        textViewGameResult = findViewById(R.id.textViewGameResult);
        textViewGameResult.setText(String.format(getString(R.string.game_result), currentAttempt, bestAttempt));
    }

    public void OnClickGameAgainButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}