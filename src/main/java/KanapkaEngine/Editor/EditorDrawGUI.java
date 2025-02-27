package KanapkaEngine.Editor;

import KanapkaEngine.Components.Component;
import KanapkaEngine.Components.RectangleCollider;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Components.Rigidbody;
import KanapkaEngine.Editor.Attributes.*;
import KanapkaEngine.Game.*;
import KanapkaEngine.RenderLayers.NodeLayer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import org.joml.Vector2d;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

public class EditorDrawGUI implements IDrawGUI {
    private Vector2d windowPos = new Vector2d(0, 0);
    private Vector2d windowSize = new Vector2d(500, Engine.getMainInstance().getWindow().getHeight());

    private List<Node> openNodes = new ArrayList<>();

    private double[] data = new double[2];

    private final List<Component> components = new ArrayList<>();

    private ImBoolean displayHierarchy = new ImBoolean(false);
    private ImBoolean displayResourceExplorer = new ImBoolean(false);

    ImString ref_name = new ImString();

    ImBoolean component_boolean = new ImBoolean();
    int[] component_int = new int[1];
    float[] component_float = new float[1];
    double[] component_double = new double[1];

    ImString component_string = new ImString();

    private int node_id = 0;

    private Node selected;

    private Engine engine;

    protected EditorDrawGUI() {
        this.engine = Engine.getMainInstance();
    }

    public void RenderGUI() {
        Window window = engine.getWindow();

        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("Windows")){

                if (ImGui.checkbox("Hierarchy", displayHierarchy)) {}
                if (ImGui.checkbox("Resource Explorer", displayResourceExplorer)) {}

                ImGui.endMenu();
            }

            if (ImGui.beginMenu("Draw")) {
                component_boolean.set(NodeLayer.DRAW_WIREFRAME);
                if (ImGui.checkbox("Draw WireFrame", component_boolean)) {

                }
                NodeLayer.DRAW_WIREFRAME = component_boolean.get();
                ImGui.endMenu();
            }

