package com.austin.mycitypicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.austin.timepicker.TimePicker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker timePicker = new TimePicker.Builder(MainActivity.this)
                        .monthCyclic(false)
                        .yearCyclic(false)
                        .defaultMonth("3")
                        .defaultDay("10")
                        .build();

                timePicker.show();

                timePicker.setOnDayItemClickListener(new TimePicker.OnDayItemClickListener() {
                    @Override
                    public void onSelected(String... daySelected) {
                        tv.setText(daySelected[0]+"年\n" +daySelected[1] + "月\n"+ daySelected[2]+ "日");

                    }
                });
            }
        });
    }
}
