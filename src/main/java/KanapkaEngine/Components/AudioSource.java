package KanapkaEngine.Components;

public class AudioSource extends Component {

    public AudioClip clip;

    public boolean playOnAwake = false;

    public float distanceFalloff = 0.1f;

    @Override
    public void Awake() {
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
}
