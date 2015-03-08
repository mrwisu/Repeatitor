package pl.wisu.repeatitor;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

    public static final int BLINK_TIME_MS = 2000;

    TextView question;
    TextView answer;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlayout);

        parentLayout = (RelativeLayout) findViewById(R.id.layoutview);

        final LayoutTransition transitioner = new LayoutTransition();
        parentLayout.setLayoutTransition(transitioner);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowwidth = size.x;
        windowheight = size.y;
        screenCenterX = windowwidth / 2;
        screenCenterY = windowheight / 2;

        question = (TextView)findViewById(R.id.question);
        answer = (TextView)findViewById(R.id.answer);

        // save default layout
        final RelativeLayout.LayoutParams def_params = (RelativeLayout.LayoutParams) answer.getLayoutParams();
        def_params.addRule(RelativeLayout.BELOW, R.id.question);

        final Map<String, Boolean> answersList = new HashMap<>();

        answersList.put("Vancomycin",       false);
        answersList.put("Penicillins",      true);
        answersList.put("Cephalosporins",   true);
        answersList.put("aztreonam",        true);
        answersList.put("macrolides",       false);
        answersList.put("tetracyclins",     false);
        answersList.put("rifampin",         false);
        answersList.put("pyrimethamine",    false);

        question.setText("Lactam?");

        nextAnswer(answer, answersList);

        answer.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        x_cord = (int) event.getRawX();
                        y_cord = (int) event.getRawY();

                        answer.setX(x_cord - screenCenterX + 40);
                        answer.setY(y_cord - screenCenterY);

                        if (x_cord >= screenCenterX) {
                            answer
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
                            answer
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
                            parentLayout.removeView(answer);
                            parentLayout.addView(answer);
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

                            parentLayout.removeView(answer);
                            parentLayout.addView(answer);
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

                            parentLayout.removeView(answer);
                            parentLayout.addView(answer);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void nextAnswer(TextView a, Map<String, Boolean> aList)
    {
        Object[] ansArray = aList.keySet().toArray();
        Random gen = new Random();
        int ansNumber = gen.nextInt(ansArray.length);
        a.setText(ansArray[ansNumber].toString());
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
}