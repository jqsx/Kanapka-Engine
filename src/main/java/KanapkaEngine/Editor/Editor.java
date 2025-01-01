package KanapkaEngine.Editor;

import KanapkaEngine.Components.*;
import KanapkaEngine.Components.Component;
import KanapkaEngine.Components.Renderer;
import KanapkaEngine.Editor.Attributes.*;
import KanapkaEngine.Game.Engine;
import KanapkaEngine.Game.*;
import KanapkaEngine.Game.Window;
import KanapkaEngine.RenderLayers.ChunkLayer;
import KanapkaEngine.RenderLayers.NodeLayer;
import KanapkaEngine.RenderLayers.ParticleLayer;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.*;
import imgui.type.*;
import org.joml.Vector2d;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Editor {
    private static boolean editor = false;

    private static Logger logger = new Logger("EDITOR");

    public static void StartEditor() {
        editor = true;

        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.FPSLIMIT = -1;
        engineConfiguration.width = 800;
        engineConfiguration.height = 600;
        engineConfiguration.window_title = "Editor";

        Engine engine = new Engine(new GameLogic() {

            private double spawnDelay = 0;

            private double particleSpawnDelay = 0;

            private Texture texture;
            private ParticleSystem<Particle> particleSystem;

            @Override
            public void Start(Engine engine) {
                RenderLayer.register(new NodeLayer());
                RenderLayer.register(new ChunkLayer());
                RenderLayer.register(new ParticleLayer());

                engine.load(new SimpleViewController());

                GuiRenderer guiRenderer = new GuiRenderer(new IDrawGUI() {
                    private Vector2d windowPos = new Vector2d(0, 0);
                    private Vector2d windowSize = new Vector2d(500, engine.getWindow().getHeight());

                    private Node selected;

                    private ImString ref_Name = new ImString();

                    private double[] data = new double[2];

                    private final List<Component> components = new ArrayList<>();

                    private ImBoolean displayHierarchy = new ImBoolean(true);

                    ImBoolean component_boolean = new ImBoolean();
                    int[] component_int = new int[1];
                    float[] component_float = new float[1];
                    double[] component_double = new double[1];

                    ImString component_string = new ImString();

                    public void RenderGUI() {
                        Window window = engine.getWindow();

                        if (ImGui.beginMainMenuBar()) {
                            if (ImGui.beginMenu("Windows")){

                                if (ImGui.checkbox("Hierarchy", displayHierarchy)) {}

                                ImGui.endMenu();
                            }
                            ImGui.endMainMenuBar();
                        }

                        ImGui.setNextWindowSize((float) windowSize.x, (float) windowSize.y);
                        ImGui.setNextWindowPos((float) windowPos.x, (float) windowPos.y, ImGuiWindowFlags.AlwaysAutoResize);

                        // First Time ever using imgui... tragedy
                        if (displayHierarchy.get()) {
                            if (ImGui.begin("Hierarchy (Its kinda shit but it works)")) {

                                selected = null;

                                if (ImGui.beginChild("##tree", new ImVec2(300, 0), ImGuiWindowFlags.NoCollapse)) {
                                    AtomicInteger i = new AtomicInteger(0);
                                    if (SceneManager.hasScene()) {

                                        SceneManager.getSceneNodes().forEach(node -> {
                                            nodeRecursive(node, i.get());
                                            i.incrementAndGet();
                                        });
                                    }
                                }

                                if (selected != null)
                                    ref_Name.set(selected.name);
                                ImGui.endChild();

                                ImGui.sameLine();

                                if (ImGui.beginChild("##Editor", new ImVec2(0, 0))) {
                                    if (this.selected != null) {
                                        if (ImGui.inputText("Name", ref_Name)) {
                                            this.selected.name = ref_Name.get();
                                        }
                                        ImGui.separator();
                                        ImGui.textColored(0xff0000ff, "Transform");

                                        Vector2d p = this.selected.transform.getPosition();
                                        data[0] = p.x;
                                        data[1] = p.y;
                                        if (ImGui.dragScalarN("Position", data, 2, 0.031f)) {
                                            this.selected.transform.setPosition(data[0], data[1]);
                                        }

                                        double[] r = {this.selected.transform.getRotation()};

                                        if (ImGui.dragScalar("Rotation", r, 0.1f)) {
                                            this.selected.transform.setRotation(r[0]);
                                        }

                                        Vector2d s = this.selected.transform.getSize();
                                        data[0] = s.x;
                                        data[1] = s.y;
                                        if (ImGui.dragScalarN("Scale", data, 2, 0.031f)) {
                                            this.selected.transform.setSize(data[0], data[1]);
                                        }

                                        components.clear();
                                        this.selected.getComponents(components, Component.class);

                                        ImGui.separator();
                                        if (ImGui.treeNode("Components")) {
                                            for (int i = 0; i < components.size(); i++) {
                                                Component component = components.get(i);
                                                ComponentRecursive(component);
                                            }
                                            ImGui.treePop();
                                        }
                                    }
                                }
                                ImGui.endChild();


                                ImVec2 vec2 = ImGui.getWindowPos();
                                ImVec2 svec2 = ImGui.getWindowSize();

                                windowPos.set(vec2.x, vec2.y);
                                windowSize.set(svec2.x, svec2.y);
                            }
                            ImGui.end();
                        }
                    }

                    private void nodeRecursive(Node node, int i) {
                        ImGui.setNextItemWidth(300);
                        String node_text = i + ". " + (node.name == null || node.name.isEmpty() ? "NoName" : node.name);
                        if (ImGui.treeNode(node_text)) {
                            selected = node;
                            for (int j = 0; j < node.childCount(); j++) {
                                nodeRecursive(node.getChild(j), j);
                            }
                            ImGui.treePop();
                        }
                    }

                    private void ComponentRecursive(Object component) {
                        String comp_text = component.getClass().getName();
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
                });

                RenderLayer.register(guiRenderer);
                engine.load(guiRenderer);

                {
                    particleSystem = new ParticleSystem<>();

                    particleSystem.setTexture(new Texture(ResourceLoader.loadResource("logo.png")));

                    particleSystem.setInstanced();

                    Node sys = new Node();

                    sys.addComponent(particleSystem);

                    sys.append();
                }

                texture = new Texture(ResourceLoader.loadResource("logo.png"));

                GenerateChunk(0, 0);
                GenerateChunk(1, 0);
            }

            private void createSandwich(double x, double y) {
                Node node = new Node();

                node.transform.setPosition(x, y);

                TextureMaterial material = new TextureMaterial();
                material.MainTex = texture;

                Renderer renderer = new Renderer();
                renderer.setMaterial(material);
                renderer.setTexture(texture);

                node.addComponent(renderer);

                node.append();
            }

            @Override
            public void Update() {

                if (spawnDelay < Time.time() && Input.isKeyDown(GLFW_KEY_SPACE)) {
                    spawnDelay = Time.time() + 0.1;

                    Vector2d wL = Input.getWorldMousePosition();
                    createSandwich(wL.x, wL.y);
                }

                if (particleSpawnDelay < Time.time()) {
                    particleSpawnDelay = Time.time() + 0.05;

                    particleSystem.SpawnOffset(new Vector2d(0, 0)).addVelocity(new Vector2d(10, 0));

                }
            }

            @Override
            public void End() {

            }

            private void GenerateChunk(int x, int y) {
                Chunk chunk = Chunk.build(new Point(x, y), World.getCurrent());

                for (int _x = 0; _x < SceneManager.getCurrentlyLoaded().getChunkSize(); _x++) {
                    for (int _y = 0; _y < SceneManager.getCurrentlyLoaded().getChunkSize(); _y++) {
                        if ((_x + _y) % 2 == 0)
                            chunk.createBlock(0, new Point(_x, _y));
                    }
                }

                chunk.ready();
                chunk.activate();

                World.getCurrent().set(chunk);
            }
        }, engineConfiguration);
    }
}
