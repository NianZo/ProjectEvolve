package com.nic.projectevolve.shapes;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by nic on 10/30/16.
 */
public class Circle {
    private Vector2 position;
    private float radius;

    public Circle(Vector2 position, float radius) {
        this.position = position;
        this.radius = radius;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getRadius() {
        return radius;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
