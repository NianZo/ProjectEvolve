package com.nic.projectevolve.tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.sprites.Enemy;

/**
 * Created by nic on 8/7/16.
 *
 * This is simply a listener for collisions. This will call methods on the bodies that caused the
 * collision.
 */
public class WorldContactListener implements ContactListener {

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            case ProjectEvolve.PLAYER_BIT | ProjectEvolve.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == ProjectEvolve.PLAYER_BIT) {
                    ((Enemy) fixB.getUserData()).hit();
                } else {
                    ((Enemy) fixA.getUserData()).hit();
                }
                break;
            case ProjectEvolve.EDGE_BIT | ProjectEvolve.PLAYER_BIT:
//                if (fixA.getFilterData().categoryBits == ProjectEvolve.PLAYER_BIT) {
//                    //((Module) fixA.getUserData()).hitWall();
//                } else {
//                    //((Module) fixB.getUserData()).hitWall();
//                }
//                break;
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }
}
