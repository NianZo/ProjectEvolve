package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 8/14/16.
 *
 * Modules are parts of the Player character and enemies. A single enemy or player can be made of
 * multiple Modules.
 *
 * Modules should handle their own collisions.
 *
 * Modules handle all Box2DBody code. I may make my own collision engine from scratch later, this
 * will allow me to only change the Module class to implement this.
 */
public class Module extends Sprite {
    public com.nic.projectevolve.physics.Body newBody;

    private Vector2 position;

    private Texture texture;

    // Constructor for a Module. Takes in a texture, position, and player
    public Module(Texture texture, Vector2 position) {
        super(texture);
        this.texture = texture;
        this.position = position;
        setBounds(0, 0, 32 / ProjectEvolve.PPM, 32 / ProjectEvolve.PPM);
        setRegion(texture);
        setOriginCenter();
    }

    public void update() {
        // Update position and rotation of module
        position = newBody.getPosition();
        float rotation = newBody.getRotation();

        // Update sprite's position and rotation
        setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
        setRotation(rotation);
    }

    public void setBody(com.nic.projectevolve.physics.Body body) {
        newBody = body;
    }

    public com.nic.projectevolve.physics.Body getBody() {
        return newBody;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void dispose() {
        texture.dispose();
    }
}
