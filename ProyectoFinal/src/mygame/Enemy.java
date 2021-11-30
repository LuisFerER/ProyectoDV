/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 *
 * @author Andrea Guadalupe Plascencia Rodriguez, Isaias Ricardo Valdivia Hernandez, Luis Fernando Escobedo Romero
 */
public class Enemy {
    private Sphere mesh;
    private Geometry geom;
    private Material mat;

    public Sphere getMesh() {
        return mesh;
    }

    public Geometry getGeom() {
        return geom;
    }

    public Material getMat() {
        return mat;
    }
    
    public Enemy(String name, Texture texture, float size, AssetManager assetManager) {
        mesh = new Sphere(20, 20, size);
        geom = new Geometry(name, mesh);
        mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", texture);
        
        geom.setMaterial(mat);
        geom.setModelBound(new BoundingBox());
        geom.updateModelBound();
    }
}
