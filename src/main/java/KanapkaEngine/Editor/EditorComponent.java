package KanapkaEngine.Editor;

import KanapkaEngine.Components.IUpdate;
import KanapkaEngine.Components.TextureMaterial;
import KanapkaEngine.Editor.Attributes.Serialized;
import KanapkaEngine.Game.Component;
import KanapkaEngine.Game.Time;

public class EditorComponent extends Component implements IUpdate {

    @Serialized
    public double speed = 5.0;
    @Serialized
    public int limit = 3;
    @Serialized
    public int add = 0;

    @Override
    public void Update() {
        TextureMaterial material = (TextureMaterial) getParent().getRenderer().getMaterial();

        material.atlasIndex = (((int) (Time.time() * speed)) % limit) + add;
    }
}
