package KanapkaEngine.Game;

import imgui.ImGui;
import imgui.ImVec2;

public interface IDrawGUI {
    void RenderGUI();

    /**
     * Literally just a function to be able to access the private texture id of the texture class with ImGui
     * @param texture
     * @param size
     * @param uv
     */
    default void ImGuiDrawImage(Texture texture, ImVec2 size, ImVec2 uv) {
        ImGui.image(texture.textureId, size, uv);
    }
}
