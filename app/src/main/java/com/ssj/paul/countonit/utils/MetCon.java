package com.ssj.paul.countonit.utils;

import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by PAUL on 4/6/2017.
 */

public class MetCon implements Serializable {

    private long duration;
    private int reps;

    public ArrayList<Integer> lapStart;
    public ArrayList<Integer> lapDuration;

    private void initialize(){
        lapStart = new ArrayList<>();
        lapDuration = new ArrayList<>();
        lapStart.add(Integer.valueOf(0));
    }

    public MetCon(long duration){
        initialize();
        this.duration = duration;
    }

    public MetCon(int reps){
        initialize();
        this.reps = reps;
    }

    public String getStart(int index){
        return formatTime(lapStart.get(index));
    }

    public String getDuration(int index){
        return formatTime(lapDuration.get(index));
    }

    public int getReps(){
        return reps;
    }

    public String tallyRep(long longTime){
        String response;

        Integer lapStartTime = Integer.valueOf((int) (longTime + 500) / 1000 * 1000);
        Integer lapDurationTime = lapStartTime - lapStart.get(lapStart.size() - 1);

        if (lapStart.size() == 1) {
            response = "Round time: " + formatTime(lapDurationTime);
        }
        else {
            Integer prevTime = lapStart.get(lapStart.size() - 1) - lapStart.get(lapStart.size() - 2);
            Integer delta = lapDurationTime - prevTime;

            String symbol = "";
            if (delta > 999)
                symbol = "+";
            else if (delta < -999)
                symbol = "-";
            if (delta < 0)
                delta *= -1;
            response = "Round time: " + formatTime(lapDurationTime) + " (" + symbol + formatTime(delta) + ")";
        }
        lapStart.add(lapStartTime);
        lapDuration.add(lapDurationTime);
        return response;
    }

    public void complete(long duration){
        this.duration = duration;
    }

    public void complete(int reps){
        this.reps = reps;
    }



    private String formatTime(Integer input) {
        return String.format("%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(input),
                TimeUnit.MILLISECONDS.toSeconds(input) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(input)));
    }
}
