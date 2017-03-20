package com.nic.projectevolve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nic.projectevolve.GameState;
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

    private TextButton screenBlank;
    private boolean drawScreenBlank;
    private float stateTimer;
    private Stage stage;

    private int levelNumber;

    private Music music;

    public PlayScreen(ProjectEvolve game, int levelNumber) {
        this.game = game;
        this.levelNumber = levelNumber;

        // Create gameCam and gamePort and center gameCam (makes 0,0 bottom left)
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);
//        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        inputScaleAdjuster = new Vector2(1, 1);

        // Load the map and give it to the renderer with the pixel scale
        TmxMapLoader mapLoader = new TmxMapLoader();
        TiledMap map = mapLoader.load(ProjectEvolve.LEVEL_NAMES[levelNumber]);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / ProjectEvolve.PPM);

        // Create the bodyList (note that this must be done before any collision bodies are created anywhere)
        bodyList = new BodyList();
        BodyList.setPPM(ProjectEvolve.PPM);

        // Create the HUD
        hud = new Hud(game.batch);

        // Create the player
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            player = new Player(rect.getX() / ProjectEvolve.PPM, rect.getY() / ProjectEvolve.PPM);
        }
        player.update(0);
        gameCam.position.set(player.getPosition().x, player.getPosition().y, 0);

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
            Enemy enemy = new Enemy(player, new Vector2(rect.getX() / ProjectEvolve.PPM, rect.getY() / ProjectEvolve.PPM), levelNumber);
            enemies.add(enemy);
            enemy.update(0);
        }

        createButton();
        drawScreenBlank = true;
        stateTimer = 0;

        // Clamp the gameCam if near the edge of the map
        if(player.getPosition().x >= gamePort.getWorldWidth() / 2 && player.getPosition().x <= (ProjectEvolve.MAP_TILE_WIDTH * 32 / ProjectEvolve.PPM - gamePort.getWorldWidth() / 2)) {
            gameCam.position.x = player.getPosition().x;
        }
        if(player.getPosition().y >= gamePort.getWorldHeight() / 2 && player.getPosition().y <= (ProjectEvolve.MAP_TILE_HEIGHT * 32 / ProjectEvolve.PPM - gamePort.getWorldHeight() / 2)) {
            gameCam.position.y = player.getPosition().y;
        }

        music = ProjectEvolve.manager.get("sounds/play_theme.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.33f);
        music.play();


    }

    private void createButton() {
        // Creating a texture named white and adding it to skin
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        Skin skin = new Skin();

        skin.add("white", new Texture(pixmap));

        // Store default libGdx font as "default"
        BitmapFont bFont = new BitmapFont();
        // skipping a scale line because BitmapFont.scale(int) is undefined
        skin.add("default", bFont);
        skin.getFont("default").getData().setScale(4.0f);

        // Configuring a TextButtonStyle named "default"
        Color uncheckedColor = new Color(0.25f, 0.25f, 0.25f, 0.5f);
        //Color checkedColor = new Color(0.5f, 0.5f, 0.5f, 0.75f);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", uncheckedColor);
        textButtonStyle.down = skin.newDrawable("white", uncheckedColor);
        textButtonStyle.checked = skin.newDrawable("white", uncheckedColor);
        textButtonStyle.over = skin.newDrawable("white", uncheckedColor);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        screenBlank = new TextButton("", skin);
        screenBlank.setText("Destroy the Other Cells!");
        screenBlank.setPosition(0, 0);
        screenBlank.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        stage = new Stage();
        stage.addActor(screenBlank);
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
        renderer.setView(gameCam);

        // Check for beginning / ending sequence first
        if(drawScreenBlank) {
            // Update HUD
            hud.update(player.getEnergy());
            // Clamp the gameCam if near the edge of the map
            if(player.getPosition().x < gamePort.getWorldWidth() / 2) {
                gameCam.position.x = gamePort.getWorldWidth() / 2;
            } else if(player.getPosition().x > (ProjectEvolve.MAP_TILE_WIDTH * 32 / ProjectEvolve.PPM - gamePort.getWorldWidth() / 2)) {
                gameCam.position.x = ProjectEvolve.MAP_TILE_WIDTH * 32 / ProjectEvolve.PPM - gamePort.getWorldWidth() / 2;
            }
            if(player.getPosition().y < gamePort.getWorldHeight() / 2) {
                gameCam.position.y = gamePort.getWorldHeight() / 2;
            } else if(player.getPosition().y > (ProjectEvolve.MAP_TILE_HEIGHT * 32 / ProjectEvolve.PPM - gamePort.getWorldHeight() / 2)) {
                gameCam.position.y = ProjectEvolve.MAP_TILE_HEIGHT * 32 / ProjectEvolve.PPM - gamePort.getWorldHeight() / 2;
            }
            gameCam.update();
            renderer.setView(gameCam);
            stateTimer += dt;
            if(stateTimer > 3.0f) {
                drawScreenBlank = false;
                stateTimer = 0;
                if(player.isDead() || enemies.size() == 0) {
                    dispose();
                    game.setScreen(new MenuScreen(game));

                }
            }
            return;
        }

        // Handle input before updating anything
        handleInput(dt);


        // Update player
        player.update(dt);

        // Update enemies
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update(dt);
            if(enemies.get(i).isDead()) {
                enemies.get(i).dispose();
                enemies.remove(i);
            }
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
        System.out.println(player.getPosition().x);

        // Update the gameCame
        gameCam.update();

        // For the world, only render what the gameCam can see
        renderer.setView(gameCam);

        if(enemies.size() == 0 && !player.isDead()) {
            if(levelNumber < ProjectEvolve.NUM_LEVELS - 1) {
                GameState.unlockedLevels[levelNumber + 1] = 1;
            }
            screenBlank.setText("Success!");
            drawScreenBlank = true;
            //dispose();
            //game.setScreen(new MenuScreen(game));
        }

        if(player.isDead()) {
            screenBlank.setText("You Died");
            drawScreenBlank = true;
        }
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

        if(drawScreenBlank) {
            stage.draw();
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
        music.stop();
        //music.dispose();
        //GameState.unlockedLevels[levelNumber + 1] = 1;
        ProjectEvolve.state.saveStateToFile();
    }
}
