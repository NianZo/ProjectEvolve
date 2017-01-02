package com.nic.projectevolve;

/**
 * Created by nic on 11/12/16.
 *
 * This class handles all game state that needs to be shared between distant objects / saved for
 * proper game play.
 */
public class GameState {
    public static int geneticMaterial;
    private short[] moduleLocations;

    public GameState() {
        moduleLocations = new short[ProjectEvolve.NUM_MODULES];
        int i;
        moduleLocations[0] = 0;
        for(i = 1; i < ProjectEvolve.NUM_MODULES; i++) {
            moduleLocations[i] = -1;
        }
    }

    public short getModule(int i) {
        return moduleLocations[i];
    }

    public void setModule(int i, short type) {
        moduleLocations[i] = type;
    }
}
