package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.BodyGroup;
import com.nic.projectevolve.physics.PhysicsMath;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/6/16.
 *
 * This is the main class for enemies. Eventually these will be able to move on their own and attack.
 */
public class Enemy extends Sprite {
    // TODO this class needs some serious work so that it can move, and chase the player

    private boolean setToDestroy;
    private boolean destroyed;


    private Vector2 position;
    private float rotation;

    private Module[] modules;
    private int numModules;
    private BodyGroup bodyGroup;

    private int energy;

    private Player player;
    private Vector2 lastIdleForce;

    public Enemy(Player character, Vector2 position) {
        player = character;
        this.position = position;//new Vector2(256 / ProjectEvolve.PPM, 256 / ProjectEvolve.PPM);
        rotation = 0f;

        modules = new Module[ProjectEvolve.NUMMODULES];
        numModules = 0;
        bodyGroup = new BodyGroup(position);
        setBounds(0, 0, 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM);
        setToDestroy =false;
        destroyed = false;

        energy = 100;

        lastIdleForce = new Vector2(1, 0);

        int i;
        for(i = 0; i < 1; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                // TODO will this cause memory issues? Can't dispose textures if I don't have a reference to it
                modules[numModules] = new Module(new Texture(ProjectEvolve.MODULETEXTURETNAMES[ProjectEvolve.state.getModule(i)]), new Vector2(position.x + ProjectEvolve.MODULELOCATIONS[i][0], position.y + ProjectEvolve.MODULELOCATIONS[i][1]));
                com.nic.projectevolve.physics.Body newBody = new com.nic.projectevolve.physics.Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true, i);
                modules[numModules].setBody(newBody);
                newBody.setCollisionIdentity(ProjectEvolve.ENEMY_BIT);
                newBody.setCollisionMask((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.EDGE_BIT));
                newBody.setUserData(this);
                bodyGroup.addBody(newBody);
                numModules++;
            }
        }
    }

    public void update(float dt) {
        System.out.println("updating enemy");
        AI(dt);
        bodyGroup.update(dt);
        position = bodyGroup.getPosition();

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

    private void AI(float dt) {
        float forceScaleFactor = 150;
        if(Math.abs(player.getPosition().x - position.x) < 5 * 32 / ProjectEvolve.PPM && Math.abs(player.getPosition().y - position.y) < 5 * 32 / ProjectEvolve.PPM) {
            Vector2 direction = new Vector2(player.getPosition().x - position.x, player.getPosition().y - position.y);
            Vector2 unitDirection = new Vector2(direction.x / direction.len(), direction.y / direction.len());
            bodyGroup.applyForce(unitDirection.scl(dt).scl(forceScaleFactor), new Vector2(0, 0), modules[0].getBody());
            System.out.println("Moving toward player");
        } else {
            lastIdleForce.x = (float) (Math.random() * 2 - 1);
            lastIdleForce.y = (float) (Math.random() * 2 - 1);
            PhysicsMath.clampVectorBelow(lastIdleForce, 1, true, true, true, true);
            bodyGroup.applyForce(lastIdleForce.scl(dt).scl(150), new Vector2(0, 0), modules[0].getBody());
        }
    }

    public void hit() {
        setToDestroy = true;
        //Hud.addScore(1);
    }

    public void addEnergy(int energy) {
        this.energy += energy;
        setToDestroy = this.energy <= 0;
    }

    public boolean isDead() {
        return setToDestroy;
    }
}