package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 9/27/16.
 *
 * This class will be used in the creator screen. This will allow the player to drag modules to
 * create their own layout of modules
 */
public class DraggableImage {
    private Sprite sprite;
    private Sprite transientSprite;
    private Vector2 position;
    private Vector2 transientPosition;
    private boolean pickedUp;

    private short moduleType;
    private CreatorMatrix matrix;

    public DraggableImage(String textureName, Vector2 position, CreatorMatrix matrix) {
        this.matrix = matrix;

        Texture texture = new Texture(textureName);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        sprite.setPosition(position.x, position.y);

        transientSprite = new Sprite(texture);
        transientSprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        transientSprite.setAlpha(0.5f);
        transientSprite.setPosition(position.x, position.y);

        this.position = position;
        transientPosition = position;

        pickedUp = false;
    }

    public void pickUp(Vector2 touchPosition) {
        if (matrix.pickUp(touchPosition) || touchPosition.x > position.x &&
                touchPosition.x < position.x + 48 / ProjectEvolve.PPM &&
                touchPosition.y > position.y &&
                touchPosition.y < position.y + 48 / ProjectEvolve.PPM) {

            pickedUp = true;

        }

    }

    public void transientMove(Vector2 position) {
        if (pickedUp) {
            transientPosition = new Vector2(position.x - 24 / ProjectEvolve.PPM, position.y - 24 / ProjectEvolve.PPM);
            transientSprite.setPosition(transientPosition.x, transientPosition.y);
        }
    }

    public void place(Vector2 placedPosition) {
        // TODO only allow placing if picked up and in a valid location, otherwise set transient position to position
        if (pickedUp) {
            Vector2 testPostition = matrix.testDrop(placedPosition);
            if (testPostition.x >= 0) {
                position = testPostition;
                sprite.setPosition(position.x, position.y);
                pickedUp = false;
            } else {
                transientPosition = position;
                pickedUp = false;
            }
        }
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        if (pickedUp) {
            transientSprite.draw(batch);
        }
    }

}
