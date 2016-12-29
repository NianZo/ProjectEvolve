package com.nic.projectevolve.physics;

import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.sprites.Enemy;
import com.nic.projectevolve.sprites.Player;

/**
 * Created by nic on 9/24/16.
 *
 * This class contains functions to be called on collisions. This connects to game code by casting
 * "userData" from the Body class into game code.
 */
public class ContactListener {
    public static void contact(Body a, Body b) {
        if (a.getCollisionIdentity() == ProjectEvolve.PLAYER_BIT && b.getCollisionIdentity() == ProjectEvolve.ENEMY_BIT) {
            Player player = (Player) a.getUserData();
            Enemy enemy = (Enemy) b.getUserData();
            player.addEnergy(-10);
            enemy.addEnergy(-10);
            if(enemy.isDead()) {
                player.addEnergy(50);
                ProjectEvolve.geneticMaterial++;
            }
        }
        if (a.getCollisionIdentity() == (ProjectEvolve.PLAYER_BIT | ProjectEvolve.ATTACKING_BIT) && b.getCollisionIdentity() == ProjectEvolve.ENEMY_BIT) {
            Player player = (Player) a.getUserData();
            Enemy enemy = (Enemy) b.getUserData();
            player.addEnergy(-5);
            enemy.addEnergy(-30);
            if(enemy.isDead()) {
                player.addEnergy(50);
                ProjectEvolve.geneticMaterial++;
            }
        }
    }
}
