package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.shapes.Circle;

/**
 * Created by nic on 10/30/16.
 *
 * This class is the matrix on which players can place creature modules. This will allow alteration
 * of the game's State class which will hold the creature blueprint.
 */
public class CreatorMatrix {
    private ProjectEvolve game;

    private Sprite sprite;

    private Circle[] sockets;
    private int numSockets = 19;
    private short[] occupied;

    // TODO Test code
    private Sprite testSprite;

    public CreatorMatrix(String textureName, Vector2 position, String testTextureName) {
        this.game = game;
        sockets = new Circle[numSockets];
        occupied = new short[numSockets];

        // Handles creation of hexagon matrix sprite
        Texture texture = new Texture(textureName);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, 300 / ProjectEvolve.PPM, 300 / ProjectEvolve.PPM);
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);

        // Handles creation and positioning of sockets to drop modules into
        // TODO THIS HAS HARD-CODED POSITION VALUES FOR THE TEST HEXAGON MATRIX IMAGE, NEEDS TO BE CHANGED
        sockets[0] = new Circle(new Vector2(position.x, position.y - 1 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[1] = new Circle(new Vector2(position.x, position.y + 49 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[2] = new Circle(new Vector2(position.x + 43 / ProjectEvolve.PPM, position.y + 25 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[3] = new Circle(new Vector2(position.x + 43 / ProjectEvolve.PPM, position.y - 25 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[4] = new Circle(new Vector2(position.x + 1 / ProjectEvolve.PPM, position.y - 50 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[5] = new Circle(new Vector2(position.x - 41 / ProjectEvolve.PPM, position.y - 26 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[6] = new Circle(new Vector2(position.x - 41 / ProjectEvolve.PPM, position.y + 24 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[7] = new Circle(new Vector2(position.x, position.y + 100 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[8] = new Circle(new Vector2(position.x + 42 / ProjectEvolve.PPM, position.y + 75 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[9] = new Circle(new Vector2(position.x + 84 / ProjectEvolve.PPM, position.y + 50 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[10] = new Circle(new Vector2(position.x + 85 / ProjectEvolve.PPM, position.y + 0 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[11] = new Circle(new Vector2(position.x + 85 / ProjectEvolve.PPM, position.y - 50 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[12] = new Circle(new Vector2(position.x + 43 / ProjectEvolve.PPM, position.y - 75 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[13] = new Circle(new Vector2(position.x + 2 / ProjectEvolve.PPM, position.y - 100 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[14] = new Circle(new Vector2(position.x - 41 / ProjectEvolve.PPM, position.y - 76 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[15] = new Circle(new Vector2(position.x - 84 / ProjectEvolve.PPM, position.y - 50 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[16] = new Circle(new Vector2(position.x - 83 / ProjectEvolve.PPM, position.y - 1 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[17] = new Circle(new Vector2(position.x - 84 / ProjectEvolve.PPM, position.y + 48 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);
        sockets[18] = new Circle(new Vector2(position.x - 42 / ProjectEvolve.PPM, position.y + 73 / ProjectEvolve.PPM), 50f / 2 / ProjectEvolve.PPM);

        // TODO TEST CODE BLOCK ///////////////////
//        int i = numSockets - 1;
//        Texture testTexture = new Texture(testTextureName);
//        testSprite = new Sprite(testTexture);
//        testSprite.setBounds(0, 0, 48 / ProjectEvolve.PPM, 48 / ProjectEvolve.PPM);
//        testSprite.setPosition(sockets[i].getPosition().x - sockets[i].getRadius(), sockets[i].getPosition().y - sockets[i].getRadius());
        // TODO END TEST CODE BLOCK ///////////////
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        //testSprite.draw(batch);
    }

    public Vector2 testDrop(Vector2 position) {
        int i = 0;
        while(i < numSockets) {
            if (intersects(position, sockets[i])) {
                // TODO grab module type and prepare to write it to the game's State.
                occupied[i] = 1;
                ProjectEvolve.state.setModule(i, (short) 1);
                return new Vector2(sockets[i].getPosition().x - sockets[i].getRadius(), sockets[i].getPosition().y - sockets[i].getRadius());

            }
            i++;
        }
        // AN INVALID LOCATION SO DraggableImage CAN TEST AGAINST IT
        return new Vector2(-1, -1);
    }

    public boolean pickUp(Vector2 position) {
        int i = 0;
        while(i < numSockets) {
            if (intersects(position, sockets[i])) {
                if(occupied[i] > 0) {
                    occupied[i] = 0;
                    ProjectEvolve.state.setModule(i, (short) 0);
                    return true;
                }
            }
            i++;
        }
        return false;
    }

    private boolean intersects(Vector2 cursor, Circle socket) {
        Vector2 displacement = new Vector2(socket.getPosition().x - cursor.x, socket.getPosition().y - cursor.y);
        float magDisplacement = displacement.len();

        if (magDisplacement <= socket.getRadius()) {
            return true;
        } else {
            return false;
        }
    }
}
