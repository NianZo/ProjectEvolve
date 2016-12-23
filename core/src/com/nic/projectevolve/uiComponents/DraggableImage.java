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
    private Texture texture;
    private Sprite sprite;
    private Sprite transientSprite;
    private Vector2 position;
    private Vector2 transientPosition;
    private boolean pickedUp;

    private int socket;

    private short moduleType;
    private CreatorMatrix matrix;

    public DraggableImage(int type, Vector2 position, CreatorMatrix matrix) {
        this.matrix = matrix;
        moduleType = (short) type;

        texture = new Texture(ProjectEvolve.MODULETEXTURETNAMES[type]);
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

    public void tryPickUp(Vector2 touchPosition) {
        if (touchPosition.x > position.x &&
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

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        if (pickedUp) {
            transientSprite.draw(batch);
        }
    }

    public void place(int index, int socket) {
        // Index is the index of the Module being placed, socket is the socket it is being placed into
        this.socket = socket;
        matrix.actuallyDrop(index, socket, moduleType);

        position = matrix.getSocketLocation(socket);
        transientPosition = position;
        sprite.setPosition(position.x, position.y);

        pickedUp = false;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean condition) {
        pickedUp = condition;
    }

    public int getSocket() {
        return socket;
    }

    public void dispose() {
        matrix.removeModule(socket);
        texture.dispose();
    }

}