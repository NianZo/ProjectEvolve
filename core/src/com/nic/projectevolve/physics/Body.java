package com.nic.projectevolve.physics;

import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/27/16.
 *
 * This class represents collidable bodies. This will encapsulate all physics of objects. Basically,
 * everything about objects can be handled here except for a visual representation of the body (sprite).
 */

public class Body {
    private Vector2 center;

    private short collisionMask;
    private short collisionIdentity;


    private Vector2 velocity;
    private float rotation;
    private float radiusX;
    private float radiusY;
    private boolean isCircle;

    private BodyGroup bodyGroup;
    private int bodyIndex;

    public Body(BodyGroup bodyGroup, Vector2 center, float radiusX, float radiusY, boolean isCircle, int index) {
        this.center = center;
        this.bodyGroup = bodyGroup;
        this.velocity = new Vector2(0, 0);
        this.isCircle = isCircle;
        if(isCircle) {
            this.radiusX = radiusX;
            this.radiusY = radiusY;
        } else {
            this.radiusX = radiusX;
            this.radiusY = radiusY;
        }
        bodyIndex = index;

        PlayScreen.bodyList.AddBody(this);
    }

    public void update(Vector2 position, Vector2 velocity, float rotation) {
        center = position;
        this.velocity = velocity;
        this.rotation = rotation;

        // Every time the object moves collision should be tested
        // TODO only test this if the body moved?
        PlayScreen.bodyList.updatePosition(this);
        PlayScreen.bodyList.testCollision(this);
    }

    public float getPositionX() {
        return center.x;
    }

    public float getPositionY() {
        return center.y;
    }

    public Vector2 getPosition() {
        return center;
    }

    public float getRotation() {
        return rotation;
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

    public Vector2 getVelocity() {
        return velocity;
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

    public int getIndex() {
        return bodyIndex;
    }

    public void giveForce(Vector2 forceVector, Vector2 forceApplicationPoint) {
        bodyGroup.applyForce(forceVector, forceApplicationPoint, this);
    }
}
