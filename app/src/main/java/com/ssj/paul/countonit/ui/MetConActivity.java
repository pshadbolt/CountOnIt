package com.ssj.paul.countonit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ssj.paul.countonit.R;
import com.ssj.paul.countonit.utils.MetCon;

import java.util.concurrent.TimeUnit;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class MetConActivity extends AppCompatActivity {

    private long milliToMin = 60 * 1000;

    Chronometer timer;
    Button startstop, set, reset;
    Button repDisplay;

    private long timeWhenStopped = 0;
    boolean running = false;

    long duration = 0;
    int repsTotal = 0;
    int direction = 1;

    private MetCon metcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_metcon);

        timer = (Chronometer) findViewById(R.id.chronometer);
        startstop = (Button) findViewById(R.id.startstop);
        set = (Button) findViewById(R.id.set);
        //reset = (Button) findViewById(R.id.reset);
        repDisplay = (Button) findViewById(R.id.repDisplay);

        timer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer cArg) {
                if (timer.isCountDown() && timer.getBase() < SystemClock.elapsedRealtime()) {
                    completeWorkout();
                }
            }
        });

        repDisplay.setText(Integer.toString(repsTotal));
    }

    public void trigger(View view) {
        if (running)
            stopTimer();
        else
            startTimer();
    }

    public void startTimer() {
        startstop.setText("STOP");
        running = true;
        timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();
        repDisplay.setEnabled(running);
    }

    public void stopTimer() {
        startstop.setText("START");
        running = false;
        timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        repDisplay.setEnabled(running);
    }

    public void setTimer(View view) {
        stopTimer();

        LayoutInflater layoutInflater = LayoutInflater.from(MetConActivity.this);
        View promptView = layoutInflater.inflate(R.layout.dialog_input, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MetConActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editTextEntry = (EditText) promptView.findViewById(R.id.editTextEntry);

        final RadioButton forTime = (RadioButton) promptView.findViewById(R.id.radioButtonTime);
        final RadioButton forReps = (RadioButton) promptView.findViewById(R.id.radioButtonReps);

        forTime.setChecked(timer.isCountDown());
        forReps.setChecked(!timer.isCountDown());

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.button_submit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //remove keyboard
                                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        // set edit text focus and show keyboard
        editTextEntry.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextEntry.getText().length() > 0) {
                    if (forTime.isChecked()) {
                        duration = Long.parseLong(editTextEntry.getText().toString()) * milliToMin + 100;

                        timer.setBase(SystemClock.elapsedRealtime() + duration);
                        timer.setCountDown(true);

                        repsTotal = 0;
                        repDisplay.setText("0");
                        direction = 1;

                        metcon = new MetCon(duration);
                    } else {
                        duration = 0;

                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.setCountDown(false);

                        repsTotal = Integer.parseInt(editTextEntry.getText().toString());
                        repDisplay.setText(Integer.toString(repsTotal));
                        direction = -1;

                        metcon = new MetCon(repsTotal);
                    }
                    //reset values
                    timeWhenStopped = duration;

                    //remove keyboard
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    alert.dismiss();
                }
            }
        });
    }

    public void resetTimer(View view) {
        startstop.setText("START");
        running = false;
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
    }

    public void tallyRep(View v) {
        Long longLap = timer.isCountDown() ? duration - (timer.getBase() - SystemClock.elapsedRealtime()) : SystemClock.elapsedRealtime() - timer.getBase();
        Toast.makeText(this, metcon.tallyRep(longLap), Toast.LENGTH_LONG).show();
        repsTotal += direction;
        repDisplay.setText(Integer.toString(repsTotal));
        if (direction == -1 && repsTotal == 0) {
            completeWorkout();
        }
    }

    private void completeWorkout() {
        startstop.setText("START");
        repDisplay.setEnabled(false);
        running = false;
        timer.stop();

        if (timer.isCountDown())
            metcon.complete(repsTotal);
        else
            metcon.complete(SystemClock.elapsedRealtime() - timer.getBase());


        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra("metcon",metcon);
        startActivity(intent);
    }
}
