package com.nic.projectevolve;

/**
 * Created by nic on 11/12/16.
 *
 * This class handles all game state that needs to be shared between distant objects / saved for
 * proper game play.
 */
public class GameState {
    private short[] moduleLocations;

    //TODO numModules needs to be replaced by ProjectEvolve.NUMMODULES
    private static final int numModules = 19;

    public GameState() {
        moduleLocations = new short[numModules];
        int i;
        moduleLocations[0] = 0;
        for(i = 1; i < numModules; i++) {
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
