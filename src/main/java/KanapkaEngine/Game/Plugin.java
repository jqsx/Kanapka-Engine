package KanapkaEngine.Game;

/**
 *  A basic accessor to the engine's loop
 *  <br><br>
 *  Can use ICleanUp if in need of disposing of assets.
 */
public interface Plugin {
    void Apply(Engine engine);

    void Update();

    void Detach();
}
