package com.dmytro.ponomarev.sportweartimer3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ponomarev on 16.02.2016.
 */
public class ConfigureTimerActivityMobile extends Activity {

    private static final int MAX_HOURS_VALUE = 99;
    private static final int MAX_MINUTES_VALUE = 59;
    private static final int MAX_SECOND_VALUE = 59;

    private long hours;
    private long minutes;
    private long seconds;
    private TextView timer_hours;
    private TextView timer_minutes;
    private TextView timer_seconds;

    class onOkClick implements View.OnClickListener {
        onOkClick() {
        }

        public void onClick(View v) {
            Intent data = new Intent();
            data.putExtra("millis", MainActivity.getMilliseconds(ConfigureTimerActivityMobile.this.hours, ConfigureTimerActivityMobile.this.minutes, ConfigureTimerActivityMobile.this.seconds));
            ConfigureTimerActivityMobile.this.setResult(-1, data);
            ConfigureTimerActivityMobile.this.finish();
        }
    }

    private class TimerModifier implements View.OnClickListener {
        private static final int TIME_PART_HOUR = 1;
        private static final int TIME_PART_MINUTE = 2;
        private static final int TIME_PART_SECOND = 3;
        private final boolean add;
        private final Long maxValue;
        private final int timePart;

        public TimerModifier(int timePart, boolean add, Long maxValue) {
            this.timePart = timePart;
            this.add = add;
            this.maxValue = maxValue;
        }

        public void onClick(View v) {
            long theValue = getValueForTimePart(this.timePart);
            if (this.add) {
                theValue++;
                if (theValue > this.maxValue.longValue()) {
                    theValue = 0;
                }
            } else {
                theValue--;
                if (theValue < 0) {
                    theValue = this.maxValue.longValue();
                }
            }
            setValueForTimePart(theValue, this.timePart);
            ConfigureTimerActivityMobile.this.updateTimerParts();
        }

        private long getValueForTimePart(int timePart) {
            if (timePart == TIME_PART_HOUR) {
                return ConfigureTimerActivityMobile.this.hours;
            }
            if (timePart == TIME_PART_MINUTE) {
                return ConfigureTimerActivityMobile.this.minutes;
            }
            if (timePart == TIME_PART_SECOND) {
                return ConfigureTimerActivityMobile.this.seconds;
            }
            return 0;
        }

        private void setValueForTimePart(long theValue, int timePart) {
            if (timePart == TIME_PART_HOUR) {
                ConfigureTimerActivityMobile.this.hours = theValue;
            }
            if (timePart == TIME_PART_MINUTE) {
                ConfigureTimerActivityMobile.this.minutes = theValue;
            }
            if (timePart == TIME_PART_SECOND) {
                ConfigureTimerActivityMobile.this.seconds = theValue;
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuretimer);


        long millis = getIntent().getLongExtra("millis", 0);
        this.hours = MainActivity.getHours(millis);
        this.minutes = MainActivity.getMinutes(millis);
        this.seconds = MainActivity.getSeconds(millis);
        this.timer_hours = (TextView) findViewById(R.id.timer_hours);
        this.timer_minutes = (TextView) findViewById(R.id.timer_minutes);
        this.timer_seconds = (TextView) findViewById(R.id.timer_seconds);


        findViewById(R.id.timer_hours_up).setOnClickListener(new TimerModifier(TimerModifier.TIME_PART_HOUR, true, Long.valueOf(MAX_HOURS_VALUE)));
        findViewById(R.id.timer_hours_down).setOnClickListener(new TimerModifier(TimerModifier.TIME_PART_HOUR, false, Long.valueOf(MAX_HOURS_VALUE)));
        findViewById(R.id.timer_minutes_up).setOnClickListener(new TimerModifier(TimerModifier.TIME_PART_MINUTE, true, Long.valueOf(MAX_MINUTES_VALUE)));
        findViewById(R.id.timer_minutes_down).setOnClickListener(new TimerModifier(TimerModifier.TIME_PART_MINUTE, false, Long.valueOf(MAX_MINUTES_VALUE)));
        findViewById(R.id.timer_seconds_up).setOnClickListener(new TimerModifier(TimerModifier.TIME_PART_SECOND, true, Long.valueOf(MAX_SECOND_VALUE)));
        findViewById(R.id.timer_seconds_down).setOnClickListener(new TimerModifier(TimerModifier.TIME_PART_SECOND, false, Long.valueOf(MAX_SECOND_VALUE)));
        findViewById(R.id.button_ok).setOnClickListener(new onOkClick());
        updateTimerParts();
    }

    private void updateTimerParts() {
        this.timer_hours.setText(String.format(getString(R.string.timeFormatString), new Object[]{Long.valueOf(this.hours)}));
        this.timer_minutes.setText(String.format(getString(R.string.timeFormatString), new Object[]{Long.valueOf(this.minutes)}));
        this.timer_seconds.setText(String.format(getString(R.string.timeFormatString), new Object[]{Long.valueOf(this.seconds)}));
    }
}
