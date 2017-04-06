package com.ssj.paul.countonit.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar.LayoutParams;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ssj.paul.countonit.R;
import com.ssj.paul.countonit.utils.MetCon;

public class SummaryActivity extends AppCompatActivity {

    private MetCon metcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        metcon = (MetCon) getIntent().getSerializableExtra("metcon");

        //https://github.com/InQBarna/TableFixHeaders

        TableLayout table = (TableLayout) findViewById(R.id.summaryTable);

        for (int i = 0; i < metcon.getReps(); i++) {
            TableRow row = new TableRow(this);
            TextView round = new TextView(this);
            round.setText(Integer.toString(i + 1));
            round.setGravity(Gravity.CENTER);
            row.addView(round);

            TextView start = new TextView(this);
            start.setText(metcon.getStart(i));
            start.setGravity(Gravity.CENTER);
            row.addView(start);

            TextView duration = new TextView(this);
            duration.setText(metcon.getDuration(i));
            duration.setGravity(Gravity.CENTER);
            row.addView(duration);

            table.addView(row, new TableLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }
}