package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 12/21/16.
 *
 * This object allows for spawning modules. Basically, this is a modified DraggableImage which maintains
 * the same position, but allows modules to be spawned at the drop location. The spawning code is handled
 * in CreatorScreen, though. CreatorScreen simply tests to see if the pickedUp flag is true to see
 * if a module can be spawned at the drop location
 */
public class ModuleSpawner {
    private Sprite sprite;
    private Sprite transientSprite;
    private Vector2 position;
    private Vector2 transientPosition;
    private boolean pickedUp;

    private short moduleType;

    public ModuleSpawner(int type, Vector2 position) {
        moduleType = (short) type;
        Texture texture = new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[type]);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        sprite.setPosition(position.x, position.y);

        transientSprite = new Sprite(texture);
        transientSprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        transientSprite.setAlpha(0.5f);

        this.position = position;
        transientPosition = position;

        pickedUp = false;
    }

    public void tryPickUp(Vector2 touchPosition) {
        if(!pickedUp) {
            if (touchPosition.x > position.x &&
                    touchPosition.x < position.x + 48 / ProjectEvolve.PPM &&
                    touchPosition.y > position.y &&
                    touchPosition.y < position.y + 48 / ProjectEvolve.PPM) {
                pickedUp = true;
            }
        }
    }

    public void transientMove(Vector2 position) {
        if (pickedUp) {
            transientPosition = new Vector2(position.x - 24 / ProjectEvolve.PPM, position.y - 24 / ProjectEvolve.PPM);
            transientSprite.setPosition(transientPosition.x, transientPosition.y);
        }
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean condition) {
        pickedUp = condition;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
        if (pickedUp) {
            transientSprite.draw(batch);
        }
    }

    public int getType() {
        return moduleType;
    }
}
