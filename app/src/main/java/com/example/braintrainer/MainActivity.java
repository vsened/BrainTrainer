package com.example.braintrainer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TextView textViewTimer;
    private TextView textViewScore;
    private TextView textViewQuestion;

    private ArrayList<Button> buttons;

    private int rightAnswer;
    private int rightAnswerPosition;
    private boolean isPositive;
    private int min = 5;
    private int max = 30;

    private int rightAttempt;
    private int countAttempt;

    private SharedPreferences preference;
    private CountDownTimer timer;
    private boolean gameOver = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.hide();
        }
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        countAttempt = 0;
        rightAttempt = 0;

        textViewScore = findViewById(R.id.textViewScore);
        textViewTimer = findViewById(R.id.textViewTimer);
        textViewQuestion = findViewById(R.id.textViewQuestion);

        textViewScore.setText(String.format(getString(R.string.current_score_text), rightAttempt, countAttempt));

        Button first_answer_button = findViewById(R.id.button0);
        Button second_answer_button = findViewById(R.id.button1);
        Button third_answer_button = findViewById(R.id.button2);
        Button fourth_answer_button = findViewById(R.id.button3);

        buttons = new ArrayList<>();
        buttons.add(first_answer_button);
        buttons.add(second_answer_button);
        buttons.add(third_answer_button);
        buttons.add(fourth_answer_button);

        playNext();
        timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                int sec = (int) (l / 1000);
                textViewTimer.setText(String.format(getString(R.string.timer_text), sec / 60, sec % 60));
                if (sec <= 5){
                    textViewTimer.setTextColor(Color.RED);
                    textViewTimer.setTypeface(Typeface.DEFAULT_BOLD);
                }
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, getString(R.string.time_is_over), Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (rightAttempt >= max){
                    preferences.edit().putInt("max", rightAttempt).apply();
                }
                Intent intent = new Intent(MainActivity.this, GameOverActivity.class);
                intent.putExtra("rightAttempt", rightAttempt);
                rightAttempt = 0;
                startActivity(intent);
            }
        };
        timer.start();
    }

    private void playNext() {
        generateQuestion();
        ArrayList<Integer> wrongAnswers = new ArrayList<>();
        for (int i = 0; i < buttons.size(); i++){
            if (i == rightAnswerPosition){
                buttons.get(i).setText(String.format(getString(R.string.string_number), rightAnswer));
            } else {
                int wrongAnswer = generateWrongAnswer(wrongAnswers);
                wrongAnswers.add(wrongAnswer);
                buttons.get(i).setText(String.format(getString(R.string.string_number), wrongAnswer));
            }
        }

    }

    private void generateQuestion(){
        int firstNumber = (int) (Math.random() * (max - min + 1) + min);
        int secondNumber = (int) (Math.random() * (max - min + 1) + min);
        int mark = (int) (Math.random() * 2);
        isPositive = mark == 1;
        textViewTimer.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        if (isPositive) {
            rightAnswer = firstNumber + secondNumber;
            textViewQuestion.setText(String.format(getString(R.string.positive_question), firstNumber, secondNumber));
        } else {
            rightAnswer = firstNumber - secondNumber;
            textViewQuestion.setText(String.format(getString(R.string.negative_question), firstNumber, secondNumber));
        }
        rightAnswerPosition = (int) (Math.random() * buttons.size());
    }

    public void onClickAnswerButton(View view) {
        Button answer = (Button) view;
        int currentPosition = Integer.parseInt(answer.getTag().toString());
        if (currentPosition == rightAnswerPosition){
            Toast.makeText(this, getString(R.string.right_answer), Toast.LENGTH_SHORT).show();
            rightAttempt++;
            countAttempt++;
            textViewScore.setText(String.format(getString(R.string.current_score_text), rightAttempt, countAttempt));
            playNext();
        } else {
            Toast.makeText(this, getString(R.string.wrong_answer), Toast.LENGTH_SHORT).show();
            countAttempt++;
            textViewScore.setText(String.format(getString(R.string.current_score_text), rightAttempt, countAttempt));
            playNext();
        }
    }

    private int generateWrongAnswer(ArrayList<Integer> wrongAnswers){
        int number = rightAnswer;
        while (number == rightAnswer || wrongAnswers.contains(number)){
            number = (int) (Math.random() * max * 2 + 1) - (max - min);
        }
        return number;
    }
}