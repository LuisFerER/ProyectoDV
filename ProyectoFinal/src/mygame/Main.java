package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Line;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Andrea Guadalupe Plascencia Rodriguez, Isaias Ricardo Valdivia Hernandez, Luis Fernando Escobedo Romero
 */
public class Main extends SimpleApplication {

    public static final Quaternion CAMINITIALROTATION = new Quaternion(-0.01f, 0.99f, -0.1f, -0.11f);
    
    private static final Trigger TRIGGER_CHG_CAMERA = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_SHOOTING = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    
    private static final String MAPPING_CHG_CAMERA = ("CHG_CAMERA");
    private static final String MAPPING_SHOOTING = "Shooting";
    
    private Vector3f tower1_camlocation;
    private Vector3f tower2_camlocation;
    
    private ArrayList<Line> l;
    private Spatial spacial=null;
    
    private Geometry castle_geom;
    private BoundingBox castle_bounds;
    
    
    private int maxEnemies;
    private int countEnemies;
    private ArrayList<Geometry> enemies;
    private int spawnedEnemies;
    private float frecEnemy;
    private float countdownToEnemy;
    Node enemies_node;
    
    float totalMoved;
        
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
        //para hacer uso de los triggers y mapping se deben registrar en el inputManager
        inputManager.addMapping(MAPPING_CHG_CAMERA, TRIGGER_CHG_CAMERA);
        inputManager.addMapping(MAPPING_SHOOTING, TRIGGER_SHOOTING);
        
        // Para poder activar los mapping debemos estar escuchando para detectar el input
        inputManager.addListener(actionListener, new String[]{MAPPING_CHG_CAMERA});
        inputManager.addListener(actionListener, new String[]{MAPPING_SHOOTING});
        
        //Para controlar si se oculta la informacion de los objetos, mallas y sombras
        setDisplayFps(false);
        setDisplayStatView(false);
        
        cam.setRotation(CAMINITIALROTATION);
        
        
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
        floor_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/floor.jpg"));
        spawn_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/spawn.jpg"));
        //floor_mat.setColor("Color", ColorRGBA.Orange);
        //spawn_mat.setColor("Color", ColorRGBA.Yellow);
        floor_geom.setMaterial(floor_mat);
        spawn_geom.setMaterial(spawn_mat);
        //Utilizaremos las dimensiones de la malla del piso para definir la posicion de unos elementos
        floor_geom.setLocalTranslation(0, 0, floor_mesh.zExtent);
        spawn_geom.setLocalTranslation(0, 0, -spawn_mesh.zExtent);
        
        Box castle_mesh = new Box(20,10,1);
        castle_geom = new Geometry("castle", castle_mesh);
        
        Material castle_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        castle_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/castle_door.jpg"));
        //castle_mat.setColor("Color", ColorRGBA.Magenta);
        castle_geom.setMaterial(castle_mat);
        
        castle_bounds = new BoundingBox(new Vector3f(0, 10, 101), 20,10,1);
        
        float towerX  = 3;
        float towerY = 10;
        float towerZ = 3;
        tower1 = new Tower("tower1", "Textures/tower.jpg", "Textures/cubierta.jpg", new Vector3f(floor_mesh.xExtent + towerX, 0, -(towerZ * 5)), towerX, towerY, towerZ , assetManager);
        tower2 = new Tower("tower2", "Textures/tower.jpg", "Textures/cubierta.jpg", new Vector3f(-(floor_mesh.xExtent + towerX), 0, -(towerZ * 5)), towerX, towerY, towerZ , assetManager);
        Node towers_node = new Node();
        towers_node.attachChild(castle_geom);
        towers_node.attachChild(tower1.getGeom());
        towers_node.attachChild(tower2.getGeom());
        towers_node.attachChild(tower1.getCubierta_geom());
        towers_node.attachChild(tower2.getCubierta_geom());
        rootNode.attachChild(towers_node);
        rootNode.attachChild(floor_geom);
        rootNode.attachChild(spawn_geom);
        towers_node.setLocalTranslation(0, castle_mesh.yExtent, (2 * floor_mesh.zExtent) + castle_mesh.zExtent);        
        
        System.out.println("castle_geom:\t" + castle_geom.getWorldTranslation());
        
        l = new ArrayList<>();
        l.add(new Line(new Vector3f(-10,4.25f,-6), new Vector3f(10,4.25f,-6)));
        l.add(new Line(new Vector3f(-10,4.25f,-8), new Vector3f(10,4.25f,-8)));
        l.add(new Line(new Vector3f(-10,4.25f,-10), new Vector3f(10,4.25f,-10)));
        l.add(new Line(new Vector3f(-10,4.25f,-12), new Vector3f(10,4.25f,-12)));
        l.add(new Line(new Vector3f(-10,4.25f,-14), new Vector3f(10,4.25f,-14)));
        
