package KanapkaEngine.Components;

import KanapkaEngine.Time;

public class AudioSource extends Component {

    public AudioClip clip;

    public boolean playOnAwake = false;

    public float falloffDistance = 100f;

    private double udv_delay = Time.time();

    @Override
    public void Awake() {
        if (playOnAwake)
            play();
    }

    public final void play() {
        if (getParent() == null) return;
        if (Camera.main == null) return;
        if (isPlaying()) return;
        if (getP() > 0.99f) return;
        if (clip != null) {
            clip.clip.setFramePosition(0);

            updateDistanceVolume();
            clip.clip.start();
        }
    }

    @Override
    public void Update() {
        if (udv_delay < Time.time()) {
            updateDistanceVolume();

            udv_delay = Time.time() + 0.02;
        }
    }

    private void updateDistanceVolume() {

        float p = getP();

        clip.setVolume(1f - p);
    }

    private float getP() {
        float distance = (float) Mathf.aDistance(Camera.main.getWorldPosition(), getParent().transform.getPosition());
        return (float) Mathf.Clamp((distance) / falloffDistance, 0.0, 1.0);

    }

    public final boolean isPlaying() {
        return clip.clip.isRunning();
    }
}
