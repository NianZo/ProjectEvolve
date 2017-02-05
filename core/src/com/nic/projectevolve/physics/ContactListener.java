package com.nic.projectevolve.physics;

import com.nic.projectevolve.GameState;
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
        if((a.getCollisionIdentity() & ProjectEvolve.PLAYER_BIT) != 0 && (b.getCollisionIdentity() & ProjectEvolve.ENEMY_BIT) != 0) {
            Player player = (Player) a.getUserData();
            Enemy enemy = (Enemy) b.getUserData();

            // Calculate attack damage
            int attackDamage = 5;
            if((a.getCollisionIdentity() & ProjectEvolve.ATTACKING_BIT) != 0) {
                attackDamage += 5 * GameState.moduleLevels[1];
            }
            if((b.getCollisionIdentity() & ProjectEvolve.DEFENDING_BIT) != 0) {
                attackDamage -= 5 * enemy.getDefenseLevel();
                if(attackDamage < 0) {
                    attackDamage = 0;
                }
            }
            enemy.addEnergy(-1 * attackDamage);

            // Calculate damage the enemy does to the player
            int defenseDamage = 5;
            if((b.getCollisionIdentity() & ProjectEvolve.ATTACKING_BIT) != 0) {
                defenseDamage += 5 * enemy.getAttackLevel();
            }
            if((a.getCollisionIdentity() & ProjectEvolve.DEFENDING_BIT) != 0) {
                defenseDamage -= 5 * GameState.moduleLevels[2];
                if(defenseDamage < 0) {
                    defenseDamage = 0;
                }
            }
            player.addEnergy(-1 * defenseDamage);

            // Add energy and resource if enemy dies
            if(enemy.isDead()) {
                player.addEnergy(50);
                GameState.geneticMaterial++;
            }
        } else if ((b.getCollisionIdentity() & ProjectEvolve.PLAYER_BIT) != 0) {
            Player player = (Player) b.getUserData();
            Enemy enemy = (Enemy) a.getUserData();

            // Calculate attack damage
            int attackDamage = 5;
            attackDamage += 5 * GameState.moduleLevels[1];
            if((a.getCollisionIdentity() & ProjectEvolve.DEFENDING_BIT) != 0) {
                attackDamage -= 5 * enemy.getDefenseLevel();
                if(attackDamage < 0) {
                    attackDamage = 0;
                }
            }
            enemy.addEnergy(-1 * attackDamage);

            // Calculate damage the enemy does to the player
            int defenseDamage = 5;
            defenseDamage += 5 * enemy.getAttackLevel();
            if((b.getCollisionIdentity() & ProjectEvolve.DEFENDING_BIT) != 0) {
                defenseDamage -= 5 * GameState.moduleLevels[2];
                if(defenseDamage < 0) {
                    defenseDamage = 0;
                }
            }
            player.addEnergy(-1 * defenseDamage);

            // Add energy and resource if enemy dies
            if(enemy.isDead()) {
                player.addEnergy(50);
                GameState.geneticMaterial++;
            }
        }
//        if (a.getCollisionIdentity() == ProjectEvolve.PLAYER_BIT && b.getCollisionIdentity() == ProjectEvolve.ENEMY_BIT) {
//            Player player = (Player) a.getUserData();
//            Enemy enemy = (Enemy) b.getUserData();
//            player.addEnergy(-10);
//            enemy.addEnergy(-10);
//            if(enemy.isDead()) {
//                player.addEnergy(50);
//                GameState.geneticMaterial++;
//            }
//        }
//        if (a.getCollisionIdentity() == (ProjectEvolve.PLAYER_BIT | ProjectEvolve.ATTACKING_BIT) && b.getCollisionIdentity() == ProjectEvolve.ENEMY_BIT) {
//            Player player = (Player) a.getUserData();
//            Enemy enemy = (Enemy) b.getUserData();
//            player.addEnergy(-5);
//            enemy.addEnergy(-30);
//            if(enemy.isDead()) {
//                player.addEnergy(50);
//                GameState.geneticMaterial++;
//            }
//        }
    }
}
