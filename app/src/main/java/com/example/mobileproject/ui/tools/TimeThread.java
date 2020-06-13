package com.example.mobileproject.ui.tools;

import android.os.Handler;
import android.widget.TextView;

import com.example.mobileproject.ui.slideshow.SlideshowFragment;

public class TimeThread extends Thread {
    private Handler handler;
    private int time = 30;
    private boolean stop, koreaned = true;
    private TextView txtTime = null;
    private ToolsFragment hf = null;
    private int start = 30;

    public TimeThread(Handler handler, int time, TextView txtTime, ToolsFragment hf) {
        this.handler = handler;
        this.time = time;
        this.txtTime = txtTime;
        this.hf = hf;
        start = time;
    }

    public void setKoreaned(boolean koreaned) {
        this.koreaned = koreaned;
    }

    public void stopThread() {
        stop = true;
    }

    public synchronized void reset(int time) {
        this.time = time;
    }

    public void run() {
        while(!stop) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (stop) break;
            time--;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (txtTime != null) txtTime.setText(Integer.toString(time));
                }
            });
            if (time == 0) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        hf.timeout(koreaned);
                        time = start;
                    }
                });
            }
        }
    }
}
