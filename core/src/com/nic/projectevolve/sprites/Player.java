package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/4/16.
 *
 * This is the main player. A player is composed of Modules. Eventually the user will be able to
 * dynamically build a player out of Modules in a configuration of their choosing
 */
public class Player {
    // Will hold player position, velocity, and rotation
    private Vector2 position;

    private Vector2 lastPosition;

    private Vector2 velocity;
    private float rotation;
    private boolean hasCollided;

    private boolean dead = false;

    float xOffset;
    float yOffset;

    Vector2 cg;

    // Modules that make up the player
    public Module[] modules;
    public Module mod1;
    public Module mod2;

    // TODO TEST CODE
    private boolean unUpdate;

    private Vector2 totalParallelForce;
    private Vector2 totalPerpendicularForce;
    private float totalTorque;

    private float mass;
    private float momentOfInertia;

    private float angularVelocity = 0;

    public Player() {
        mass = 1f;
        momentOfInertia = 100;

        totalParallelForce = new Vector2(0, 0);
        totalPerpendicularForce = new Vector2(0, 0);
        modules = new Module[ProjectEvolve.NUMMODULES];
        cg = new Vector2(0, 16/ProjectEvolve.PPM);
//        int j;
//        for (j = 0; j < ProjectEvolve.NUMMODULES; j++) {
//            ProjectEvolve.state.setModule(j, (short) 1);
//        }
        //ProjectEvolve.state.setModule(2, (short) 1);
        position = new Vector2(128 / ProjectEvolve.PPM, 128 / ProjectEvolve.PPM);
        velocity = new Vector2(0, 0);
        lastPosition = position;
        rotation = 0;

        // Load the texture for the Modules
        //Texture playerTexture = new Texture("mushroom.png");
        Texture normalModule = new Texture("normalmodule.png");
        Texture attackingModule = new Texture("attackingmodule.png");

        // Create Modules for the player
        int i;
        for(i = 0; i < ProjectEvolve.NUMMODULES; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                // TODO will this cause memory issues? Can't dispose textures if I don't have a reference to it
                modules[i] = new Module(i, new Texture(ProjectEvolve.MODULETEXTURETNAMES[ProjectEvolve.state.getModule(i)]), position.x + ProjectEvolve.MODULELOCATIONS[i][0], position.y + ProjectEvolve.MODULELOCATIONS[i][1], this, ProjectEvolve.PLAYER_BIT);
                modules[i].setCollisionInformation(ProjectEvolve.ENEMY_BIT | ProjectEvolve.EDGE_BIT, ProjectEvolve.PLAYER_BIT);
            }
            //mod1 = new Module(normalModule, position.x, position.y, this, ProjectEvolve.PLAYER_BIT);
            //mod2 = new Module(attackingModule, position.x, position.y + 32 / ProjectEvolve.PPM, this, ProjectEvolve.ATTACKING_BIT);
        }
    }

    public void update(float dt) {
        int i;
        if (unUpdate) {
            for (i = 0; i < ProjectEvolve.NUMMODULES; i++) {
                if (modules[i] != null) {
                    modules[i].update(new Vector2(lastPosition.x + (ProjectEvolve.MODULELOCATIONS[i][0] * xOffset - ProjectEvolve.MODULELOCATIONS[i][1] * yOffset) / ProjectEvolve.PPM, lastPosition.y + (ProjectEvolve.MODULELOCATIONS[i][1] * xOffset + ProjectEvolve.MODULELOCATIONS[i][0] * yOffset) / ProjectEvolve.PPM), rotation, velocity);
                }
            }
            unUpdate = false;
        }
        lastPosition = position;
        // Update velocities based on forces from last collisions / inputs
        velocity = new Vector2(totalParallelForce.x / 60f / mass + velocity.x, totalParallelForce.y / 60f / mass + velocity.y);
        //velocity = new Vector2(totalParallelForce.x * 0, totalParallelForce.y * 0);
        // TODO work in progress
        //System.out.println(velocity.x);
        //System.out.println(velocity.y);
        angularVelocity += totalTorque * 60f / momentOfInertia;
        angularVelocity -= 1 * angularVelocity * dt;

        // Reset forces and torques
        totalParallelForce = new Vector2(0, 0);
        totalTorque = 0;


        hasCollided = false;
        unUpdate = false;
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        //rotation += angularVelocity * dt; // rotation goes CCW, torque / angular velocity goes CW
        //mod1.getBody().setVelocity(velocity);
        //mod2.getBody().setVelocity(velocity);

        // Cosine and Sine of the player's rotation property

        xOffset = (float) Math.cos(Math.toRadians(rotation));
        yOffset = (float) Math.sin(Math.toRadians(rotation));

        // Update player position
//        position.x += velocity.x * dt;
//        position.y += velocity.y * dt;

        // Update all modules
        // TODO update based on dynamic relative locations instead of the static "32 / ProjectEvolve.PPM"

        for (i = 0; i < ProjectEvolve.NUMMODULES; i++) {
            if (modules[i] != null) {
                modules[i].update(new Vector2(position.x + (ProjectEvolve.MODULELOCATIONS[i][0] * xOffset - ProjectEvolve.MODULELOCATIONS[i][1] * yOffset) / ProjectEvolve.PPM, position.y + (ProjectEvolve.MODULELOCATIONS[i][1] * xOffset + ProjectEvolve.MODULELOCATIONS[i][0] * yOffset) / ProjectEvolve.PPM), rotation, velocity/*new Vector2(velocity.x - angularVelocity * (ProjectEvolve.MODULELOCATIONS[i][0] * xOffset - ProjectEvolve.MODULELOCATIONS[i][1] * yOffset) / ProjectEvolve.PPM, velocity.y + angularVelocity * (ProjectEvolve.MODULELOCATIONS[i][1] * xOffset + ProjectEvolve.MODULELOCATIONS[i][0] * yOffset) / ProjectEvolve.PPM)*/);
            }
        }

        Vector2 mod0Position = new Vector2((ProjectEvolve.MODULELOCATIONS[0][0] * xOffset - ProjectEvolve.MODULELOCATIONS[0][1] * yOffset) / ProjectEvolve.PPM, (ProjectEvolve.MODULELOCATIONS[0][1] * xOffset + ProjectEvolve.MODULELOCATIONS[0][0] * yOffset) / ProjectEvolve.PPM);
        Vector2 mod1Position = new Vector2((ProjectEvolve.MODULELOCATIONS[1][0] * xOffset - ProjectEvolve.MODULELOCATIONS[1][1] * yOffset) / ProjectEvolve.PPM, (ProjectEvolve.MODULELOCATIONS[1][1] * xOffset + ProjectEvolve.MODULELOCATIONS[1][0] * yOffset) / ProjectEvolve.PPM);

        cg.x = position.x + (mod0Position.x + mod1Position.y) / 2;
        cg.y = position.y + (mod0Position.y + mod1Position.y) / 2;
//        // Collision detection will happen in these function calls
//        mod1.update(new Vector2(position.x, position.y), rotation);
//        velocity = mod1.newBody.getVelocity();
//        //mod1.newBody.setVelocity(velocity);
//        mod2.newBody.setVelocity(velocity);
////        position = position.add(velocity.scl(0.1f));
////        velocity.scl(1/0.1f);
//        mod2.update(new Vector2(position.x + (32 * xOffset) / ProjectEvolve.PPM, position.y + (32 * yOffset) /ProjectEvolve.PPM), rotation);
//        velocity = mod2.newBody.getVelocity();
//        mod1.newBody.setVelocity(velocity);
////        position = position.add(velocity.scl(0.1f));
////        velocity.scl(1/0.1f);
//        //mod2.newBody.setVelocity(velocity);
//        // Update player position
        //position.x += velocity.x * dt / 2;
        //position.y += velocity.y * dt / 2;
        //System.out.print(position.x);
        //System.out.print(", ");
        //System.out.println(position.y);
    }

    public void render(SpriteBatch batch) {
        int i;
        for (i = 0; i < ProjectEvolve.NUMMODULES; i++) {
            if (modules[i] != null) {
                modules[i].draw(batch);
            }
        }
//        mod1.draw(batch);
//        mod2.draw(batch);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void addVelocity(float x, float y) {
        // Update velocities
        velocity.x += x;
        velocity.y += y;
        // Bounds check x
        if (velocity.x > 2) {
            velocity.x = 2;
        } else if (velocity.x < -2) {
            velocity.x = -2;
        }
        // Bounds check y
        if (velocity.y > 2) {
            velocity.y = 2;
        } else if (velocity.y < -2) {
            velocity.y = -2;
        }
        //mod1.newBody.setVelocity(velocity);
        //mod2.newBody.setVelocity(velocity);
    }

    public void setVelocity(float x, float y) {
        if (!hasCollided) {
            velocity.x = x;
            velocity.y = y;
            //mod1.newBody.setVelocity(velocity);
            //mod2.newBody.setVelocity(velocity);
        }
    }

    public void changeDirection(float x, float y) {

    }

    public void changeDirection(int x, int y) {
        if (x > 0) {

            if (rotation >= 0 && rotation < 180) {
                rotation--;
            } else {
                rotation++;
            }
        } else if (x < 0) {
            if (rotation >= 0 && rotation < 180) {
                rotation++;
            } else {
                rotation--;
            }
        }
        if (y > 0) {
            if (rotation >= -90 && rotation < 90) {
                rotation++;
            } else {
                rotation--;
            }
        } else if (y < 0) {
            if (rotation >= -90 && rotation < 90) {
                rotation--;
            } else {
                rotation++;
            }
        }
        if (rotation > 180) {
            rotation -= 360;
        } else if (rotation < -180) {
            rotation += 360;
        }
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        hasCollided = true;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void hit() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public void giveForce(Module m, Vector2 force) {
        //System.out.print("Force x: ");
        //System.out.println(force.x);
        //System.out.print("Force y: ");
        //System.out.println(force.y);

        int index = m.getIndex();
        Vector2 unitParallel;
        Vector2 parallel = new Vector2(ProjectEvolve.MODULELOCATIONS[index][0] - cg.x, ProjectEvolve.MODULELOCATIONS[index][1] - cg.x);
        if (parallel.len() == 0) {
            unitParallel = new Vector2(0, 0);
        }else {
            unitParallel = new Vector2(parallel.x / parallel.len(), parallel.y / parallel.len());
        }
        Vector2 parallelForce = new Vector2(force.x * unitParallel.x, force.y * unitParallel.y);
        // Currently counter-clockwise perpendicular
        Vector2 unitPerpendicular = new Vector2(-1 * unitParallel.y, unitParallel.x);
        Vector2 perpendicularForce = new Vector2(force.x * unitPerpendicular.x, force.y * unitPerpendicular.y);

        totalParallelForce = new Vector2(totalParallelForce.x + parallelForce.x, totalParallelForce.y + parallelForce.y);
        //totalPerpendicularForce = new Vector2(totalPerpendicularForce.x + parallelForce.x, totalPerpendicularForce.y + perpendicularForce.y);
        //totalTorque = perpendicularForce.x * unitPerpendicular.x + perpendicularForce.y * unitPerpendicular.y;
    }

    public void giveForce(Module m, Vector2 forceVector, Vector2 pointOfForceApplication) {
        //totalParallelForce = forceVector;
//        System.out.print("Force x: ");
//        System.out.println(forceVector.x);
//        System.out.print("Force y: ");
//        System.out.println(forceVector.y);

        int index = m.getIndex();
        Vector2 unitParallel;
        Vector2 parallel = new Vector2(cg.x - ProjectEvolve.MODULELOCATIONS[index][0] / ProjectEvolve.PPM - pointOfForceApplication.x, cg.y - ProjectEvolve.MODULELOCATIONS[index][1] / ProjectEvolve.PPM - pointOfForceApplication.y);
        if (parallel.len() == 0) {
            unitParallel = new Vector2(0, 0);
        }else {
            unitParallel = new Vector2(parallel.x / parallel.len(), parallel.y / parallel.len());
        }
//        System.out.print("Unit Parallel x: ");
//        System.out.println(unitParallel.x);
//        System.out.print("Unit Parallel y: ");
//        System.out.println(unitParallel.y); //good
        Vector2 parallelForce = new Vector2(Math.abs(forceVector.x) * unitParallel.x, Math.abs(forceVector.y) * unitParallel.y);
        // Currently counter-clockwise perpendicular
        Vector2 unitPerpendicular = new Vector2(1 * unitParallel.y, -unitParallel.x);
        //Vector2 perpendicularForce = new Vector2(Math.abs(forceVector.x) * unitPerpendicular.x, Math.abs(forceVector.y) * unitPerpendicular.y);
        System.out.print("Parallel Force x: ");
        System.out.println(parallelForce.x);
        System.out.print("Parallel Force y: ");
        System.out.println(parallelForce.y); //good

//        System.out.print("Perpendicular x: ");
//        System.out.println(unitPerpendicular.x);
//        System.out.print("Perpendicular y: ");
//        System.out.println(unitPerpendicular.y); //good

        totalParallelForce = new Vector2(totalParallelForce.x + parallelForce.x, totalParallelForce.y + parallelForce.y);
        //unUpdate = true;
        totalTorque = totalTorque + forceVector.x * unitPerpendicular.x + forceVector.y * unitPerpendicular.y;
        System.out.print("Torque: ");
        System.out.println(totalTorque);
    }

    public void unUpdate() {
        unUpdate = true;
    }
}
