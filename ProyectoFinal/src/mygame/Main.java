package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Line;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {

    public static final Quaternion PITCH090 = new Quaternion().fromAngleAxis(FastMath.PI/2, new Vector3f(1,0,0));
    
    private static final Trigger TRIGGER_CHG_CAMERA = new KeyTrigger(KeyInput.KEY_SPACE);
    private static final String MAPPING_CHG_CAMERA = ("CHG_CAMERA");
    
    private ArrayList<Line> l;
    private Spatial spacial=null;
    
    private int maxEnemies;
    private int countEnemies;
    private ArrayList<Enemy> enemies;
    private int spawnedEnemies;
    Node enemies_node;
    //private static final Vector3f tower1_camlocation = new Vector3f(tower1_geom.getWorldTranslation().getX(), 2 * tower1_mesh.yExtent + floor_mesh.yExtent + 0.7f, tower1_geom.getWorldTranslation().getZ());
    
    Tower tower1;
    Tower tower2;
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true); //Creamos el objeto para controlar las especificaciones
        settings.setTitle("My Tower Defense Demo "); //Cambiamos el nombre de la ventana 
        //Integramos una imagen personal a la pantalla de inicio
        //settings.setSettingsDialogImage("Interface/Dona.png");
        //modificar la resolucion 
        settings.setResolution(1280, 960);
        //useInput establece si deseamos reaccionar a las entradas del mouse o teclado
        //settings.useInput(false)
        Main app = new Main();
        app.setSettings(settings);//Aplicamos las especificaciones a la app
        app.start();
    }
    

    @Override
    public void simpleInitApp() {
        inputManager.addMapping(MAPPING_CHG_CAMERA, TRIGGER_CHG_CAMERA);
        
        
        //Para controlar si se oculta la informacion de los objetos, mallas y sombras
        setDisplayFps(false);
        setDisplayStatView(false);

        //El objeto flayCam esta instanciado por defecto, al extender SimpleApplication
        flyCam.setMoveSpeed(10.8f); //Determinamos que la camara se mueva a una mayor velocidad
        
        //Cambiaremos la ubicacion y rotacion de la camara para dar la perspectiva que requiere el escenario
        cam.setLocation(new Vector3f(0, 40, 15));
        
        cam.setRotation(PITCH090);
        
        Node playerNode = new Node("player_node"), towerNode = new Node("tower_node"), creepNode = new Node("creep_node");
        
        //Se define la caja color naranja que sera el piso
        //Recuerda la tecla 0 Mueve hacia arriba la camara
        //tecla Z mueve hacia abajo la camara
        Box floor_mesh = new Box(20, 0.5f, 50);
        Box spawn_mesh = new Box(20, 0.5f, 10);
        Geometry floor_geom = new Geometry("floor", floor_mesh);
        Geometry spawn_geom = new Geometry("spawn", spawn_mesh);
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material spawn_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.setColor("Color", ColorRGBA.Orange);
        spawn_mat.setColor("Color", ColorRGBA.Yellow);
        floor_geom.setMaterial(floor_mat);
        spawn_geom.setMaterial(spawn_mat);
        //Utilizaremos las dimensiones de la malla del piso para definir la posicion de unos elementos
        floor_geom.setLocalTranslation(0, 0, floor_mesh.zExtent);
        spawn_geom.setLocalTranslation(0, 0, -spawn_mesh.zExtent);
        
        Box castle_mesh = new Box(20,10,1);
        Geometry castle_geom = new Geometry("castle", castle_mesh);
        
        Material castle_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        castle_mat.setColor("Color", ColorRGBA.Magenta);
        castle_geom.setMaterial(castle_mat);
        
        float towerX  = 1;
        float towerY = 10;
        float towerZ = 1;
        tower1 = new Tower("tower1", "", new Vector3f(floor_mesh.xExtent + towerX, 0, -(towerZ * 5)), towerX, towerY, towerZ , assetManager);
        tower2 = new Tower("tower2", "", new Vector3f(-(floor_mesh.xExtent + towerX), 0, -(towerZ * 5)), towerX, towerY, towerZ , assetManager);
        Node towers_node = new Node();
        towers_node.attachChild(castle_geom);
        towers_node.attachChild(tower1.getGeom());
        towers_node.attachChild(tower2.getGeom());
        rootNode.attachChild(towers_node);
        rootNode.attachChild(floor_geom);
        rootNode.attachChild(spawn_geom);
        towers_node.setLocalTranslation(0, castle_mesh.yExtent, (2 * floor_mesh.zExtent) + castle_mesh.zExtent);        
        
        l = new ArrayList<>();
        l.add(new Line(new Vector3f(-10,4.25f,-6), new Vector3f(10,4.25f,-6)));
        l.add(new Line(new Vector3f(-10,4.25f,-8), new Vector3f(10,4.25f,-8)));
        l.add(new Line(new Vector3f(-10,4.25f,-10), new Vector3f(10,4.25f,-10)));
        l.add(new Line(new Vector3f(-10,4.25f,-12), new Vector3f(10,4.25f,-12)));
        l.add(new Line(new Vector3f(-10,4.25f,-14), new Vector3f(10,4.25f,-14)));
        
        maxEnemies = 50;
        countEnemies = 0;
        spawnedEnemies = 0;
        enemies = new ArrayList<>();
        
        enemies_node = new Node();
        rootNode.attachChild(enemies_node);
        flyCam.setMoveSpeed(100);
    }
    
    private final ActionListener actionListener = new ActionListener(){

        @Override
        public void onAction(String name, boolean isPressed, float tpf){
            System.out.println("yout triggered" + name);
            
            if(name.equals(MAPPING_CHG_CAMERA) && !isPressed) {
                
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        Vector3f randomPoint = l.get((int) (Math.random()*5)).random();
        
        if(countEnemies < maxEnemies){
            enemies.add(new Enemy(("enemy" + spawnedEnemies), "", l.get((int) (Math.random()*5)).random(), 1,4,1, assetManager));
            spawnedEnemies++;
            countEnemies++;
            enemies_node.attachChild(enemies.get(enemies.size() - 1).getGeom());
        }
        enemies_node.move(0, 0, 0.001f);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}