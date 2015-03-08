package pl.wisu.repeatitor;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {
    int windowwidth,
        windowheight;
    int screenCenterX,
        screenCenterY;
    int x_cord, y_cord;
    int Likes = 0;
    RelativeLayout parentLayout;
    LayoutTransition transitioner;

    Map<String, Boolean> answersList;

    OnTouchListener ansTlistener;

    public static final int BLINK_TIME_MS = 2000;

    TextView question;
    TextView answer;
    String current_answer;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mainlayout);

        parentLayout = (RelativeLayout) findViewById(R.id.layoutview);

        transitioner = new LayoutTransition();
        parentLayout.setLayoutTransition(transitioner);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowwidth = size.x;
        windowheight = size.y;
        screenCenterX = windowwidth / 2;
        screenCenterY = windowheight / 2;

        question = (TextView)findViewById(R.id.question);

        // TODO read from database
        answersList = new HashMap<>();
        answersList.put("Vancomycin",       false);
        answersList.put("Penicillins",      true);
        answersList.put("Cephalosporins",   true);
        answersList.put("aztreonam",        true);
        answersList.put("macrolides",       false);
        answersList.put("tetracyclins",     false);
        answersList.put("rifampin",         false);
        answersList.put("pyrimethamine",    false);

        question.setText("Lactam?");

        ansTlistener = new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x_cord = (int) event.getRawX();
                        y_cord = (int) event.getRawY();

                        v.setX(x_cord - screenCenterX + 40);
                        v.setY(y_cord - screenCenterY);

                        if (x_cord >= screenCenterX) {
                            v
                                    .setRotation((float) ((x_cord - screenCenterX) * (Math.PI / 32)));
                            if (x_cord > (screenCenterX + (screenCenterX / 2))) {
                                if (x_cord > (windowwidth - (screenCenterX / 4))) {
                                    Likes = 2;
                                } else {
                                    Likes = 0;
                                }
                            } else {
                                Likes = 0;
                            }
                        } else {
                            // rotate
                            v
                                    .setRotation((float) ((x_cord - screenCenterX) * (Math.PI / 32)));
                            if (x_cord < (screenCenterX / 2)) {
                                if (x_cord < screenCenterX / 4) {
                                    Likes = 1;
                                } else {
                                    Likes = 0;
                                }
                            } else {
                                Likes = 0;
                            }

                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        x_cord = (int) event.getRawX();
                        y_cord = (int) event.getRawY();

                        Log.e("X Point", "" + x_cord + " , Y " + y_cord);

                        if (Likes == 0)
                        {
                            Log.e("Event Status", "Nothing");
                            newAnswerView(false);
                        }
                        else if (Likes == 1)
                        {
                            // answer NO
                            if (answersList.get(answer.getText().toString()))
                            {
                                // good != wrong - bad answer
                                Log.e("Answer", "Wrong");
                                showAnswer(transitioner, false);
                            }
                            else
                            {
                                // wrong == wrong - right answer
                                Log.e("Answer", "Good");
                                showAnswer(transitioner, true);
                            }

                            newAnswerView(true);
                        }
                        else if (Likes == 2)
                        {
                            // answer YES
                            if (answersList.get(answer.getText().toString()))
                            {
                                // good == good - right answer
                                Log.e("Answer", "Good");
                                showAnswer(transitioner, true);
                            }
                            else
                            {
                                // wrong != good - bad answer
                                Log.e("Answer", "Wrong");
                                showAnswer(transitioner, false);
                            }

                            newAnswerView(true);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        };

        newAnswerView(true);
    }

    public void nextAnswer(TextView a, Map<String, Boolean> aList)
    {
        Object[] ansArray = aList.keySet().toArray();
        Random gen = new Random();
        int ansNumber = gen.nextInt(ansArray.length);
        current_answer = ansArray[ansNumber].toString();
        a.setText(current_answer);
    }

    public void showAnswer(LayoutTransition trans, boolean correct)
    {
        int color;

        if (correct)
            color = Color.GREEN;
        else
            color = Color.RED;

        trans.setDuration(BLINK_TIME_MS);
        parentLayout.findViewWithTag("answer_indicator").setBackgroundColor(color);
        parentLayout.findViewWithTag("answer_indicator").setVisibility(View.VISIBLE);
        parentLayout.findViewWithTag("answer_indicator").setVisibility(View.INVISIBLE);
        trans.setDuration(0);
    }

    public void newAnswerView(boolean changeAnswer)
    {
        parentLayout.removeView(answer);
        answer = new TextView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) ViewGroup.LayoutParams.FILL_PARENT, (int) ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(10, 10, 10, 10);
        params.addRule(RelativeLayout.BELOW, R.id.question);
        answer.setPadding(10, 10, 10, 10);
        answer.setTextSize((float) 30);
        answer.setGravity(Gravity.CENTER);
        answer.setBackgroundColor(Color.parseColor("#ff3300"));
        answer.setLayoutParams(params);
        if (changeAnswer)
        {
            nextAnswer(answer, answersList);
        }
        else
        {
            answer.setText(current_answer);
        }
        parentLayout.addView(answer);
        answer.setOnTouchListener(ansTlistener);
        answer.getMeasuredState();
    }
}