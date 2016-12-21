package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 12/21/16.
 */
public class ModuleSpawner {
    private Sprite sprite;
    private Sprite transientSprite;
    private Vector2 position;
    private Vector2 transientPosition;
    public boolean pickedUp;

    private short moduleType;
    private CreatorMatrix matrix;

    public ModuleSpawner(String textureName, Vector2 position, CreatorMatrix matrix) {
        this.matrix = matrix;

        Texture texture = new Texture(textureName);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);

        transientSprite = new Sprite(texture);
        transientSprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        transientSprite.setAlpha(0.5f);

        this.position = position;
        transientPosition = position;

        pickedUp = false;
    }

    public void pickUp(Vector2 touchPosition) {
        if(!pickedUp) {
            if (touchPosition.x > position.x &&
                    touchPosition.x < position.x + 48 / ProjectEvolve.PPM &&
                    touchPosition.y > position.y &&
                    touchPosition.y < position.y + 48 / ProjectEvolve.PPM) {

                pickedUp = true;
                System.out.println("Picking up new Module");

            }
        }

    }

    public void transientMove(Vector2 position) {
        if (pickedUp) {
            transientPosition = new Vector2(position.x - 24 / ProjectEvolve.PPM, position.y - 24 / ProjectEvolve.PPM);
            transientSprite.setPosition(transientPosition.x, transientPosition.y);
        }
    }

    public Vector2 place(Vector2 placedPosition) {
        // TODO only allow placing if picked up and in a valid location, otherwise set transient position to position
        if (pickedUp) {
            Vector2 testPosition = matrix.testDrop(placedPosition);
            if (testPosition.x >= 0) {
                //position = testPostition;
                //transientPosition = position;
                //transientSprite.setPosition(transientPosition.x, transientPosition.y);
                //sprite.setPosition(position.x, position.y);
                pickedUp = false;
                return testPosition;
            } else {
                //transientPosition = position;
                //transientSprite.setPosition(transientPosition.x, transientPosition.y);
                pickedUp = false;
                return new Vector2(-1, -1);
            }
        }
        return new Vector2(-1, -1);
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        if (pickedUp) {
            transientSprite.draw(batch);
            //System.out.println("rendering transient sprite");
        }
    }
}
