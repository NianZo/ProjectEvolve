package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/14/16.
 *
 * Modules are parts of the Player character and enemies. A single enemy or player can be made of
 * multiple Modules.
 *
 * Modules should handle their own collisions.
 *
 * Modules handle all Box2DBody code. I may make my own collision engine from scratch later, this
 * will allow me to only change the Module class to implement this.
 */
public class Module extends Sprite {

    // TEST CODE FOR NEW PHYSICS ENGINE
    public com.nic.projectevolve.physics.Body newBody;

    public static World world;
    //public Body b2body;
    // TODO this is only needed for the collision work around
    //private Player player;
    //private Enemy enemy;

    //private int index;

//    private Vector2 lastPosition;
//    private float lastRotation;
    private Vector2 position;
    //private float rotation;

    // Constructor for a Module. Takes in a texture, position, and player
    // TODO consider changing position to a Vector2?
    public Module(int i, Texture texture, float x, float y, short identity) {
        super(texture);
        world = PlayScreen.getWorld();
        // TODO this is only needed for the collision work around
        //this.player = player;
        position = new Vector2(x, y);

        //index = i;

        //definePlayer(new Vector2(x, y));
        setBounds(0, 0, 32 / ProjectEvolve.PPM, 32 / ProjectEvolve.PPM);
        setRegion(texture);
        setOriginCenter();

        //this.setCollisionIdentity(identity);
    }

    // TODO do I need dt in this method?
//    public void update(Vector2 position, float rotation, Vector2 velocity) {
////        lastPosition = this.position;
////        lastRotation = this.rotation;
//        this.position = position;
//        this.rotation = rotation;
//        // Set the sprite rotation to the player's rotation
//        setRotation(rotation);
//        // Set the position of the Box2DBody to the correct location
//        b2body.setTransform(position, 0);
//        // Set the sprite position to the player's position
//        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
//        newBody.setPosition(position, velocity);
//        // TESTING CODE FOR NEW PHYSICS ENGINE
//        if (position.x != newBody.getPositionX() || position.y != newBody.getPositionY()) {
//            newBody.setPosition(position, velocity);
//        }
//    }

    public void update() {
        position = newBody.getPosition();
        float rotation = newBody.getRotation();

        setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
        setRotation(rotation);
    }

    // Handles the creation of the Module's Box2DBody and Box2DBody's fixture
//    public void definePlayer(Vector2 position) {
//        // TESTING CODE FOR NEW PHYSICS ENGINE
////        newBody = new com.nic.projectevolve.physics.Body(player, position, 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM, true);
////        newBody.setModule(this);
////        newBody.setCollisionIdentity(ProjectEvolve.PLAYER_BIT);
////        newBody.setCollisionMask((short)(ProjectEvolve.ENEMY_BIT | ProjectEvolve.EDGE_BIT));
//
//        // Setup our Box2DBody
//        BodyDef bdef = new BodyDef();
//        bdef.position.set(position.x, position.y);
//        bdef.type = BodyDef.BodyType.DynamicBody;
//        bdef.allowSleep = false; // Forces bodies to always remain awake
//        b2body = world.createBody(bdef);
//        // Set up the fixture for our body
//        // Currently uses a circle of radius 16 for the collision box
//        // Currently collides with EDGE and ENEMY
//        FixtureDef fdef = new FixtureDef();
//        CircleShape shape = new CircleShape();
//        shape.setRadius(16 / ProjectEvolve.PPM);
//        fdef.shape = shape;
//        fdef.filter.categoryBits = ProjectEvolve.PLAYER_BIT;
//        fdef.filter.maskBits =
//                        ProjectEvolve.EDGE_BIT |
//                        ProjectEvolve.ENEMY_BIT;
//        b2body.createFixture(fdef).setUserData(this);
//    }

    // TODO this will replace definePlayer
    public void setBody(com.nic.projectevolve.physics.Body body) {
        newBody = body;
        //System.out.println(newBody.getPosition().x);
    }

    // TODO currently player handles this because a work around is needed for bouncing off
//    public void hitWall() {
//        player.hitWall();
//    }

    public com.nic.projectevolve.physics.Body getBody() {
        return newBody;
    }

//    public void setCollisionInformation(int mask, int identity) {
//        newBody.setCollisionMask((short)mask);
//        newBody.setCollisionIdentity((short)identity);
//    }

//    public void setCollisionIdentity(short identity) {
//        newBody.setCollisionIdentity(identity);
//    }

//    public void hit(Module module) {
//        System.out.println("Module.hit function");
//        if (module.getBody().getCollisionIdentity() == ProjectEvolve.ENEMY_BIT) {
//            if (newBody.getCollisionIdentity() == ProjectEvolve.ATTACKING_BIT) {
//                module.getEnemy().hit();
//                System.out.println("Destroy enemy.");
//            } else if (newBody.getCollisionIdentity() == ProjectEvolve.PLAYER_BIT) {
//                this.getPlayer().hit();
//            }
//        }
//    }

//    public Player getPlayer() {
//        return player;
//    }

//    public int getIndex() {
//        return index;
//    }

    public Vector2 getPosition() {
        return position;
    }
}
