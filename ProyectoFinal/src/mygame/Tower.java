/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
    private Box mesh;
    private Geometry geom;
    private Material mat;
    Geometry cubierta_geom;

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
    
    
    
    public Tower(String name, String texture, String textureCubierta, Vector3f location, float xExtent, float yExtent, float zExtent, AssetManager assetManager) {
        mesh = new Box(xExtent, yExtent, zExtent);
        geom = new Geometry(name, mesh);
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture(texture));
        //mat.setColor("Color", ColorRGBA.randomColor());
        geom.setMaterial(mat);
        geom.setLocalTranslation(location);
        Quad cubierta_mesh = new Quad(xExtent*2, zExtent*2);
        Material cubierta_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        cubierta_geom = new Geometry("cubierta_"+name, cubierta_mesh);
        cubierta_mat.setTexture("ColorMap", assetManager.loadTexture(textureCubierta));
        cubierta_geom.setMaterial(cubierta_mat);
        cubierta_geom.rotate(-FastMath.DEG_TO_RAD*90, 0, 0);
        cubierta_geom.setLocalTranslation(location);
        cubierta_geom.move(-xExtent,yExtent+0.001f,zExtent);
        System.out.println(cubierta_geom.getWorldTranslation());
    }
}