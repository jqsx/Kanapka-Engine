package KanapkaEngine.Game;

public interface IInput {
    void KeyCallBack(int key, int scancode, int action, int mods);
    void CharKeyCallback(int c);
    void ScrollCallback(double x, double y);
}
