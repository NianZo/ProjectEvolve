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

    // TODO TEST CODE
    //private boolean unUpdate;
    private BodyGroup bodyGroup;
    private int numModules;

    public Player() {
        modules = new Module[ProjectEvolve.NUMMODULES];
        cg = new Vector2(0, 16/ProjectEvolve.PPM);

        numModules = 0;
        position = new Vector2(128 / ProjectEvolve.PPM, 128 / ProjectEvolve.PPM);
        bodyGroup = new BodyGroup(position);

        // Create Modules for the player
        int i;
        for(i = 0; i < ProjectEvolve.NUMMODULES; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                System.out.println(i);
                // TODO will this cause memory issues? Can't dispose textures if I don't have a reference to it
                modules[numModules] = new Module(numModules, new Texture(ProjectEvolve.MODULETEXTURETNAMES[ProjectEvolve.state.getModule(i)]), position.x + ProjectEvolve.MODULELOCATIONS[i][0] / ProjectEvolve.PPM, position.y + ProjectEvolve.MODULELOCATIONS[i][1] / ProjectEvolve.PPM, ProjectEvolve.PLAYER_BIT);
                System.out.println(modules[numModules].getPosition().x);
                Body newBody = new Body(bodyGroup, modules[numModules].getPosition(), 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true, i);
                newBody.setCollisionIdentity(ProjectEvolve.PLAYER_BIT);
                newBody.setCollisionMask((short) (ProjectEvolve.ENEMY_BIT | ProjectEvolve.EDGE_BIT));
                bodyGroup.addBody(newBody);
                modules[numModules].setBody(newBody);
                numModules++;
            }
        }
    }

    public void update(float dt) {
        bodyGroup.update(dt);
        position = bodyGroup.getPosition();

        for(int i = 0; i < numModules; i++) {
            modules[i].update();
        }
    }

    public void render(SpriteBatch batch) {
        int i;
        for (i = 0; i < ProjectEvolve.NUMMODULES; i++) {
            if (modules[i] != null) {
                modules[i].draw(batch);
            }
        }
    }

    // TODO refactor this, don't need some functionality and the name needs changed
    public void addVelocity(float x, float y) {
//        System.out.print("Direction of Application: ");
//        System.out.println(x);
//        System.out.print("Direction of Application: ");
//        System.out.println(y);
        bodyGroup.applyForce(new Vector2(x, y), new Vector2(0, 0), modules[0].getBody());
    }

    public Vector2 getPosition() {
        return position;
    }

    // TODO reimplement this once physics actually works
    public void hit() {
        dead = true;
    }

    public boolean isDead() {
        return dead;
    }
}