            ImGui.endMainMenuBar();
        }

        ImGui.setNextWindowSize((float) windowSize.x, (float) windowSize.y);
        ImGui.setNextWindowPos((float) windowPos.x, (float) windowPos.y, ImGuiWindowFlags.AlwaysAutoResize);

        openNodes.clear();

        // First Time ever using imgui... tragedy
        DrawHeirarchy();
        DrawResourceExplorer();

        DrawCameraInfo();
    }

    private void DrawResourceExplorer() {
        if (displayResourceExplorer.get()) {
            if (ImGui.begin("Resource Explorer")) {
                if (ImGui.treeNode("Texures")) {

                    ImGui.treePop();
                }
            }
            ImGui.end();
        }
    }

    private void DrawHeirarchy() {
        if (displayHierarchy.get()) {
            if (ImGui.begin("Hierarchy (Its kinda shit but it works)")) {

                node_id = 0;
                if (ImGui.beginChild("##tree", new ImVec2(300, 0), ImGuiWindowFlags.NoCollapse)) {
                    if (SceneManager.hasScene()) {

                        SceneManager.getSceneNodes().forEach(node -> {
                            nodeRecursive(node);
                        });
                    }
                }

                ImGui.endChild();

                ImGui.sameLine();


                if (selected != null) {
                    if (ImGui.beginChild("##Editor", new ImVec2(0, 0))) {
                        ref_name.set(selected.name);
                        if (ImGui.inputText(" Name", ref_name)) {
                            selected.name = ref_name.get();
                        }
                        ImGui.sameLine();
                        if (ImGui.treeNodeEx("##" + ref_name.get() + "reset", 0, "Reset")) {
                            if (ImGui.button("Reset Position")) {
                                selected.transform.setPosition(0, 0);
                            }
                            else if (ImGui.button("Reset Scale")) {
                                selected.transform.setSize(1, 1);
                            }
                            else if (ImGui.button("Reset Rotation")) {
                                selected.transform.setRotation(0);
                            }
                            else if (ImGui.button("Reset Transform")) {
                                selected.transform.setPosition(0, 0);
                                selected.transform.setSize(1, 1);
                                selected.transform.setRotation(0);
                            }

                            ImGui.treePop();
                        }
                        ImGui.separator();
                        ImGui.textColored(0xff0000ff, "Transform");

                        Vector2d p = selected.transform.getPosition();
                        data[0] = p.x;
                        data[1] = p.y;
                        if (ImGui.dragScalarN("Position", data, 2, 0.031f)) {
                            selected.transform.setPosition(data[0], data[1]);
                        }

                        double[] r = { selected.transform.getRotation()};

                        if (ImGui.dragScalar("Rotation", r, 0.1f)) {
                            selected.transform.setRotation(r[0]);
                        }

                        Vector2d s = selected.transform.getSize();
                        data[0] = s.x;
                        data[1] = s.y;
                        if (ImGui.dragScalarN("Scale", data, 2, 0.031f)) {
                            selected.transform.setSize(data[0], data[1]);
                        }

                        components.clear();
                        selected.getComponents(components, Component.class);

                        ImGui.separator();
                        for (int i = 0; i < components.size(); i++) {
                            Component component = components.get(i);
                            ComponentRecursive(component);
                            if (i != components.size() - 1)
                                ImGui.separator();
                        }
                    }
                    ImGui.endChild();
                }


                ImVec2 vec2 = ImGui.getWindowPos();
                ImVec2 svec2 = ImGui.getWindowSize();

                windowPos.set(vec2.x, vec2.y);
                windowSize.set(svec2.x, svec2.y);
            }
            ImGui.end();
        }
    }

    private void nodeRecursive(Node node) {
        node_id++;
        ImGui.setNextItemWidth(300);
        String node_text = node_id + ". " + (node.name == null || node.name.isEmpty() ? "NoName" : node.name);
        if (ImGui.treeNodeEx("##TreeNodeN" + node_id, 0, node_text)) {
            if (ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                if (selected == node)
                    selected = null;
                else selected = node;
            }
            openNodes.add(node);
            for (int j = 0; j < node.childCount(); j++) {
                nodeRecursive(node.getChild(j));
            }
            ImGui.treePop();
        }
    }

    private void ComponentRecursive(Object component) {
        String isRenderer = (component instanceof Renderer ? " (Renderer)" : "");
        String isRigidbody = (component instanceof Rigidbody ? " (Physics)" : "");
        String isCollider = (component instanceof RectangleCollider ? " (Collider)" : "");
        String comp_text = component.getClass().getSimpleName() + isRenderer + isRigidbody + isCollider;
        if (ImGui.treeNode(comp_text)) {
            ImGui.separator();

            ImGui.text("Fields");

            for (Field field : component.getClass().getDeclaredFields()) {

                try {
                    field.setAccessible(true);
                } catch (InaccessibleObjectException e) {
                    if (ImGui.treeNode(field.getName() + " : Can't access oof")) {
                        ImGui.textWrapped(e.toString());
                        ImGui.treePop();
                    }
                    continue;
                }

                boolean canContinue = true;
                if (!Modifier.isPublic(field.getModifiers()))
                    canContinue = false;

                if (field.getAnnotation(HideEditor.class) != null || field.getType().getAnnotation(HideEditor.class) != null) {
                    canContinue = false;
                } else if (field.getAnnotation(NoViewEditor.class) != null || field.getType().getAnnotation(NoViewEditor.class) != null) {
                    ImGui.text(field.getName());
                    canContinue = false;
                }
                else if (field.getAnnotation(ReadOnly.class) != null) {
                    try {
                        ImGui.text(field.getName() + " { " + field.get(component) + " }");
                    } catch (IllegalAccessException e) {

                    }
                    canContinue = false;
                }
                else if (field.getAnnotation(Serialized.class) != null) {
                    canContinue = true;
                }

                if (!canContinue) {
                    field.setAccessible(false);
                    continue;
                }

                try {
                    if (field.getType() == boolean.class) {
                        component_boolean.set((Boolean) field.get(component));
                        if (ImGui.checkbox(field.getName(), component_boolean)) {
                            field.set(component, component_boolean.get());
                        }
                    } else if (field.getType() == int.class) {
                        component_int[0] = (Integer) field.get(component);
                        if (ImGui.dragScalar(field.getName(), component_int)) {
                            field.set(component, component_int[0]);
                        }
                    } else if (field.getType() == float.class) {
                        component_float[0] = (float) field.get(component);
                        if (ImGui.dragScalar(field.getName(), component_float, 0.01f)) {
                            field.set(component, component_float[0]);
                        }
                    } else if (field.getType() == double.class) {
                        component_double[0] = (double) field.get(component);
                        if (ImGui.dragScalar(field.getName(), component_double, 0.01f)) {
                            field.set(component, component_double[0]);
                        }
                    }
                    else if (field.getType() == String.class) {
                        component_string.set((String) field.get(component));
                        if (ImGui.inputText(field.getName(), component_string)) {
                            field.set(component, component_string.get());
                        }
                    }
                    else {
                        ComponentRecursive(field.get(component));
                    }
                } catch (IllegalAccessException e) {
                    if (ImGui.treeNode(field.getName() + " : Can't access oof")) {
                        ImGui.textWrapped(e.toString());
                        ImGui.treePop();
                    }
                }

                field.setAccessible(false);
            }

            ShowMethods showMethods = component.getClass().getAnnotation(ShowMethods.class);

            ImGui.separator();

            ImGui.text("Methods");

            for (Method method : component.getClass().getMethods()) {
                if (!(showMethods != null || method.getDeclaredAnnotation(Serialized.class) != null))
                    continue;
                try {
                    method.setAccessible(true);
                } catch (InaccessibleObjectException e) {
                    if (ImGui.treeNode(method.getName() + " : Can't access method oof")) {
                        ImGui.textWrapped(e.toString());
                        ImGui.treePop();
                    }
                    continue;
                }

                String prefix = "";
                int modifiers = method.getModifiers();

                if (Modifier.isPublic(modifiers))
                    prefix = "public";
                else if (Modifier.isPrivate(modifiers))
                    prefix = "private";
                else if (Modifier.isProtected(modifiers))
                    prefix = "protected";

                if (Modifier.isStatic(modifiers))
                    prefix += " static";

                if (Modifier.isFinal(modifiers))
                    prefix += " final";

                if (Modifier.isAbstract(modifiers))
                    prefix += " abstract";

                String methodName = prefix + " " + method.getReturnType().getName() + " " + method.getName() + "(";

                Class[] classes = method.getParameterTypes();
                for (int i = 0; i < classes.length; i++)
                    methodName += (i != 0 ?  " " : "") + classes[i].getName() + (i != classes.length - 1 ?  "," : "");

                methodName += ")";

                Serialized serialized = method.getDeclaredAnnotation(Serialized.class);
                if (classes.length == 0 && serialized != null) {
                    if (ImGui.button(serialized.methodName().isEmpty() ? methodName : serialized.methodName())) {
                        try {
                            method.invoke(component);
                        } catch (IllegalAccessException | InvocationTargetException e) {

                        }
                    }
                }
                else {
                    ImGui.text(methodName);
                }
            }

            ImGui.treePop();
        }
    }

    private void DrawCameraInfo() {
        ImGui.setNextWindowPos(0, 20);
        ImGui.begin("CameraInfo", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBackground);

        ImGui.text("Position=" + Camera.main.getPosition());
        ImGui.text("Rotation=" + Camera.main.getRotation());
        ImGui.text("Size=" + Camera.main.size);

        ImGui.end();
    }
}
