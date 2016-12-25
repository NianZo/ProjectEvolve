package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.BodyGroup;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/6/16.
 *
 * This is the main class for enemies. Eventually these will be able to move on their own and attack.
 */
public class Enemy extends Sprite {

    private boolean setToDestroy;
    private boolean destroyed;


    private Vector2 position;
    private float rotation;

    private Module[] modules;
    private int numModules;
    private BodyGroup bodyGroup;

    public Enemy() {
        position = new Vector2(256 / ProjectEvolve.PPM, 256 / ProjectEvolve.PPM);
        rotation = 0f;

        modules = new Module[ProjectEvolve.NUMMODULES];
        numModules = 0;
        bodyGroup = new BodyGroup(position);
        setBounds(0, 0, 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM);
        setToDestroy =false;
        destroyed = false;

        int i;
        for(i = 0; i < 1; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                // TODO will this cause memory issues? Can't dispose textures if I don't have a reference to it
                modules[numModules] = new Module(numModules, new Texture(ProjectEvolve.MODULETEXTURETNAMES[ProjectEvolve.state.getModule(i)]), position.x + ProjectEvolve.MODULELOCATIONS[i][0], position.y + ProjectEvolve.MODULELOCATIONS[i][1], ProjectEvolve.PLAYER_BIT);
                com.nic.projectevolve.physics.Body newBody = new com.nic.projectevolve.physics.Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true, i);
                modules[numModules].setBody(newBody);
                newBody.setCollisionIdentity(ProjectEvolve.ENEMY_BIT);
                newBody.setCollisionMask((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.EDGE_BIT));

                numModules++;
            }
        }
    }

    public void update() {
        modules[0].update();
        if (setToDestroy && !destroyed) {
            destroyed = true;
            PlayScreen.bodyList.RemoveBody(modules[0].getBody());
        }
    }

    public void render(SpriteBatch batch) {
        if (!destroyed) {
            modules[0].draw(batch);
        }
    }

    public void hit() {
        setToDestroy = true;
        //Hud.addScore(1);
    }
}
