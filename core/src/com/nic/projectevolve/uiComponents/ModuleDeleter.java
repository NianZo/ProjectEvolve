package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 12/22/16.
 *
 * Similar to the Module Spawner, this class represents an icon. This icon can have Modules dropped
 * on it to delete them. This class contains a sprite which holds the texture to be drawn.
 */
public class ModuleDeleter {
    private Sprite sprite;
    private Vector2 position;

    public ModuleDeleter(String textureName, Vector2 position) {
        this.position = position;

        Texture texture = new Texture(textureName);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        sprite.setPosition(position.x, position.y);
    }

    public boolean intersects(Vector2 testPosition) {
        Vector2 displacement = new Vector2(position.x + 25 / ProjectEvolve.PPM - testPosition.x, position.y + 25 / ProjectEvolve.PPM - testPosition.y);
        return displacement.len() < 25 / ProjectEvolve.PPM;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
