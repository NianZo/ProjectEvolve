package com.nic.projectevolve.physics;

import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 9/24/16.
 */
public class ContactListener {
    public static void contact(Body a, Body b) {
        if (a.getCollisionIdentity() != ProjectEvolve.ENEMY_BIT) {
            //a.getModule().hit(b.getModule());
        }
    }
}
