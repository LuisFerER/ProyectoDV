package mygame;
import com.jme3.app.SimpleApplication;
import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Line;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author Andrea Guadalupe Plascencia Rodriguez, Isaias Ricardo Valdivia Hernandez, Luis Fernando Escobedo Romero
 */
public class Main extends SimpleApplication {

    //Se usa un Quaternion para establecer la mira apuntando hacia el spawn enemigo
    public static final Quaternion CAMINITIALROTATION = new Quaternion(-0.01f, 0.99f, -0.1f, -0.11f);
    
    //constantes triggers que representan los clicks de la barra y el mouse
    //los triggers son los objetos que representan las entradas fisicas de los clicks o los joysticks
    private static final Trigger TRIGGER_CHG_CAMERA = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_SHOOTING = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    
    //definimos las constantes que nos ayduaran a identificar las acciones por los triggers
    //Una accion puede tener mas de un trigger activada
    private static final String MAPPING_CHG_CAMERA = ("CHG_CAMERA");
    private static final String MAPPING_SHOOTING = "Shooting";
    
    //variables para establecer la localizacion de la camara arriba de las torres
    private Vector3f tower1_camlocation;
    private Vector3f tower2_camlocation;
    
    //Se define un ArrayList que contendra las lineas en donde apareceran loe enemigos
    private ArrayList<Line> l;
    
    //se define la geometria y la caja limitadora (BoundingBox)
    private Geometry castle_geom;
    private BoundingBox castle_bounds;
    
    
    private int maxEnemies; //variable que representa el maximo de enemigos
    private int countEnemies; //variable que representa el contador de enemigos
    private ArrayList<Geometry> enemies; //ArrayList de geometrias para representar a los enemigos
    private int spawnedEnemies; //variable que representa la cuenta de los enemigos aparecidos
    private float frecEnemy; //variabble que representa la frecuencia con la que aparecen los enemigos
    private float countdownToEnemy; //variable que representa el tiempo que falta para que aparezca un enemigo
    Node enemies_node; //variable que representa el nodo donde se encuentran los enemigos
    private int score; //variable que guarda la puntuacion
    
    float totalMoved; //variable que representa el espacio que se han movido los enemigos
    
