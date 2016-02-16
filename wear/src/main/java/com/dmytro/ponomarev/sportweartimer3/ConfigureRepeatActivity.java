package com.dmytro.ponomarev.sportweartimer3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ponomarev on 16.02.2016.
 */
public class ConfigureRepeatActivity extends Activity {
    private long repeatCount;
    private TextView vRepeatCount;


    class onOkClick implements View.OnClickListener {
        onOkClick() {
        }

        public void onClick(View v) {
            Intent data = new Intent();
            data.putExtra("repeatCount", ConfigureRepeatActivity.this.repeatCount);
            ConfigureRepeatActivity.this.setResult(-1, data);
            ConfigureRepeatActivity.this.finish();
        }
    }

    private class RepeatCountModifier implements View.OnClickListener {
        private final boolean add;
        private final Long maxValue;

        public RepeatCountModifier(boolean add, Long maxValue) {
            this.add = add;
            this.maxValue = maxValue;
        }

        public void onClick(View v) {
            long theValue = ConfigureRepeatActivity.this.repeatCount;
            if (this.add) {
                theValue++;
                if (theValue > this.maxValue.longValue()) {
                    theValue = -1;
                }
            } else {
                theValue--;
                if (theValue < -1) {
                    theValue = this.maxValue.longValue();
                }
            }
            ConfigureRepeatActivity.this.repeatCount = theValue;
            ConfigureRepeatActivity.this.updateTimerParts();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configurerepeat);
        this.repeatCount = getIntent().getLongExtra("repeatCount", 0);
        this.vRepeatCount = (TextView) findViewById(R.id.repeatCount);
        findViewById(R.id.repeatCount_up).setOnClickListener(new RepeatCountModifier(true, Long.valueOf(99)));
        findViewById(R.id.repeatCount_down).setOnClickListener(new RepeatCountModifier(false, Long.valueOf(99)));
        findViewById(R.id.button_ok).setOnClickListener(new onOkClick());
        updateTimerParts();
    }

    private void updateTimerParts() {
        if (this.repeatCount >= 0) {
            this.vRepeatCount.setText(String.format("%2d", new Object[]{Long.valueOf(this.repeatCount)}));
        } else {
            this.vRepeatCount.setText("\u221e"); //знак бесконечности
        }
    }
}
