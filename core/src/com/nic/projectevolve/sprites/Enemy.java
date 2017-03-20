package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.Body;
import com.nic.projectevolve.physics.BodyGroup;
import com.nic.projectevolve.physics.PhysicsMath;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/6/16.
 *
 * This is the main class for enemies. Eventually these will be able to move on their own and attack.
 */
public class Enemy {
    private Vector2 position;
    private int energy;
    private boolean setToDestroy;
    private boolean destroyed;

    // Module that will make up the enemy
    private Module[] modules;
    private int numModules;

    // Physics bodyGroup
    private BodyGroup bodyGroup;

    // Needed for AI to decide if the player is close enough to chase / run away
    private Player player;

    // State variable used in the AI algorithm
    private Vector2 lastIdleForce;

    private int attackLevel = 1;
    private int defenseLevel = 1;

    private int AItype;

    public Enemy(Player character, Vector2 position, int levelNumber) {
        player = character;
        this.position = position;

        // Initialize modules array
        modules = new Module[ProjectEvolve.NUM_MODULES];
        numModules = 0;

        // Initialize physics bodyGroup
        bodyGroup = new BodyGroup(position, PlayScreen.bodyList, ProjectEvolve.MAX_VELOCITY, ProjectEvolve.MAX_ANGULAR_VELOCITY);

        // Initialize general enemy variables
        setToDestroy =false;
        destroyed = false;
        energy = 100;
        lastIdleForce = new Vector2(1, 0);

        // Randomly choose an integer from zero to NUM_ENEMY_DESIGNS - 1 to choose an enemy design from
        if((levelNumber + 1) % 5 != 0) {
            int rand = (int) Math.floor(Math.random() * ProjectEvolve.NUM_ENEMY_DESIGNS);
            AItype = (int) Math.floor(Math.random() * 5);
            AItype = (AItype == 1) ? -1 : 1;

            // Create modules based on the random number found
            float maxSpeed = 0.75f;
            for (int i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
                // Only create module if the index is not invalid
                if (ProjectEvolve.ENEMY_MODULE_DESIGNS[rand][i] != -1) {
                    // Create module and body and give the body to the module
                    int type = ProjectEvolve.ENEMY_MODULE_DESIGNS[rand][i];
                    modules[numModules] = new Module(new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[ProjectEvolve.ENEMY_MODULE_DESIGNS[rand][i]]), new Vector2(position.x + ProjectEvolve.MODULE_LOCATIONS[i][0], position.y + ProjectEvolve.MODULE_LOCATIONS[i][1]));
                    Body newBody = new Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true);
                    modules[numModules].setBody(newBody);

                    // Set collision information
                    short collisionIdentity = ProjectEvolve.ENEMY_BIT;
                    collisionIdentity = (short) ((type == 1) ? (collisionIdentity | ProjectEvolve.ATTACKING_BIT) : collisionIdentity);
                    collisionIdentity = (short) ((type == 2) ? (collisionIdentity | ProjectEvolve.DEFENDING_BIT) : collisionIdentity);
                    newBody.setCollisionIdentity(collisionIdentity);

                    newBody.setCollisionMask((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.EDGE_BIT | ProjectEvolve.ENEMY_BIT));

                    // Give objects the rest of the references they need
                    newBody.setUserData(this);
                    bodyGroup.addBody(newBody);
                    numModules++;
                    if (ProjectEvolve.state.getModule(i) == 0) {
                        maxSpeed += .1f * .2f * GameState.moduleLevels[0];
                        // TODO randomly generate module levels too
                    }
                }
                bodyGroup.setMaxVelocity(maxSpeed);
            }
        } else {
            for(int i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
                AItype = 1;
                int type = ProjectEvolve.BOSS_MODULE_DESIGN[0][i];
                if(type != -1) {
                    modules[numModules] = new Module(new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[ProjectEvolve.BOSS_MODULE_DESIGN[0][i]]), new Vector2(position.x + ProjectEvolve.MODULE_LOCATIONS[i][0], position.y + ProjectEvolve.MODULE_LOCATIONS[i][1]));
                    Body newBody = new Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true);
                    modules[numModules].setBody(newBody);

                    // Set collision information
                    short collisionIdentity = ProjectEvolve.ENEMY_BIT;
                    collisionIdentity = (short) ((type == 1) ? (collisionIdentity | ProjectEvolve.ATTACKING_BIT) : collisionIdentity);
                    collisionIdentity = (short) ((type == 2) ? (collisionIdentity | ProjectEvolve.DEFENDING_BIT) : collisionIdentity);
                    newBody.setCollisionIdentity(collisionIdentity);

                    newBody.setCollisionMask((short) (ProjectEvolve.PLAYER_BIT | ProjectEvolve.EDGE_BIT | ProjectEvolve.ENEMY_BIT));

                    // Give objects the rest of the references they need
                    newBody.setUserData(this);
                    bodyGroup.addBody(newBody);
                    numModules++;
//                    if (ProjectEvolve.state.getModule(i) == 0) {
//                        maxSpeed += .1f * .2f * GameState.moduleLevels[0];
//                        // TODO randomly generate module levels too
//                    }
                    energy = 150;
                }
                bodyGroup.setMaxVelocity(.75f);
            }
        }
    }

    public void update(float dt) {
        // Calculate the AI
        AI(dt);

        // Update the bodyGroup and then grab the position from the bodyGroup
        bodyGroup.update(dt);
        position = bodyGroup.getPosition();

        if(Double.isNaN(position.x) || position.x < 0 || position.y < 0 || position.x > 50 * 32 / ProjectEvolve.PPM || position.y > 50 * 32 / ProjectEvolve.PPM) {
            setToDestroy = true;
        }

        // Update all the modules
        for(int i = 0; i < numModules; i++) {
            modules[i].update();
        }

        // Remove all bodies possessed by the enemy if the enemy is destroyed
        if (setToDestroy && !destroyed) {
            destroyed = true;
            for(int i = 0; i < numModules; i++) {
                PlayScreen.bodyList.RemoveBody(modules[i].getBody());
            }
        }
    }

    public void render(SpriteBatch batch) {
        // Render all modules if the enemy is not destroyed
        if (!destroyed) {
            for(int i = 0; i < numModules; i++) {
                modules[i].draw(batch);
            }
        }
    }

    private void AI(float dt) {
        float forceScaleFactor = 100;

        // If the player is within 5 tiles in both the x and y directions then chase the player
        if(Math.abs(player.getPosition().x - position.x) < 5 * 32 / ProjectEvolve.PPM && Math.abs(player.getPosition().y - position.y) < 5 * 32 / ProjectEvolve.PPM) {
            Vector2 direction = new Vector2(player.getPosition().x - position.x, player.getPosition().y - position.y);
            Vector2 unitDirection = new Vector2(AItype * direction.x / direction.len(), AItype * direction.y / direction.len());
            bodyGroup.applyForce(unitDirection.scl(dt).scl(forceScaleFactor), new Vector2(0, 0), modules[0].getBody());
        }
        // Otherwise, apply a random force close in direction to the last force (for seemingly random movement)
        else {
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

    public int getAttackLevel() {
        return attackLevel;
    }

    public int getDefenseLevel() {
        return defenseLevel;
    }

    public void dispose() {
        for(int i = 0; i < numModules; i++) {
            modules[i].dispose();
        }
    }
}