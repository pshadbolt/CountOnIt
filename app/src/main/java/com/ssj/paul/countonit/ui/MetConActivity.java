package com.ssj.paul.countonit.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.ssj.paul.countonit.R;
import com.ssj.paul.countonit.utils.MetCon;

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

    long duration = 0;
    long durationTarget = 0;
    int repsCompleted = 0;
    int repsTarget = 0;
    //int direction = 1;

    private MetCon metcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

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

        startstop.setEnabled(false);
        repDisplay.setText("0");
    }

    public void trigger(View view) {
        if (startstop.getText().equals(R.string.button_start))
            startTimer();
        else if (startstop.getText().equals(R.string.button_pause))
            pauseTimer();
        else
            resumeTimer();
    }

    public void startTimer() {
        Log.d("timer", "start");
        startstop.setText(R.string.button_pause);
        timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();
        repDisplay.setEnabled(true);
    }

    public void pauseTimer() {
        Log.d("timer", "pause");
        if (startstop.isEnabled())
            startstop.setText(R.string.button_resume);
        timeWhenStopped = timer.getBase() - SystemClock.elapsedRealtime();
        timer.stop();
        repDisplay.setEnabled(false);
    }

    public void resumeTimer() {
        Log.d("timer", "resume");
        startstop.setText(R.string.button_pause);
        timer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
        timer.start();
        repDisplay.setEnabled(true);
    }

    public void setTimer(View view) {
        pauseTimer();

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
                        repsTarget = 0;
                    } else {
                        duration = 0;
                        timer.setBase(SystemClock.elapsedRealtime());
                        timer.setCountDown(false);
                        repsTarget = Integer.parseInt(editTextEntry.getText().toString());
                    }
                    //reset values
                    repDisplay.setText(Integer.toString(repsTarget));
                    repsCompleted = 0;
                    metcon = new MetCon();
                    timeWhenStopped = duration;

                    //remove keyboard
                    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                    alert.dismiss();

                    //enable start
                    startstop.setText(R.string.button_start);
                    startstop.setEnabled(true);
                }
            }
        });
    }

    public void resetTimer(View view) {
        startstop.setText(R.string.button_start);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.stop();
    }

    public void tallyRep(View v) {
        Long longLap = timer.isCountDown() ? duration - (timer.getBase() - SystemClock.elapsedRealtime()) : SystemClock.elapsedRealtime() - timer.getBase();
        Toast.makeText(this, metcon.tallyRep(longLap), Toast.LENGTH_LONG).show();
        repsCompleted++;

        if (timer.isCountDown())
            repDisplay.setText(Integer.toString(repsCompleted));
        else
            repDisplay.setText(Integer.toString(repsTarget - repsCompleted));

        if (repsCompleted == repsTarget) {
            completeWorkout();
        }
    }

    private void completeWorkout() {
        timer.stop();

        if (timer.isCountDown())
            metcon.complete(SystemClock.elapsedRealtime() + duration - timer.getBase(), repsCompleted);
        else
            metcon.complete(SystemClock.elapsedRealtime() - timer.getBase(), repsCompleted);

        startstop.setText(R.string.button_start);
        startstop.setEnabled(false);
        repDisplay.setEnabled(false);

        Log.d("metcon", metcon.getDuration());
        Log.d("metcon", Integer.toString(metcon.getReps()));

        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra("metcon", metcon);
        startActivity(intent);
    }
}
