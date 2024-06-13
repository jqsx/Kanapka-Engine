package KanapkaEngine.Components;

import javax.sound.sampled.*;

/**
 * WIP
 */
public class AudioClip implements LineListener {
    public final Clip clip;
    public final FloatControl fc;

    public AudioClip(Clip clip) {
        this.clip = clip;
        this.fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(0f);
    }

    public void start() {
        this.clip.setFramePosition(0);
        this.clip.start();
    }

    public void stop() {
        this.clip.stop();
    }
    public void onStopped() {

    }

    public void onStarted() {

    }

    @Override
    public final void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.START)
            onStarted();
        else if (event.getType() == LineEvent.Type.STOP)
            onStopped();
    }
}
