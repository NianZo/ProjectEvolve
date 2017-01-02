package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.Body;
import com.nic.projectevolve.physics.BodyGroup;

/**
 * Created by nic on 8/4/16.
 *
 * This is the main player. A player is composed of Modules. Eventually the user will be able to
 * dynamically build a player out of Modules in a configuration of their choosing
 */
public class Player {
    // Holds player position
    private Vector2 position;

    private boolean dead = false;

    Vector2 cg;

    // Modules that make up the player
    public Module[] modules;

    private BodyGroup bodyGroup;
    private int numModules;

    private int energy;
    private float energyTime;

    public Player() {
        modules = new Module[ProjectEvolve.NUM_MODULES];
        cg = new Vector2(0, 16/ProjectEvolve.PPM);

        numModules = 0;
        position = new Vector2(128 / ProjectEvolve.PPM, 128 / ProjectEvolve.PPM);
        bodyGroup = new BodyGroup(position);

        energy = 100;
        energyTime = 0;

        // Create Modules for the player
        int i;
        for(i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                //System.out.println(i);
                // TODO will this cause memory issues? Can't dispose textures if I don't have a reference to it
                modules[numModules] = new Module(new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[ProjectEvolve.state.getModule(i)]), new Vector2(position.x + ProjectEvolve.MODULE_LOCATIONS[i][0] / ProjectEvolve.PPM, position.y + ProjectEvolve.MODULE_LOCATIONS[i][1] / ProjectEvolve.PPM));
                //System.out.println(modules[numModules].getPosition().x);
                Body newBody = new Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true, i);
                if(ProjectEvolve.state.getModule(i) == 0) {
                    newBody.setCollisionIdentity(ProjectEvolve.PLAYER_BIT); // Blue modules
                } else if(ProjectEvolve.state.getModule(i) == 1) {
                    newBody.setCollisionIdentity((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.ATTACKING_BIT));
                }
                newBody.setCollisionMask((short) (ProjectEvolve.ENEMY_BIT | ProjectEvolve.EDGE_BIT));
                newBody.setUserData(this);
                bodyGroup.addBody(newBody);
                modules[numModules].setBody(newBody);
                numModules++;
            }
        }
    }

    public void update(float dt) {
        energyTime += dt;
        if(energyTime > 1) {
            energyTime = 0;
            energy--;
        }
        if(energy <= 0) {
            dead = true;
        }
        bodyGroup.update(dt);
        position = bodyGroup.getPosition();

        for(int i = 0; i < numModules; i++) {
            modules[i].update();
        }
    }

    public void render(SpriteBatch batch) {
        int i;
        for (i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
            if (modules[i] != null) {
                modules[i].draw(batch);
            }
        }
    }

    public void giveForce(Vector2 force) {
        bodyGroup.applyForce(force, new Vector2(0, 0), modules[0].getBody());
    }

    public Vector2 getPosition() {
        return position;
    }

//    public void hit() {
//        //dead = true;
//        energy += 25;
//        System.out.println("Hit Enemy!");
//    }

    public void addEnergy(int energy) {
        this.energy += energy;
        dead = this.energy <= 0;
    }

    public boolean isDead() {
        return dead;
    }

    public int getEnergy() {
        return energy;
    }
}
