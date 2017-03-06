package com.nic.projectevolve;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Created by nic on 11/12/16.
 *
 * This class handles all game state that needs to be shared between distant objects / saved for
 * proper game play.
 */
public class GameState {
    public static int geneticMaterial;
    private short[] moduleLocations;
    //public static int playerSpeedLevel = 1;
    //public static int playerDefenseLevel = 1;
    //public static int playerAttackLevel = 1;
    public static int[] moduleLevels = {1,1,1};
    public static int[] unlockedLevels = {1,0,0,0,0,0,0,0,0,0,0};
    //public static int[] unlockedLevels = {1,1,1,1,1,1,1,1,1,1,1};

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

    public void saveStateToFile() {
        byte geneticMaterialByte = (byte) geneticMaterial;
        byte[] moduleLevelBytes = new byte[3];
        for(int i = 0; i < 3; i++) {
            moduleLevelBytes[i] = (byte) moduleLevels[i];
        }
        byte[] moduleLocationBytes = new byte[ProjectEvolve.NUM_MODULES];
        for(int i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
            moduleLocationBytes[i] = (byte) moduleLocations[i];
        }
        FileHandle file = Gdx.files.local("game_state.txt");
        file.writeBytes(new byte[] {geneticMaterialByte, moduleLevelBytes[0], moduleLevelBytes[1], moduleLevelBytes[2],
            moduleLocationBytes[0], moduleLocationBytes[1], moduleLocationBytes[2], moduleLocationBytes[3], moduleLocationBytes[4],
            moduleLocationBytes[5], moduleLocationBytes[6], moduleLocationBytes[7], moduleLocationBytes[8], moduleLocationBytes[9],
            moduleLocationBytes[10], moduleLocationBytes[11], moduleLocationBytes[12], moduleLocationBytes[13], moduleLocationBytes[14],
            moduleLocationBytes[15], moduleLocationBytes[16], moduleLocationBytes[17], moduleLocationBytes[18]}, false);
    }

    public void readStateFromFile() {
        FileHandle file = Gdx.files.local("game_state.txt");
        byte[] data = file.readBytes();
        geneticMaterial = (int) data[0];
        for(int i = 1; i < 4; i++) {
            moduleLevels[i - 1] = (int) data[i];
        }
        for(int i = 4; i < 4 + ProjectEvolve.NUM_MODULES; i++) {
            moduleLocations[i - 4] = (short) data[i];
            System.out.println(moduleLocations[i - 4]);
        }
    }
}
