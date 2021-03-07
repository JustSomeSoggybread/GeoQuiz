package com.example.geoquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final String IS_CHEATER = "choot";

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private int score = 0;
    private int mCurrentIndex= 0;
    private boolean mIsCheater;
    private String SAVE_ARRAY = "array";



    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_one, true),
            new Question(R.string.question_two,  false),
            new Question(R.string.question_three, true),
    };

    private boolean[] mCheatBank = new boolean[mQuestionBank.length];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        for (int i = 0; i < mCheatBank.length; i++){
            mCheatBank[i] = false;
        }

        if(savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(true);
            }
        });


        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                checkAnswer(false);
            }
        });

        mCheatButton= (Button) findViewById((R.id.cheat_button));
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(MainActivity.this, answerIsTrue);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1 ) % mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                if (mCurrentIndex == -1){
                    mCurrentIndex = 2;
                }
                updateQuestion();
            }
        });

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });


        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(IS_CHEATER, false);
            mCheatBank = savedInstanceState.getBooleanArray(SAVE_ARRAY);
        }
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            if (data == null){
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }

        if (mIsCheater){
            mCheatBank[mCurrentIndex] = true;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
        score = 0;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSAVEInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        savedInstanceState.putBoolean(IS_CHEATER, mIsCheater);
        savedInstanceState.putBooleanArray(SAVE_ARRAY, mCheatBank);
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() calld");
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText((question));
        mTrueButton.setEnabled(true);
        mFalseButton.setEnabled(true);
        mPreviousButton.setEnabled(true);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;


        if(userPressedTrue == answerIsTrue) {
            messageResId = R.string.correct_toast;
            mPreviousButton.setEnabled(false);
            mFalseButton.setEnabled(false);
            mTrueButton.setEnabled(false);

            if (mIsCheater){messageResId = R.string.judgement_toast;}

            else if(mCheatBank[mCurrentIndex] == true){
                messageResId = R.string.judgement_toast;
            }

            else{
                score++;
                Log.d(TAG, "score: " + score);}

        }
        else{
            if (userPressedTrue) {
                mTrueButton.setEnabled(false);
            } else {
                mFalseButton.setEnabled(false);
            }
            messageResId = R.string.incorrect_toast;
        }
        displayToast(messageResId, 0, 0);
    }

    private void displayToast(int messageID, int x, int y) {
        Toast toast = Toast.makeText(this, messageID, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL, x, y);
        toast.show();
        if (mCurrentIndex == mQuestionBank.length -1) {
            //mNextButton.setEnabled(false);
            if (score > 3){
                score = 3;
            }
            Toast scoreToast = Toast.makeText(this, "Score: " + (score * 100) / 3 + "%", Toast.LENGTH_SHORT);
            scoreToast.setGravity(Gravity.TOP | Gravity.CENTER_VERTICAL, x, y);
            scoreToast.show();

        }
    }
}