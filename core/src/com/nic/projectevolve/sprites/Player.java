package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.Body;
import com.nic.projectevolve.physics.BodyGroup;
import com.nic.projectevolve.screens.PlayScreen;

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

    // Modules that make up the player
    public Module[] modules;
    private int numModules;

    private BodyGroup bodyGroup;

    // Player will die when energy reaches zero or below, energyTime is a intermediate placeholder for reducing energy each second
    private int energy;
    private float energyTime;

    public Player() {
        // Initialize modules array and numModules counter
        modules = new Module[ProjectEvolve.NUM_MODULES];
        numModules = 0;

        // Initialize player to 128,128 pixels
        position = new Vector2(128 / ProjectEvolve.PPM, 128 / ProjectEvolve.PPM);

        // Create the player's bodyGroup
        bodyGroup = new BodyGroup(position, PlayScreen.bodyList, ProjectEvolve.MAX_VELOCITY, ProjectEvolve.MAX_ANGULAR_VELOCITY);

        // Initialize energy values
        energy = 100;
        energyTime = 0;

        // Create Modules for the player
        float maxSpeed = 1;
        int i;
        for(i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                // Create module and body
                modules[numModules] = new Module(new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[ProjectEvolve.state.getModule(i)]), new Vector2(position.x + ProjectEvolve.MODULE_LOCATIONS[i][0], position.y + ProjectEvolve.MODULE_LOCATIONS[i][1]));
                Body newBody = new Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true);

                // Set collision identity based on the type of module
                if(ProjectEvolve.state.getModule(i) == 0) {
                    newBody.setCollisionIdentity(ProjectEvolve.PLAYER_BIT); // Blue modules
                } else if(ProjectEvolve.state.getModule(i) == 1) {
                    newBody.setCollisionIdentity((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.ATTACKING_BIT)); // Red modules
                } else if(ProjectEvolve.state.getModule(i) == 2) {
                    newBody.setCollisionIdentity((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.DEFENDING_BIT)); // Green modules
                }

                // Set collision mask and user data of body
                newBody.setCollisionMask((short) (ProjectEvolve.ENEMY_BIT | ProjectEvolve.EDGE_BIT));
                newBody.setUserData(this);

                // Finish giving all objects references to things they need
                bodyGroup.addBody(newBody);
                modules[numModules].setBody(newBody);

                // Let the player class know the number of modules
                numModules++;
                if(ProjectEvolve.state.getModule(i) == 0) {
                    maxSpeed += .1f * .2f * GameState.moduleLevels[0];
                }
                if(ProjectEvolve.state.getModule(i) == 2) {
                    energy += 5;
                }
            }
            bodyGroup.setMaxVelocity(maxSpeed);
        }
    }

    public void update(float dt) {
        // Add the delta time to energy time
        energyTime += dt;

        // After a second, reset energy time and decrement energy
        if(energyTime > 1 + .2 * GameState.moduleLevels[2]) {
            energyTime = 0;
            energy--;
            // If energy is zero or below then the player is dead
            dead = energy <= 0;
        }

        // Update bodyGroup for physics calculations and get resultant position
        bodyGroup.update(dt);
        position = bodyGroup.getPosition();

        // Update all occupied modules
        for(int i = 0; i < numModules; i++) {
            modules[i].update();
        }
    }

    public void render(SpriteBatch batch) {
        // Render each module
        for (int i = 0; i < numModules; i++) {
            modules[i].draw(batch);
        }
    }

    public void giveForce(Vector2 force) {
        // Pass applied force on to the bodyGroup indicating that it should be on the CG
        bodyGroup.applyForce(force, new Vector2(0, 0), modules[0].getBody());
    }

    public Vector2 getPosition() {
        return position;
    }

    public void addEnergy(int energy) {
        // Add energy and test to see if the player is dead
        if(energy < 0) {
            energy += GameState.moduleLevels[2];
            if(energy > 0) {
                energy = 0;
            }
        }
        this.energy += energy;
        dead = this.energy <= 0;
    }

    public boolean isDead() {
        return dead;
    }

    public int getEnergy() {
        return energy;
    }

    public void dispose() {
        for(int i = 0; i < numModules; i++) {
            modules[i].dispose();
        }
    }
}
