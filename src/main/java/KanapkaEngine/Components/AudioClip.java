package KanapkaEngine.Components;

import javax.sound.sampled.*;

/**
 * WIP
 */
public class AudioClip implements LineListener {
    public final Clip clip;
    private final FloatControl fc;

    private boolean isPlaying = false;

    public AudioClip(Clip clip) {
        this.clip = clip;
        this.fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(1f);
    }

    public float getVolume() {
        return (float) Math.pow(10f, fc.getValue() / 20f);
    }

    public void setVolume(float volume) {
        volume = (float)Mathf.Clamp(volume, 0.0, 1.0);
        fc.setValue((float)Mathf.Clamp(20f * (float) Math.log10(volume), fc.getMinimum(), fc.getMaximum()));
    }

    public void start() {
        this.clip.setFramePosition(0);
        this.clip.start();
        isPlaying = true;
    }

    public void stop() {
        isPlaying = false;
        this.clip.stop();
    }
    public void onStopped() {

    }

    public void onStarted() {

    }

    @Override
    public final void update(LineEvent event) {
        if (event.getType() == LineEvent.Type.START) {
            isPlaying = true;
            onStarted();
        }
        else if (event.getType() == LineEvent.Type.STOP) {
            isPlaying = false;
            onStopped();
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