    //Variables que definen las torres correspondientes
    Tower tower1;
    Tower tower2;
    
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true); //Creamos el objeto para controlar las especificaciones
        settings.setTitle("Tower Defense"); //Cambiamos el nombre de la ventana 
        //Integramos una imagen personal a la pantalla de inicio
        settings.setSettingsDialogImage("Interface/logo.png");
        //modificar la resolucion 
        settings.setResolution(1280, 720);
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
        
        //Se establece la camara mirando hacia el spawn enemigo
        cam.setRotation(CAMINITIALROTATION);
        
        //Se crea el piso y el area de aparicion
        Box floor_mesh = new Box(20, 0.5f, 50);
        Box spawn_mesh = new Box(20, 0.5f, 10);
        Geometry floor_geom = new Geometry("floor", floor_mesh);
        Geometry spawn_geom = new Geometry("spawn", spawn_mesh);
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Material spawn_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        floor_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/floor.jpg"));
        spawn_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/spawn.jpg"));
        floor_geom.setMaterial(floor_mat);
        spawn_geom.setMaterial(spawn_mat);
        
        //Utilizaremos las dimensiones de la malla del piso para definir la posicion de unos elementos
        floor_geom.setLocalTranslation(0, 0, floor_mesh.zExtent);
        spawn_geom.setLocalTranslation(0, 0, -spawn_mesh.zExtent);
        
        //se crea la puerta del castillo
        Box castle_mesh = new Box(20,10,1);
        castle_geom = new Geometry("castle", castle_mesh);
        Material castle_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        castle_mat.setTexture("ColorMap", assetManager.loadTexture("Textures/castle_door.jpg"));
        castle_geom.setMaterial(castle_mat);
        
        //Se crea la caja delimitadora con las dimensiones de la puerta del castillo
        castle_bounds = new BoundingBox(new Vector3f(0, 10, 101), 20,10,1);
        
        //se definen las dimensiones de las torres
        float towerX  = 3;
        float towerY = 10;
        float towerZ = 3;
        
        //Se crean las torres instanciadas desde otra clase pasandose como parametros lo necesario para crearlas
        tower1 = new Tower("tower1", "Textures/tower.jpg", "Textures/cubierta.jpg", new Vector3f(floor_mesh.xExtent + towerX, 0, -(towerZ * 5)), towerX, towerY, towerZ , assetManager);
        tower2 = new Tower("tower2", "Textures/tower.jpg", "Textures/cubierta.jpg", new Vector3f(-(floor_mesh.xExtent + towerX), 0, -(towerZ * 5)), towerX, towerY, towerZ , assetManager);
        
        //Se inicializa el nodo de las torres
        Node towers_node = new Node();
        //Al nodo se agregan como hijos las geometrias de las torres y el piso de las mismas
        towers_node.attachChild(castle_geom);
        towers_node.attachChild(tower1.getGeom());
        towers_node.attachChild(tower2.getGeom());
        towers_node.attachChild(tower1.getCubierta_geom());
        towers_node.attachChild(tower2.getCubierta_geom());
        
        //Se agregan al rootNode el nodo de las torres, la geometria del piso y la geometria del spawn
        rootNode.attachChild(towers_node);
        rootNode.attachChild(floor_geom);
        rootNode.attachChild(spawn_geom);
        towers_node.setLocalTranslation(0, castle_mesh.yExtent, (2 * floor_mesh.zExtent) + castle_mesh.zExtent);        
        
        //Se definen las lineas de aparicion
        l = new ArrayList<>();
        l.add(new Line(new Vector3f(-10,4.25f,-6), new Vector3f(10,4.25f,-6)));
        l.add(new Line(new Vector3f(-10,4.25f,-8), new Vector3f(10,4.25f,-8)));
        l.add(new Line(new Vector3f(-10,4.25f,-10), new Vector3f(10,4.25f,-10)));
        l.add(new Line(new Vector3f(-10,4.25f,-12), new Vector3f(10,4.25f,-12)));
        l.add(new Line(new Vector3f(-10,4.25f,-14), new Vector3f(10,4.25f,-14)));
        
        //Se inicializan las variables que controlan algunos aspectos del videojuego
        maxEnemies = 50;
        countEnemies = 0;
        spawnedEnemies = 0;
        frecEnemy = 5;
        countdownToEnemy = frecEnemy;
        score = 0;
        
        //Se inicializa el arreglo y el nodo que contendra a los enemigos
        enemies = new ArrayList<>();
        enemies_node = new Node();
        rootNode.attachChild(enemies_node);
        
        totalMoved = 0; //Se inicializa la variable que lleva la cuenta del movimiento del nodo de enemigos
        
        //Se establece las localizaciones donde podra moverse la camara 
        tower1_camlocation = new Vector3f(tower1.getGeom().getWorldTranslation().getX()-2f, 2 * tower1.getMesh().yExtent + floor_mesh.yExtent + 0.7f, tower1.getGeom().getWorldTranslation().getZ()-2f);
        tower2_camlocation = new Vector3f(tower2.getGeom().getWorldTranslation().getX()+2f, 2 * tower2.getMesh().yExtent + floor_mesh.yExtent + 0.7f, tower2.getGeom().getWorldTranslation().getZ()-2f);

        //Se define en 0 la velocidad para que el jugador no pueda moverse de las torres
        flyCam.setMoveSpeed(0);
        cam.setLocation(tower1_camlocation); //se inicia la camara en la torre 1
        
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
            //Se verifica que accion ha sido activada
            if(name.equals(MAPPING_CHG_CAMERA) && !isPressed) { //Si se ha precionado la barra espacionadora
                Vector3f posicion = cam.getLocation(); //Se obtiene la posicion actual de la camara
                
                //Se comprueba en que torre esta posicionado para luego moverla
                if(posicion.equals(tower1_camlocation)){
                    cam.setLocation(tower2_camlocation);
                }else{
                    if(posicion.equals(tower2_camlocation)){
                        cam.setLocation(tower1_camlocation);
                    }
                }
            }
            else if(name.equals(MAPPING_SHOOTING) && !isPressed){ //Se se ha dado clic izquierdo del mouse
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
                        if(target.getName().contains("enemy")){
                            Geometry temp = (Geometry) enemies_node.getChild(target.getName());
                            enemies_node.detachChildNamed(target.getName());
                            enemies.remove(temp);
                            temp = null;
                            countEnemies--;
                            score++;
                        }

                    }
                }
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        //Se decrementa la cuenta regresiva el tiempo que tardo en generarse el nuevo frame
        countdownToEnemy = countdownToEnemy - tpf;
        //Se comprueba si el numero de enemigos en pantalla es menor al maximo y
        //si el tiempo para que aparezca un enemigo ya ha llegado a 0
        if(countEnemies < maxEnemies && countdownToEnemy <= 0){
            float size = 2; //define el tamaño del proyectil
            //Se agrega un nuevo enemigo al arreglo de enemigos con las caracteristicas
            //pasadas en el constructor de enemy
            enemies.add(new Enemy(("enemy" + spawnedEnemies),assetManager.loadTexture("Textures/ball.jpg") , size, assetManager).getGeom());
            spawnedEnemies++; //Se aumenta en 1 la cantidad total de enemigos aparecidos
            countEnemies++; //Se aumenta en 1 la cantidad total de enemigos en pantalla
            //Se agrega al nodo de enemigos el enemigo creado
            enemies_node.attachChild(enemies.get(enemies.size() - 1));
            //Se mueve el enemigo a un punto aleatorio de la zona de aparicion
            enemies.get(enemies.size() - 1).move(size, (float) (size + Math.random()*7), size - totalMoved);
            countdownToEnemy = frecEnemy; //Se reinicia el contador para la aparicion de un nuevo enemigo
            //Si la cantidad de enemigos aparecidos es multiplo de 5 se ejecuta el bloque dentro del if
            if(spawnedEnemies % 5 == 0){
                if(frecEnemy > 0.5) //Si la frecuencia de aparicion es mayor a 0.5 segundos esta se divide sobre 1.5
                    frecEnemy = (float) (frecEnemy / 1.5);
                if(maxEnemies < 50) //Si el maximo de enemigos es menor a 50 este se multiplica por 1.5
                    maxEnemies = (int) (maxEnemies * 1.5);
            }
        }
        
        //Si hay algun enemigo se mueven los enemigos en pantalla y se comprueba si ha habido una colision
        if(countEnemies > 0){
            enemies_node.move(0, 0, 0.01f); //Los enemigos se mueven 0.01 en Z
            totalMoved += 0.01; //Se aumenta el contador de distancia movida
            //Se crea una variable para almacenar el resultado de la colision
            CollisionResults results = new CollisionResults();
            //Se comprueba si algun enemigo a colisionado con la puerta del castillo
            castle_bounds.collideWith(enemies_node, results);
            //Si el resultado es mayor a 0 ha habido una colision
            if (results.size() > 0) {
               PanelDerrota panel = new PanelDerrota(score);
                JOptionPane.showMessageDialog(null, panel,"Derrota",JOptionPane.PLAIN_MESSAGE);
                System.exit(0);
            }
        }   
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}