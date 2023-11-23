package KanapkaEngine.Game;

import KanapkaEngine.Engine;

public abstract class Plugin {
    public abstract void Apply(Engine engine);

    public abstract void Update();

    public abstract void Detach();
}
