package com.nic.projectevolve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.BodyList;
import com.nic.projectevolve.sprites.Enemy;
import com.nic.projectevolve.sprites.Player;
import com.nic.projectevolve.tools.WorldContactListener;

/**
 * Created by nic on 8/4/16.
 *
 * This is the screen that handles game play.
 *
 * Currently loads player, enemy, map, and Box2DBodies for the map. Also handles camera, hud creation
 * and input.
 */
public class PlayScreen implements Screen{
    private ProjectEvolve game;
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    //private Hud hud;

    private OrthogonalTiledMapRenderer renderer;

    private static World world;
    private Box2DDebugRenderer b2dr;

    private Player player;
    private Enemy enemy;

    public static BodyList bodyList;

    public PlayScreen(ProjectEvolve game) {
        // TODO moved from class variables, not sure if this is a perfect solution
        com.nic.projectevolve.physics.Body newBody;
        TmxMapLoader mapLoader;
        TiledMap map;

        bodyList = new BodyList();

        this.game = game;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);
        //hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("prealphaworld.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / ProjectEvolve.PPM);
        // to center map (originally around origin)
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new WorldContactListener()); // test code
        b2dr = new Box2DDebugRenderer();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        player = new Player();
        enemy = new Enemy(); // TODO needs changed to implement more enemies

        // TODO move to a B2WorldCreator class, add other objects as needed
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // Could probably keep everything above this if implementing own collision system
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / ProjectEvolve.PPM, (rect.getY() + rect.getHeight() / 2) / ProjectEvolve.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / ProjectEvolve.PPM, rect.getHeight() / 2 / ProjectEvolve.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = ProjectEvolve.EDGE_BIT;
            body.createFixture(fdef);

            // Physics Engine testing code
            newBody = new com.nic.projectevolve.physics.Body(null, new Vector2((rect.getX() + rect.getWidth() / 2) / ProjectEvolve.PPM, (rect.getY() + rect.getHeight() / 2) / ProjectEvolve.PPM),
                    Math.abs(rect.getWidth()) / (2 * ProjectEvolve.PPM), Math.abs(rect.getHeight()) / (2 * ProjectEvolve.PPM), false, 0);
            newBody.setCollisionIdentity((short) 1);
            newBody.setCollisionMask((short) 0);
            System.out.println("Border Object Created");
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (Gdx.input.isTouched()) {
            float velocityScaleFactor = 100;
            Vector2 direction = new Vector2(Gdx.input.getX() / ProjectEvolve.PPM - (player.getPosition().x - gameCam.position.x + gamePort.getWorldWidth() / 2), -Gdx.input.getY() / ProjectEvolve.PPM + (-player.getPosition().y + gameCam.position.y + gamePort.getWorldHeight() / 2));
            Vector2 unitDirection = new Vector2(direction.x / direction.len(), direction.y / direction.len());
            player.addVelocity(unitDirection.x * dt * velocityScaleFactor, unitDirection.y * dt * velocityScaleFactor);
        }
    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);
        player.update(dt);
        //hud.update(dt);
        enemy.update(); // TODO more demo enemy code here

        // Clamp the gameCam if near the edge of the map
        if(player.getPosition().x >= gamePort.getWorldWidth() / 2 && player.getPosition().x <= (ProjectEvolve.MAPTILEWIDTH * 32 / ProjectEvolve.PPM - gamePort.getWorldWidth() / 2)) {
            gameCam.position.x = player.getPosition().x;
        }
        if(player.getPosition().y >= gamePort.getWorldHeight() / 2 && player.getPosition().y <= (ProjectEvolve.MAPTILEHEIGHT * 32 / ProjectEvolve.PPM - gamePort.getWorldHeight() / 2)) {
            gameCam.position.y = player.getPosition().y;
        }

        gameCam.update();
        renderer.setView(gameCam); // only render what gameCam can see
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        b2dr.render(world, gameCam.combined);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.render(game.batch);
        enemy.render(game.batch); // TODO more demo enemy code here
        game.batch.end();

        //game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        //hud.stage.draw();

        if (player.isDead()) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    public static World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
