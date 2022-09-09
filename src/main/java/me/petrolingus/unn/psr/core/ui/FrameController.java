package me.petrolingus.unn.psr.core.ui;

import me.petrolingus.unn.psr.core.Configuration;
import me.petrolingus.unn.psr.opengl.RuntimeConfiguration;

import java.util.concurrent.TimeUnit;

public class FrameController implements Runnable {

    @Override
    public void run() {

        double FPS_TARGET;
        double FRAME_TIME_TARGET;
        long frameStart = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted()) {
            if (!RuntimeConfiguration.running) {
                try {
                    TimeUnit.MILLISECONDS.sleep(32);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }
            FPS_TARGET = UiConfiguration.frameRate;
            FRAME_TIME_TARGET = 1000.0 / FPS_TARGET;
            long frameStop = System.currentTimeMillis();
            if (frameStop - frameStart > FRAME_TIME_TARGET && UiConfiguration.autoplay) {
                RuntimeConfiguration.currentFrame++;
                RuntimeConfiguration.currentFrame = RuntimeConfiguration.currentFrame % RuntimeConfiguration.maxFrame;
                frameStart = frameStop;
            }
        }

    }
}
