/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;

/**
 * 
 * @author Andrea Guadalupe Plascencia Rodriguez, Isaias Ricardo Valdivia Hernandez, Luis Fernando Escobedo Romero
 */

public class Tower {
    private Box mesh; //malla de la torre
    private Geometry geom; //geometria de la torre
    private Material mat; //material de la torre
    Geometry cubierta_geom; //geometria de la cubierta de la torre

    /**
     * Se inicializan los valores necesarios para la creacion de las torres
     * @param name
     * @param texture
     * @param textureCubierta
     * @param location
     * @param xExtent
     * @param yExtent
     * @param zExtent
     * @param assetManager 
     */
    public Tower(String name, String texture, String textureCubierta, Vector3f location, float xExtent, float yExtent, float zExtent, AssetManager assetManager) {
        mesh = new Box(xExtent, yExtent, zExtent); //Se crean la malla con las dimensiones pasadas en los parametros
        geom = new Geometry(name, mesh); //Se establece la geometria de la malla
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture(texture));
        geom.setMaterial(mat);
        geom.setLocalTranslation(location); //Se localizan las geometrias de las torres
        //Se crea un Quad para la cubierta de la torre
        Quad cubierta_mesh = new Quad(xExtent*2, zExtent*2);
        Material cubierta_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cubierta_geom = new Geometry("cubierta_"+name, cubierta_mesh);
        cubierta_mat.setTexture("ColorMap", assetManager.loadTexture(textureCubierta));
        cubierta_geom.setMaterial(cubierta_mat);
        //Se rota y se establece la cubierta para que quede sobre la torre
        cubierta_geom.rotate(-FastMath.DEG_TO_RAD*90, 0, 0);
        cubierta_geom.setLocalTranslation(location);
        cubierta_geom.move(-xExtent,yExtent+0.001f,zExtent);
    }
    
    //Getters y Setters
    
    public Box getMesh() {
        return mesh;
    }

    public Geometry getGeom() {
        return geom;
    }

    public Material getMat() {
        return mat;
    }

    public Geometry getCubierta_geom() {
        return cubierta_geom;
    }

}