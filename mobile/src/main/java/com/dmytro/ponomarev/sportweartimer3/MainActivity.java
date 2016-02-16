package com.dmytro.ponomarev.sportweartimer3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity {

    private static final int NOTIFICATION_ID = 1;
    private static Button buttonPauseResume;
    private static IntervalCountDownTimer timer;
    private static TextView vMainCounter;
    private static TextView vPauseCounter;
    private static TextView vPauseOnOff;
    private static TextView vRepeatCount;
    private static TextView vRepeatCountOnOff;
    private static View viewStartTimer;
    private static View viewStoptimer;
    private final long[] VIBRATION_FINISHED;
    private final long[] VIBRATION_MAINTIMER;
    private final long[] VIBRATION_PAUSETIMER;
    private SharedPreferences settings;
    private Vibrator vibrator;

    private IntervalCountDownTimer r2;

    private Button buttonStart;
    private ImageView iv;

    public MainActivity() {
        this.VIBRATION_FINISHED = new long[]{0, 1000, 100, 100};
        this.VIBRATION_MAINTIMER = new long[]{0, 500};
        this.VIBRATION_PAUSETIMER = new long[]{0, 300, 200, 300, 200};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (timer == null) {
            timer = new IntervalCountDownTimer();
            this.settings = getSharedPreferences("preferences", 0);
            timer.setMillisInFuture(this.settings.getLong("millisToFuture", 60000));
            timer.setMillisToPause(this.settings.getLong("millisToPause", 0));
            timer.setMillisToPauseEnabled(this.settings.getBoolean("millisToPauseEnabled", false));
            timer.setRepeatCount(this.settings.getLong("repeatCount", 0));
            timer.setRepeatCountEnabled(this.settings.getBoolean("repeatCountEnabled", false));
        }

        vMainCounter = (TextView) findViewById(R.id.counter_main);
        vPauseCounter = (TextView) findViewById(R.id.counter_pause);
        vPauseOnOff = (TextView) findViewById(R.id.pause_onoff);
        vRepeatCount = (TextView) findViewById(R.id.repeat);
        vRepeatCountOnOff = (TextView) findViewById(R.id.repeat_onoff);
        viewStartTimer = findViewById(R.id.timercontrol_start);
        viewStoptimer = findViewById(R.id.timercontrol_stop);
        buttonStart = (Button) findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new startTimer());
        iv = (ImageView) findViewById(R.id.button_stop);
        iv.setOnClickListener(new resetTimer());
        buttonPauseResume = (Button) findViewById(R.id.button_pause_resume);
        buttonPauseResume.setOnClickListener(new pauseResumeTimer());
        vMainCounter.setOnClickListener(new runningTimer());
        vPauseCounter.setOnClickListener(new editPauseCounter());
        vPauseOnOff.setOnClickListener(new clickPauseOnOff());
        vRepeatCount.setOnClickListener(new clickRepeatCount());
        vRepeatCountOnOff.setOnClickListener(new makeRepeatCountEnabled());
        timer.updateGui();

    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        boolean z = false;
        boolean z2 = true;
        if (resultCode == -1) {
            if (requestCode == NOTIFICATION_ID) {
                timer.setMillisInFuture(data.getLongExtra("millis", 0));
            } else if (requestCode == 2) {
                timer.setMillisToPause(data.getLongExtra("millis", 0));
                r2 = timer;
                if (timer.getMillisToPause() <= 0) {
                    z2 = false;
                }
                r2.setMillisToPauseEnabled(z2);
            } else if (requestCode == 3) {
                timer.setRepeatCount(data.getLongExtra("repeatCount", 0));
                r2 = timer;
                if (timer.getRepeatCount() > 0 || timer.getRepeatCount() == -1) {
                    z = true;
                }
                r2.setRepeatCountEnabled(z);
            }
            timer.updateGui();
        }
    }

    public static long getHours(long millis) {
        return TimeUnit.MILLISECONDS.toHours(millis);
    }

    public static long getMinutes(long millis) {
        return TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis));
    }

    public static long getSeconds(long millis) {
        return TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis));
    }

    public static long getMilliseconds(long hours, long minutes, long seconds) {
        return 1000 * ((((hours * 60) * 60) + (60 * minutes)) + seconds);
    }

    private void showNotification(String title, long[] vibrationSequence) {
        this.vibrator.vibrate(vibrationSequence, -1);
        Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
    }

    class startTimer implements View.OnClickListener {
        startTimer() {
        }

        public void onClick(View v) {
            if (MainActivity.timer.getMillisInFuture() == 0) {
                Toast.makeText(MainActivity.this, R.string.timer_start_error, Toast.LENGTH_SHORT).show();
            } else if (!MainActivity.timer.isRunning()) {
                MainActivity.timer.start();
                MainActivity.timer.updateGui();
            }
        }
    }

    class resetTimer implements View.OnClickListener {
        resetTimer() {
        }

        public void onClick(View v) {
            if (MainActivity.timer.isRunning()) {
                MainActivity.timer.reset();
                MainActivity.timer.updateGui();
            }
        }
    }

    class pauseResumeTimer implements View.OnClickListener {
        pauseResumeTimer() {
        }

        public void onClick(View v) {
            if (MainActivity.timer.isPaused()) {
                MainActivity.timer.resume();
                MainActivity.buttonPauseResume.setText(R.string.button_pause);
            } else {
                MainActivity.timer.pause();
                MainActivity.buttonPauseResume.setText(R.string.button_resume);
            }
            MainActivity.timer.updateGui();
        }
    }

    class runningTimer implements View.OnClickListener {
        runningTimer() {
        }

        public void onClick(View v) {
            if (MainActivity.timer.isRunning()) {
                Toast.makeText(MainActivity.this, R.string.edit_timer_running, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this.getApplicationContext(), ConfigureTimerActivityMobile.class);
            intent.putExtra("millis", MainActivity.timer.getMillisInFuture());
            MainActivity.this.startActivityForResult(intent, MainActivity.NOTIFICATION_ID);
        }
    }

    class editPauseCounter implements View.OnClickListener {
        editPauseCounter() {
        }

        public void onClick(View v) {
            if (MainActivity.timer.isRunning()) {
                Toast.makeText(MainActivity.this, R.string.edit_timer_running, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this.getApplicationContext(), ConfigureTimerActivityMobile.class);
            intent.putExtra("millis", MainActivity.timer.getMillisToPause());
            MainActivity.this.startActivityForResult(intent, 2);
        }
    }

    class clickPauseOnOff implements View.OnClickListener {
        clickPauseOnOff() {
        }

        public void onClick(View v) {
            MainActivity.timer.setMillisToPauseEnabled(!MainActivity.timer.getMillisToPauseEnabled());
            MainActivity.timer.updateGui();
        }
    }

    class clickRepeatCount implements View.OnClickListener {
        clickRepeatCount() {
        }

        public void onClick(View v) {
            if (MainActivity.timer.isRunning()) {
                Toast.makeText(MainActivity.this, R.string.edit_timer_running, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(MainActivity.this.getApplicationContext(), ConfigureRepeatActivityMobile.class);
            intent.putExtra("repeatCount", MainActivity.timer.getRepeatCount());
            MainActivity.this.startActivityForResult(intent, 3);
        }
    }

    class makeRepeatCountEnabled implements View.OnClickListener {
        makeRepeatCountEnabled() {
        }

        public void onClick(View v) {
            MainActivity.timer.setRepeatCountEnabled(!MainActivity.timer.getRepeatCountEnabled());
            MainActivity.timer.updateGui();
        }
    }


    private class IntervalCountDownTimer {
        private long currentMillisInFuture;
        private long currentMillisToPause;
        private long currentRepeatCount;
        private CountDownTimer mainTimer;
        private long millisInFuture;
        private long millisToPause;
        private boolean millisToPauseEnable;
        private CountDownTimer pauseTimer;
        private boolean pauseTimerIsRunning;
        private boolean paused;
        private long repeatCount;
        private boolean repeatCountEnabled;
        private boolean running;

        class MainTimer extends CountDownTimer {
            MainTimer(long x0, long x1) {
                super(x0, x1);
            }

            public void onTick(long millisUntilFinished) {
                IntervalCountDownTimer.this.currentMillisInFuture = millisUntilFinished;
                IntervalCountDownTimer.this.updateGui();
            }

            public void onFinish() {
                Resources res = MainActivity.this.getResources();
                IntervalCountDownTimer.this.currentMillisInFuture = -1;
                IntervalCountDownTimer.this.updateGui();
                IntervalCountDownTimer.this.currentMillisInFuture = IntervalCountDownTimer.this.millisInFuture;
                if (!IntervalCountDownTimer.this.repeatCountEnabled || (IntervalCountDownTimer.this.currentRepeatCount <= 0 && IntervalCountDownTimer.this.currentRepeatCount != -1)) {
                    IntervalCountDownTimer.this.running = false;
                    MainActivity.this.showNotification(res.getText(R.string.n_text_finished).toString(), MainActivity.this.VIBRATION_FINISHED);
                } else {
                    String notificationText;
                    if (IntervalCountDownTimer.this.currentRepeatCount > 0) {
                        IntervalCountDownTimer.this.currentRepeatCount = IntervalCountDownTimer.this.currentRepeatCount - 1;
                    }
                    if (!IntervalCountDownTimer.this.millisToPauseEnable || IntervalCountDownTimer.this.millisToPause <= 0) {
                        IntervalCountDownTimer.this.mainTimer = IntervalCountDownTimer.this.getNewMainTimer(IntervalCountDownTimer.this.millisInFuture);
                        IntervalCountDownTimer.this.mainTimer.start();
                    } else {
                        IntervalCountDownTimer.this.pauseTimer = IntervalCountDownTimer.this.getNewPauseTimer(IntervalCountDownTimer.this.millisToPause);
                        IntervalCountDownTimer.this.pauseTimerIsRunning = true;
                        IntervalCountDownTimer.this.pauseTimer.start();
                    }
                    if (IntervalCountDownTimer.this.currentRepeatCount == -1) {
                        notificationText = res.getText(R.string.n_text_infinity).toString();
                    } else {
                        int access$500 = ((int) IntervalCountDownTimer.this.currentRepeatCount) + MainActivity.NOTIFICATION_ID;
                        Object[] objArr = new Object[MainActivity.NOTIFICATION_ID];
                        objArr[0] = Long.valueOf(IntervalCountDownTimer.this.currentRepeatCount + 1);
                        notificationText = res.getQuantityString(R.plurals.n_text, access$500, objArr);
                    }
                    MainActivity.this.showNotification(notificationText, MainActivity.this.VIBRATION_MAINTIMER);
                }
                IntervalCountDownTimer.this.updateGui();
            }
        }

        class PauseTimer extends CountDownTimer {
            PauseTimer(long x0, long x1) {
                super(x0, x1);
            }

            public void onTick(long millisUntilFinished) {
                IntervalCountDownTimer.this.currentMillisToPause = millisUntilFinished;
                IntervalCountDownTimer.this.updateGui();
            }

            public void onFinish() {
                IntervalCountDownTimer.this.currentMillisToPause = -1;
                IntervalCountDownTimer.this.updateGui();
                IntervalCountDownTimer.this.currentMillisToPause = IntervalCountDownTimer.this.millisToPause;
                IntervalCountDownTimer.this.pauseTimerIsRunning = false;
                Resources res = MainActivity.this.getResources();
                MainActivity.this.showNotification(res.getText(R.string.n_text_pause_finished).toString(), MainActivity.this.VIBRATION_PAUSETIMER);
                IntervalCountDownTimer.this.mainTimer = IntervalCountDownTimer.this.getNewMainTimer(IntervalCountDownTimer.this.millisInFuture);
                IntervalCountDownTimer.this.mainTimer.start();
                IntervalCountDownTimer.this.updateGui();
            }
        }

        public IntervalCountDownTimer() {
            this.running = false;
            this.paused = false;
            this.pauseTimerIsRunning = false;
        }

        public void start() {
            startInternal(this.millisInFuture, this.repeatCount, this.millisToPause);
        }

        private void startInternal(long millisInFuture, long repeatCount, long millisToPause) {
            this.currentMillisInFuture = millisInFuture;
            this.currentRepeatCount = repeatCount;
            this.currentMillisToPause = millisToPause;
            if (this.pauseTimerIsRunning) {
                this.pauseTimer = getNewPauseTimer(this.currentMillisToPause);
                this.pauseTimer.start();
            } else {
                this.mainTimer = getNewMainTimer(millisInFuture);
                this.mainTimer.start();
            }
            this.running = true;
        }

        private CountDownTimer getNewMainTimer(long duration) {
            return new MainTimer(duration, 500);
        }

        private CountDownTimer getNewPauseTimer(long duration) {
            return new PauseTimer(duration, 500);
        }

        public void updateGui() {
            long msMainCounter = Math.min(this.currentMillisInFuture + 1000, this.millisInFuture + 999);
            long msPauseCounter = Math.min(this.currentMillisToPause + 1000, this.millisToPause + 999);
            long repeatsLeft = this.currentRepeatCount;
            if (!this.running) {
                msMainCounter = this.millisInFuture;
                msPauseCounter = this.millisToPause;
                repeatsLeft = this.repeatCount;
            }
            MainActivity.vMainCounter.setText(String.format(MainActivity.this.getResources().getString(R.string.fullTimeFormatString), new Object[]{Long.valueOf(MainActivity.getHours(msMainCounter)), Long.valueOf(MainActivity.getMinutes(msMainCounter)), Long.valueOf(MainActivity.getSeconds(msMainCounter))}));
            MainActivity.vPauseCounter.setText(String.format(MainActivity.this.getResources().getString(R.string.fullTimeFormatString), new Object[]{Long.valueOf(MainActivity.getHours(msPauseCounter)), Long.valueOf(MainActivity.getMinutes(msPauseCounter)), Long.valueOf(MainActivity.getSeconds(msPauseCounter))}));
            MainActivity.vPauseCounter.setTextColor(MainActivity.this.getResources().getColor(this.millisToPauseEnable ? R.color.timer_text : R.color.timer_text_inactive));
            if (this.repeatCount >= 0) {
                TextView access$2100 = MainActivity.vRepeatCount;
                Resources resources = MainActivity.this.getResources();
                int i = (int) repeatsLeft;
                Object[] objArr = new Object[MainActivity.NOTIFICATION_ID];
                objArr[0] = Long.valueOf(repeatsLeft);
                access$2100.setText(resources.getQuantityString(R.plurals.label_repeat, i, objArr));
            } else {
                MainActivity.vRepeatCount.setText(R.string.label_repeat_infitity);
            }
            MainActivity.vRepeatCount.setTextColor(MainActivity.this.getResources().getColor(this.repeatCountEnabled ? R.color.timer_text : R.color.timer_text_inactive));
            MainActivity.vPauseOnOff.setText(this.millisToPauseEnable ? R.string.option_on : R.string.option_off);
            MainActivity.vPauseOnOff.setBackgroundColor(MainActivity.this.getResources().getColor(this.millisToPauseEnable ? R.color.option_on_background : R.color.option_off_background));
            MainActivity.vRepeatCountOnOff.setText(this.repeatCountEnabled ? R.string.option_on : R.string.option_off);
            MainActivity.vRepeatCountOnOff.setBackgroundColor(MainActivity.this.getResources().getColor(this.repeatCountEnabled ? R.color.option_on_background : R.color.option_off_background));
            if (this.running) {
                MainActivity.viewStartTimer.setVisibility(View.GONE);
                MainActivity.viewStoptimer.setVisibility(View.VISIBLE);
                return;
            }
            MainActivity.viewStartTimer.setVisibility(View.VISIBLE);
            MainActivity.viewStoptimer.setVisibility(View.GONE);
        }

        public void pause() {
            if (this.running) {
                if (this.mainTimer != null) {
                    this.mainTimer.cancel();
                }
                if (this.pauseTimer != null) {
                    this.pauseTimer.cancel();
                }
                this.paused = true;
            }
        }

        public void resume() {
            if (this.running && this.paused && this.mainTimer != null) {
                this.paused = false;
                startInternal(this.currentMillisInFuture, this.currentRepeatCount, this.currentMillisToPause);
            }
        }

        public void reset() {
            pause();
            this.mainTimer = null;
            this.pauseTimer = null;
            this.running = false;
            this.paused = false;
            this.pauseTimerIsRunning = false;
        }

        public void setMillisInFuture(long millisInFuture) {
            this.millisInFuture = millisInFuture;
            MainActivity.this.settings.edit().putLong("millisToFuture", millisInFuture).apply();
        }

        public long getMillisInFuture() {
            return this.millisInFuture;
        }

        public void setRepeatCount(long repeatCount) {
            this.repeatCount = repeatCount;
            MainActivity.this.settings.edit().putLong("repeatCount", repeatCount).apply();
        }

        public long getRepeatCount() {
            return this.repeatCount;
        }

        public void setRepeatCountEnabled(boolean repeatCountEnabled) {
            this.repeatCountEnabled = repeatCountEnabled;
            MainActivity.this.settings.edit().putBoolean("repeatCountEnabled", repeatCountEnabled).apply();
            if (!repeatCountEnabled) {
                setMillisToPauseEnabled(false);
            }
        }

        public boolean getRepeatCountEnabled() {
            return this.repeatCountEnabled;
        }

        public void setMillisToPause(long millisToPause) {
            this.millisToPause = millisToPause;
            MainActivity.this.settings.edit().putLong("millisToPause", millisToPause).apply();
        }

        public long getMillisToPause() {
            return this.millisToPause;
        }

        public void setMillisToPauseEnabled(boolean millisToPauseEnabled) {
            this.millisToPauseEnable = millisToPauseEnabled;
            MainActivity.this.settings.edit().putBoolean("millisToPauseEnabled", millisToPauseEnabled).apply();
            if (millisToPauseEnabled) {
                MainActivity.timer.setRepeatCountEnabled(true);
                if (MainActivity.timer.getRepeatCount() <= 1) {
                    MainActivity.timer.setRepeatCount(1);
                }
            }
        }

        public boolean getMillisToPauseEnabled() {
            return this.millisToPauseEnable;
        }

        public boolean isRunning() {
            return this.running;
        }

        public boolean isPaused() {
            return this.paused;
        }
    }


}
