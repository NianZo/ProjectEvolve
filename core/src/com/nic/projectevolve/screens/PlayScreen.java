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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.physics.Body;
import com.nic.projectevolve.physics.BodyGroup;
import com.nic.projectevolve.physics.BodyList;
import com.nic.projectevolve.scenes.Hud;
import com.nic.projectevolve.sprites.Enemy;
import com.nic.projectevolve.sprites.Player;

import java.util.ArrayList;

/**
 * Created by nic on 8/4/16.
 *
 * This is the screen that handles game play.
 *
 * Currently loads player, enemy, map, and Box2DBodies for the map. Also handles camera, hud creation
 * and input.
 */
public class PlayScreen implements Screen{
    // This has to be passed since the spriteBatch shouldn't be static
    private ProjectEvolve game;

    // Required objects for rendering all parts of the scene
    private OrthographicCamera gameCam;
    private Viewport gamePort;
    private OrthogonalTiledMapRenderer renderer;

    private Hud hud;

    private Player player;
    private ArrayList<Enemy> enemies;

    // Used to scale input when the display size changes
    private Vector2 inputScaleAdjuster;

    // Create the body list that will handle all collision
    public static BodyList bodyList;

    public PlayScreen(ProjectEvolve game) {
        this.game = game;

        // Create gameCam and gamePort and center gameCam (makes 0,0 bottom left)
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);
        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        inputScaleAdjuster = new Vector2(1, 1);

        // Load the map and give it to the renderer with the pixel scale
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load("pre_alpha_world.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / ProjectEvolve.PPM);

        // Create the bodyList (note that this must be done before any collision bodies are created anywhere)
        bodyList = new BodyList();
        BodyList.setPPM(ProjectEvolve.PPM);

        // Create the HUD
        hud = new Hud(game.batch);

        // Create the player
        player = new Player();

        // Create border objects based on the .tmx level file
        BodyGroup edgeGroup = new BodyGroup(new Vector2(0, 0), bodyList, 0, 0);
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            Body newBody = new Body(edgeGroup, new Vector2((rect.getX() + rect.getWidth() / 2) / ProjectEvolve.PPM, (rect.getY() + rect.getHeight() / 2) / ProjectEvolve.PPM),
                    Math.abs(rect.getWidth()) / (2 * ProjectEvolve.PPM), Math.abs(rect.getHeight()) / (2 * ProjectEvolve.PPM), false);
            newBody.setCollisionIdentity(ProjectEvolve.EDGE_BIT);
            newBody.setCollisionMask(ProjectEvolve.NOTHING_BIT);
            edgeGroup.addBody(newBody);
        }

        // Create enemies based on the .tmx level file
        enemies = new ArrayList<Enemy>();
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            Enemy enemy = new Enemy(player, new Vector2(rect.getX() / ProjectEvolve.PPM, rect.getY() / ProjectEvolve.PPM));
            enemies.add(enemy);
        }
    }

    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        // If the screen is touched apply a force to the player
        if (Gdx.input.isTouched()) {
            // Correction factor to get the force in the range needed
            float velocityScaleFactor = 150;

            // Calculate vector and unit vector from the player to the touch location
            Vector2 direction = new Vector2((Gdx.input.getX() - gamePort.getLeftGutterWidth()) * inputScaleAdjuster.x / ProjectEvolve.PPM - (player.getPosition().x - gameCam.position.x + gamePort.getWorldWidth() / 2), (-Gdx.input.getY() + gamePort.getTopGutterHeight()) * inputScaleAdjuster.y / ProjectEvolve.PPM + (-player.getPosition().y + gameCam.position.y + gamePort.getWorldHeight() / 2));
            Vector2 unitDirection = new Vector2(direction.x / direction.len(), direction.y / direction.len());

            // Apply a force of magnitude velocityScaleFactor * dt in the direction from the player to the cursor
            player.giveForce(unitDirection.scl(dt).scl(velocityScaleFactor));
        }
    }

    public void update(float dt) {
        // Handle input before updating anything
        handleInput(dt);

        // Update player
        player.update(dt);

        // Update enemies
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update(dt);
        }

        // Update HUD
        hud.update(player.getEnergy());

        // Clamp the gameCam if near the edge of the map
        if(player.getPosition().x >= gamePort.getWorldWidth() / 2 && player.getPosition().x <= (ProjectEvolve.MAP_TILE_WIDTH * 32 / ProjectEvolve.PPM - gamePort.getWorldWidth() / 2)) {
            gameCam.position.x = player.getPosition().x;
        }
        if(player.getPosition().y >= gamePort.getWorldHeight() / 2 && player.getPosition().y <= (ProjectEvolve.MAP_TILE_HEIGHT * 32 / ProjectEvolve.PPM - gamePort.getWorldHeight() / 2)) {
            gameCam.position.y = player.getPosition().y;
        }

        // Update the gameCame
        gameCam.update();

        // For the world, only render what the gameCam can see
        renderer.setView(gameCam);
    }

    @Override
    public void render(float delta) {
        // Update everything before rendering
        update(delta);

        // Clear the screen to black and clear the color buffer
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the world
        renderer.render();

        // Render player and enemies
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        player.render(game.batch);
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).render(game.batch);
        }
        game.batch.end();

        // Render the HUD
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        // If the player is dead, go to the menu screen and dispose of resources from this screen
        if (player.isDead()) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        inputScaleAdjuster.x = (float) ProjectEvolve.V_WIDTH / (width - gamePort.getLeftGutterWidth() - gamePort.getRightGutterWidth());
        inputScaleAdjuster.y = (float) ProjectEvolve.V_HEIGHT / (height - gamePort.getTopGutterHeight() - gamePort.getBottomGutterHeight());
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
        renderer.dispose();
        hud.dispose();
        player.dispose();
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).dispose();
        }
        ProjectEvolve.state.saveStateToFile();
    }
}
