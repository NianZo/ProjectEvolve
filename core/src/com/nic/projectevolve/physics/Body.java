package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.screens.PlayScreen;
import com.nic.projectevolve.sprites.Module;
import com.nic.projectevolve.sprites.Player;

/**
 * Created by nic on 8/27/16.
 *
 * This class represents collidable bodies. This will encapsulate all physics of objects. Basically,
 * everything about objects can be handled here except for a visual representation of the body (sprite).
 */

// TODO create shape class to handle shape operations
public class Body {
    private Vector2 center;

    // This should not be here, this is game code not physics engine code
    private Player player;
    private Module module;

    private float mass;

    private short collisionMask;
    private short collisionIdentity;


    private Vector2 velocity;
    private float radiusX;
    private float radiusY;
    private boolean isCircle;

    // TODO TEST CODE VARIABLES, UNSTABLE
    private Vector2 force;
    // I'm pretty sure I don't need a class variable radius and I'm not sure I'll use a class array of
    // vertices until I expand this engine to more complex polygons
    //private float radius;
    //private Vector2[] vertex;

    public Body(Player player, Vector2 center, float radiusX, float radiusY, boolean isCircle) {
        this.center = center;
        this.player = player;
        this.velocity = new Vector2(0, 0);
        this.isCircle = isCircle;
        if(isCircle) {
            //radius = radiusX;
            this.radiusX = radiusX;
            this.radiusY = radiusY;
        } else {
            // This case assumes that the shape is a rectangle (or square)
            //vertex = new Vector2[4];
            //vertex[0] = new Vector2(center.x + radiusX / 2, center.y + radiusY /2);
            //vertex[1] = new Vector2(center.x + radiusX / 2, center.y - radiusY /2);
            //vertex[2] = new Vector2(center.x - radiusX / 2, center.y - radiusY /2);
            //vertex[3] = new Vector2(center.x - radiusX / 2, center.y + radiusY /2);
            this.radiusX = radiusX;
            this.radiusY = radiusY;
        }
        PlayScreen.bodyList.AddBody(this);
    }

    public void setPosition(Vector2 position, Vector2 velocity) {
        this.velocity = velocity;
        center = position;
        // Every time the object moves collision should be tested
        PlayScreen.bodyList.updatePosition(this);
        PlayScreen.bodyList.testCollision(this);

    }

// This could be useful, but not currently, so it will be left out
//    public void setPosition(float x, float y) {
//        center = new Vector2(x, y);
//        // Every time the object moves collision should be tested
//        PlayScreen.bodyList.testCollision(this);
//    }

    public float getPositionX() {
        return center.x;
    }

    public float getPositionY() {
        return center.y;
    }

    public float getRadiusX() {
        return radiusX;
    }

    public boolean getIsCircle() {
        return isCircle;
    }

    public float getRadiusY() {
        return radiusY;
    }
// ENEMY WILL IMPLEMENT THIS, BUT THE ENEMY CLASS NEEDS TO BE CHANGED TO IMPLEMENT MODULES FIRST
//    public void disposeBody() {
//        PlayScreen.bodyList.RemoveBody(this);
//    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setCollisionIdentity(short identity) {
        collisionIdentity = identity;
    }

    public void setCollisionMask(short mask) {
        collisionMask = mask;
    }

    public short getCollisionIdentity() {
        return collisionIdentity;
    }

    public short getCollisionMask() {
        return collisionMask;
    }

    // Game specific code
    public Player getPlayer() {
        return player;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module m) {
        module = m;
    }

    // TODO TEST CODE, UNSTABLE (like everything else isn't)
    public void unUpdate() {
        player.unUpdate();
    }

    public void giveForce(Vector2 force) {
        player.giveForce(module, force);
    }

    public void giveForce(Vector2 forceVector, Vector2 forceApplicationPoint) {
        player.giveForce(module, forceVector, forceApplicationPoint);
    }
}
