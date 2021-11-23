/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 *
 * @author isaia
 */
public class Enemy {
    private Box mesh;
    private Geometry geom;
    private Material mat;

    public Box getMesh() {
        return mesh;
    }

    public Geometry getGeom() {
        return geom;
    }

    public Material getMat() {
        return mat;
    }
    
    public Enemy(String name, String texture,Vector3f location, float xExtent, float yExtent, float zExtent, AssetManager assetManager) {
        mesh = new Box(xExtent, yExtent, zExtent);
        geom = new Geometry(name, mesh);
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.randomColor());
        geom.setMaterial(mat);
        geom.setLocalTranslation(location);
    }
}
