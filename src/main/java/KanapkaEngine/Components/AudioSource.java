package KanapkaEngine.Components;

import java.math.BigDecimal;

public class AudioSource extends NodeComponent {

    public AudioClip clip;

    public boolean playOnAwake = false;

    public float distanceFalloff = 0.1f;

    @Override
    void Start() {

    }

    @Override
    void Awake() {
        if (playOnAwake)
            play();
    }

    public final void play() {
        if (getParent() == null) return;
        if (Camera.main == null) return;
        if (isPlaying()) return;
        if (clip != null) {
            clip.clip.setFramePosition(0);
            float distance = (float) Mathf.distance(Camera.main.getPosition(), getParent().transform.getPosition());
            float p = (float) Mathf.Clamp((distance) / 1000.0f, 0.0, 1.0);
            clip.fc.setValue(p);
            System.out.println(clip.fc.getValue());
            clip.clip.start();

        }
    }

    public final boolean isPlaying() {
        return clip.clip.isRunning();
    }

    @Override
    void Update() {

    }

    @Override
    void onDestroy() {

    }

    @Override
    String toJSON() {
        return null;
    }
}
