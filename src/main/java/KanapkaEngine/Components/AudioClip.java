package KanapkaEngine.Components;

import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;

public class AudioClip {
    public final Clip clip;
    public final FloatControl fc;

    public AudioClip(Clip clip) {
        this.clip = clip;
        this.fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(0f);
    }
}
