package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.shapes.Circle;

/**
 * Created by nic on 10/30/16.
 *
 * This class is the matrix on which players can place creature modules. This will allow alteration
 * of the game's State class which will hold the creature blueprint.
 */
public class CreatorMatrix {
    private static final int[][] ADJACENTSOCKETS =
            {{1,2,3,4,5,6}
            ,{7,8,2,0,6,18}
            ,{8,9,10,3,0,1}
            ,{2,10,11,12,4,0}
            ,{0,3,12,13,14,5}
            ,{6,0,4,14,15,16}
            ,{18,1,0,5,16,17}
            ,{8,1,18,-1,-1,-1}
            ,{9,2,1,7,-1,-1}
            ,{10,2,8,-1,-1,-1}
            ,{11,3,2,9,-1,-1}
            ,{12,3,10,-1,-1,-1}
            ,{13,4,3,11,-1,-1}
            ,{14,4,12,-1,-1,-1}
            ,{15,5,4,13,-1,-1}
            ,{16,5,14,-1,-1,-1}
            ,{17,6,5,15,-1,-1}
            ,{18,6,16,-1,-1,-1}
            ,{7,1,6,17,-1,-1}};
    private static final int[][] SOCKETLOCATIONS = {{0,-1},{0,49},{43,25},{43,-25},{1,-50},{-41,-26},{-41,24},{0,100},{42,75},{84,50},{85,0},{85,-50},{43,-75},{2,-100},{-41,-76},{-84,-50},{-83,-1},{-84,48},{-42,73}};
    private static final float SOCKETRADIUS = 50f / 2 / ProjectEvolve.PPM;

    private Sprite sprite;

    private Circle[] sockets;
    private int numSockets = ProjectEvolve.NUM_MODULES;
    private short[] occupied;

    public CreatorMatrix(String textureName, Vector2 position, Vector2 size) {
        sockets = new Circle[numSockets];
        occupied = new short[numSockets];

        // Initialize to all sockets being unoccupied
        for(int i = 0; i < numSockets; i++) {
            occupied[i] = -1;
        }

        // Handles creation of hexagon matrix sprite
        Texture texture = new Texture(textureName);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, size.x / ProjectEvolve.PPM, size.y / ProjectEvolve.PPM);
        sprite.setPosition(position.x - sprite.getWidth() / 2, position.y - sprite.getHeight() / 2);

        // Handles creation and positioning of sockets to drop modules into
        for(int i = 0; i < numSockets; i++) {
//            sockets[i] = new Circle(new Vector2(position.x + SOCKETLOCATIONS[i][0] / ProjectEvolve.PPM, position.y + SOCKETLOCATIONS[i][1] / ProjectEvolve.PPM), SOCKETRADIUS);
            sockets[i] = new Circle(new Vector2(position.x + ProjectEvolve.SOCKET_LOCATIONS[i][0] / ProjectEvolve.PPM, position.y + ProjectEvolve.SOCKET_LOCATIONS[i][1] / ProjectEvolve.PPM), Gdx.graphics.getHeight() * 1 / 16 / ProjectEvolve.PPM);
        }
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    // This will return the socket number that corresponds to the input position
    public int testDrop(Vector2 position) {
        // Iterate through all sockets
        int i = 0;
        while(i < numSockets) {
            // Test if position is in the socket
            if (intersects(position, sockets[i])) {
                // Return the collided socket
                return i;
            }
            i++;
        }
        // Return an invalid socket to test against
        return -1;
    }

    private boolean intersects(Vector2 cursor, Circle socket) {
        Vector2 displacement = new Vector2(socket.getPosition().x - cursor.x, socket.getPosition().y - cursor.y);
        return displacement.len() <= socket.getRadius();
    }

    public int getModuleIndex(int socket) {
        return occupied[socket];
    }

    public Vector2 getSocketLocation(int socket) {
        if(socket == -1) {
            return new Vector2(-1, -1);
        }
        return new Vector2(sockets[socket].getPosition().x - sockets[socket].getRadius(), sockets[socket].getPosition().y - sockets[socket].getRadius());
    }

    public void actuallyDrop(int index, int socket, int type) {
        occupied[socket] = (short) index;
        ProjectEvolve.manager.get("sounds/water_sfx.ogg", Sound.class).play();
        ProjectEvolve.state.setModule(socket, (short) type);
    }

    public void removeModule(int index) {
        occupied[index] = -1;
        ProjectEvolve.state.setModule(index, (short) -1);
        ProjectEvolve.manager.get("sounds/water_sfx.ogg", Sound.class).play();
    }

    // Socket is the socket we want something adjacent to, starting socket is where the module started
    public boolean adjacentOccupied(int newSocket, int oldSocket) {
        // Arrays for testing against
        // connectedList holds the list of modules found to be touching module zero in some way
        int connectedList[] = {0,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        int numConnected = 1;
        // socketList holds all sockets to test (all sockets that need to be touching module zero
        int socketList[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
        int numOccupiedSockets = 0;
        for(int i = 0; i < 19; i++) {
            // Populate socketList with each occupied socket, excluding oldSocket, and including newSocket
            if(occupied[i] != -1 && i != oldSocket || i == newSocket) {
                socketList[numOccupiedSockets] = i;
                numOccupiedSockets++;
            }
        }

        boolean connected = false;
        // Iterate through all filled sockets except zero socket (zero socket already touches zero socket)
        for(int i = 1; i < numOccupiedSockets; i++) {
            // For each filled socket, iterate through connectedList
            for(int j = 0; j < numConnected; j++) {
                // See if the filled socket is adjacent to the connected socket
                if (!connected && isAdjacentTo(socketList[i], connectedList[j])) {
                    connectedList[numConnected] = socketList[i];
                    numConnected++;
                    connected = true;
                }
            }
            // If the filled socket was not connected to any sockets in the connectedList, then it is not connected to the zero socket and the test fails
            if(!connected) {
                return false;
            }
            // Reset connected flag for next filled socket
            connected = false;
        }
        // If no filled sockets failed then test is successful, return true;
        return true;
    }

    // Helper function for adjacentOccupied; tests to see if a single socket is adjacent to a single other socket
    private boolean isAdjacentTo(int socket, int socketToTest) {
        boolean adjacent = false;

        for(int i = 0; i < 6; i++) {
            if(!adjacent) {
                adjacent = ADJACENTSOCKETS[socket][i] == socketToTest;
            }
        }
        return adjacent;
    }
}