        maxEnemies = 50;
        countEnemies = 0;
        spawnedEnemies = 0;
        frecEnemy = 5;
        countdownToEnemy = frecEnemy;
        enemies = new ArrayList<>();
        
        enemies_node = new Node();
        rootNode.attachChild(enemies_node);
        
        totalMoved = 0;
        
        tower1_camlocation = new Vector3f(tower1.getGeom().getWorldTranslation().getX()-2f, 2 * tower1.getMesh().yExtent + floor_mesh.yExtent + 0.7f, tower1.getGeom().getWorldTranslation().getZ()-2f);
        tower2_camlocation = new Vector3f(tower2.getGeom().getWorldTranslation().getX()+2f, 2 * tower2.getMesh().yExtent + floor_mesh.yExtent + 0.7f, tower2.getGeom().getWorldTranslation().getZ()-2f);

        flyCam.setMoveSpeed(0);
        cam.setLocation(tower1_camlocation);
        
        // La mira que indica la posicion del mouse es inicializada
        attachCenterMark();
    }
    
    /**
     * attachCenterMarck crea un objeto geometry que servira de mira para apuntar 
     * diferentes objetos en el escenario. Ya que es una marca 2D, se debe adjuntar 
     * a la interface 2D del usurio "guiNode", este objeto es intanciado en 
     * cualquier SimpleApplication.
     */
    private void attachCenterMark(){
        Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("centermark", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        geom.setLocalTranslation(Vector3f.ZERO);
        geom.scale(4);
        geom.setLocalTranslation(settings.getWidth()/2, settings.getHeight()/2, 0);
        guiNode.attachChild(geom); //adjunta a la interface 2D del usuario
    }
    
    private final ActionListener actionListener = new ActionListener(){

        @Override
        public void onAction(String name, boolean isPressed, float tpf){
            System.out.println("yout triggered : " + name);
            
            if(name.equals(MAPPING_CHG_CAMERA) && !isPressed) {
                Vector3f posicion = cam.getLocation();
                if(posicion.equals(tower1_camlocation)){
                    cam.setLocation(tower2_camlocation);
                }else{
                    if(posicion.equals(tower2_camlocation)){
                        cam.setLocation(tower1_camlocation);
                    }
                }
            }
            else if(name.equals(MAPPING_SHOOTING) && !isPressed){
                // se comprueba que el trigger indentificado corresponda a la acciÃ³n deseada
                if(name.equals(MAPPING_SHOOTING)){
                    //En esta seccion determinamos la accion de disparar al enemico que este apuntando
                    //la mira del mouse.
                    //colision identificara el objeto al cual se le hace click
                    CollisionResults results = new CollisionResults();
                    //Se proyecta una linea de acuerdo a la posicion de la camara, en la
                    //direccion donde la camara esta apuntando
                    Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                    //calculamos si este rayo proyecto hace colision con el objeto
                    rootNode.collideWith(ray, results);

                    //si el usuario ha hecho click en algo, indentifacaremos la geometria seleccionada
                    if(results.size()>0){
                        Geometry target = results.getClosestCollision().getGeometry();
                        System.out.println(cam.getRotation());
                        if(target.getName().contains("enemy")){
                            System.out.println(target.getName());
                            Geometry temp = (Geometry) enemies_node.getChild(target.getName());
                            enemies_node.detachChildNamed(target.getName());
                            enemies.remove(temp);
                            temp = null;
                            countEnemies--;
                        }

                    }
                }
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        countdownToEnemy = countdownToEnemy - tpf;
        if(countEnemies < maxEnemies && countdownToEnemy <= 0){
            float size = 2;
            enemies.add(new Enemy(("enemy" + spawnedEnemies),assetManager.loadTexture("Textures/ball.jpg") , size, assetManager).getGeom());
            spawnedEnemies++;
            countEnemies++;
            enemies_node.attachChild(enemies.get(enemies.size() - 1));
            enemies.get(enemies.size() - 1).move(size, (float) (size + Math.random()*7), size - totalMoved);
            countdownToEnemy = frecEnemy;
            if(spawnedEnemies % 5 == 0){
                if(frecEnemy > 0.5)
                    frecEnemy = (float) (frecEnemy / 1.5);
                if(maxEnemies < 50)
                    maxEnemies = (int) (maxEnemies * 1.5);
            }
        }
        
        if(countEnemies > 0){
            enemies_node.move(0, 0, 0.01f);
            totalMoved += 0.01;
            CollisionResults results = new CollisionResults();
            castle_bounds.collideWith(enemies_node, results);
            if (results.size() > 0) {
                CollisionResult closest  = results.getClosestCollision();
                JPanel panel = new JPanel();
            panel.add(new JLabel("Has Perdido :C"));
            JOptionPane.showMessageDialog(null, panel,"Derrota",JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            }
        }   
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}