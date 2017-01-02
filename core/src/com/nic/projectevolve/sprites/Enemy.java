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
    private boolean setToDestroy;
    private boolean destroyed;


    private Vector2 position;

    private Module[] modules;
    private int numModules;
    private BodyGroup bodyGroup;

    private int energy;

    private Player player;
    private Vector2 lastIdleForce;

    public Enemy(Player character, Vector2 position) {
        player = character;
        this.position = position;

        modules = new Module[ProjectEvolve.NUM_MODULES];
        numModules = 0;
        bodyGroup = new BodyGroup(position);
        setBounds(0, 0, 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM);
        setToDestroy =false;
        destroyed = false;

        energy = 100;

        lastIdleForce = new Vector2(1, 0);

        int rand = (int) Math.floor(Math.random() * ProjectEvolve.NUM_ENEMY_DESIGNS);
        System.out.println(rand);

        int i;
        for(i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
            if(ProjectEvolve.ENEMY_MODULE_DESIGNS[rand][i] != -1) {
                // TODO will this cause memory issues? Can't dispose textures if I don't have a reference to it
                modules[numModules] = new Module(new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[ProjectEvolve.ENEMY_MODULE_DESIGNS[rand][i]]), new Vector2(position.x + ProjectEvolve.MODULE_LOCATIONS[i][0], position.y + ProjectEvolve.MODULE_LOCATIONS[i][1]));
                com.nic.projectevolve.physics.Body newBody = new com.nic.projectevolve.physics.Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true, i);
                modules[numModules].setBody(newBody);
                newBody.setCollisionIdentity(ProjectEvolve.ENEMY_BIT);
                newBody.setCollisionMask((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.EDGE_BIT | ProjectEvolve.ENEMY_BIT));
                newBody.setUserData(this);
                bodyGroup.addBody(newBody);
                numModules++;
            }
        }
    }

    public void update(float dt) {
        AI(dt);
        bodyGroup.update(dt);
        position = bodyGroup.getPosition();

        for(int i = 0; i < numModules; i++) {
            modules[i].update();
        }

        if (setToDestroy && !destroyed) {
            destroyed = true;
            for(int i = 0; i < numModules; i++) {
                PlayScreen.bodyList.RemoveBody(modules[i].getBody());
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (!destroyed) {
            for(int i = 0; i < numModules; i++) {
                modules[i].draw(batch);
            }
        }
    }

    private void AI(float dt) {
        float forceScaleFactor = 150;
        if(Math.abs(player.getPosition().x - position.x) < 5 * 32 / ProjectEvolve.PPM && Math.abs(player.getPosition().y - position.y) < 5 * 32 / ProjectEvolve.PPM) {
            Vector2 direction = new Vector2(player.getPosition().x - position.x, player.getPosition().y - position.y);
            Vector2 unitDirection = new Vector2(direction.x / direction.len(), direction.y / direction.len());
            bodyGroup.applyForce(unitDirection.scl(dt).scl(forceScaleFactor), new Vector2(0, 0), modules[0].getBody());
        } else {
            lastIdleForce.x = (float) (Math.random() * 2 - 1);
            lastIdleForce.y = (float) (Math.random() * 2 - 1);
            PhysicsMath.clampVectorBelow(lastIdleForce, 1, true, true, true, true);
            bodyGroup.applyForce(lastIdleForce.scl(dt).scl(150), new Vector2(0, 0), modules[0].getBody());
        }
    }

    public void hit() {
        setToDestroy = true;
    }

    public void addEnergy(int energy) {
        this.energy += energy;
        setToDestroy = this.energy <= 0;
    }

    public boolean isDead() {
        return setToDestroy;
    }
}