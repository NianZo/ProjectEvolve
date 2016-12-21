package com.nic.projectevolve.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.scenes.Hud;
import com.nic.projectevolve.screens.PlayScreen;

/**
 * Created by nic on 8/6/16.
 *
 * This is the main class for enemies. Eventually these will be able to move on their own and attack.
 */
public class Enemy extends Sprite {
    public World world;
    public Body b2body;

    private boolean setToDestroy;
    private boolean destroyed;

    private Module mod;

    private Vector2 position;
    private float rotation;

    public Enemy() {
        position = new Vector2(256 / ProjectEvolve.PPM, 256 / ProjectEvolve.PPM);
        rotation = 0f;
        //super(screen.getTexture());
//        this.world = world;
//        definePlayer();
//        setBounds(0, 0, 16 / ProjectEvolve.PPM, 16 / ProjectEvolve.PPM);
//        setRegion(screen.getTexture());
        Texture enemyTexture = new Texture("mushroom.png");
        setToDestroy =false;
        destroyed = false;

        mod = new Module(enemyTexture, 256, 256);
        mod.setCollisionInformation(/*ProjectEvolve.PLAYER_BIT*/0, ProjectEvolve.ENEMY_BIT);
        mod.setEnemy(this);
    }

    public void update() {
        //setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setPosition(position.x - getWidth() / 2, position.y - getHeight() / 2);
        mod.update(position, rotation, new Vector2(0, 0));
        if (setToDestroy && !destroyed) {
            //world.destroyBody(b2body);
            destroyed = true;
            PlayScreen.bodyList.RemoveBody(mod.getBody());

            // TESTING CODE FOR NEW PHYSICS ENGINE

        }
    }

    public void render(SpriteBatch batch) {
        if (!destroyed) {
            mod.draw(batch);
        }
    }

//    public void definePlayer() {
//        BodyDef bdef = new BodyDef();
//        bdef.position.set(256 / ProjectEvolve.PPM, 256 / ProjectEvolve.PPM);
//        bdef.type = BodyDef.BodyType.DynamicBody;
//        b2body = world.createBody(bdef);
//
//        FixtureDef fdef = new FixtureDef();
//        CircleShape shape = new CircleShape();
//        shape.setRadius(16 / ProjectEvolve.PPM);
//
//        fdef.shape = shape;
//        fdef.filter.categoryBits = ProjectEvolve.ENEMY_BIT;
//        fdef.filter.maskBits =
//                ProjectEvolve.EDGE_BIT |
//                ProjectEvolve.PLAYER_BIT;
//        b2body.createFixture(fdef).setUserData(this);
//    }

    public void hit() {
        setToDestroy = true;
        //Hud.addScore(1);
    }
}
