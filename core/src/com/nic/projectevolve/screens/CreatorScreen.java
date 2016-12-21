package com.nic.projectevolve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.uiComponents.CreatorMatrix;
import com.nic.projectevolve.uiComponents.DraggableImage;
import com.nic.projectevolve.uiComponents.ModuleSpawner;

/**
 * Created by nic on 9/25/16.
 *
 * This screen handles modifying the structure of the player character. This screen will also go
 * back to the menuScreen via a button.
 */
public class CreatorScreen implements Screen{
    private ProjectEvolve game;
    private Skin skin;
    private Stage stage;

    public SpriteBatch batch;

    private OrthographicCamera gameCam;
    private FitViewport gamePort;

    private TextButton menuButton;
    private DraggableImage[] dragImages;
    private CreatorMatrix creatorMatrix;
    private ModuleSpawner blueSpawner;

    private DraggableImage dragImage1;

    private int blueSpawners;

    public CreatorScreen(ProjectEvolve game) {
        //Gdx.input.setInputProcessor(stage);
        dragImages = new DraggableImage[19];
        blueSpawners = 0;
        createButtons();

        this.game = game;

        batch = new SpriteBatch();

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // TODO Debug code
        //dragImage1 = new DraggableImage("normalmodule.png", new Vector2(0, 0));
    }

    private void createButtons() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Setup default font for text buttons
        BitmapFont font = new BitmapFont();
        skin = new Skin();
        skin.add("default", font);

        // Create texture for background of buttons
        Pixmap pixmap = new Pixmap(ProjectEvolve.V_WIDTH / 4, ProjectEvolve.V_HEIGHT / 10, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        skin.add("background", new Texture(pixmap));

        // Create button style
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

        menuButton = new TextButton("Back to Menu", skin);
        menuButton.setPosition(ProjectEvolve.V_WIDTH - menuButton.getWidth(), 0);
        stage.addActor(menuButton);

        // Create event listener for menuButton
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
        });


        creatorMatrix = new CreatorMatrix("hexagongrid.png", new Vector2(ProjectEvolve.V_WIDTH / 2 / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / 2 / ProjectEvolve.PPM), "normalmodule.png");
        //dragImage1 = new DraggableImage("normalmodule.png", new Vector2(0, 0), creatorMatrix);
        blueSpawner = new ModuleSpawner("normalmodule.png", new Vector2(0, 0), creatorMatrix);
    }

    private void createPartsList() {

    }

    @Override
    public void show() {

    }

    private void handleInput(float dt) {
        //System.out.println("handling input");
        if (Gdx.input.isTouched()) {
            //System.out.println("left click detected");
//            if(blueSpawner.TrySpawn(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM))) {
//                // Create Blue Module
//                System.out.println("Spawning module");
//                dragImages[blueSpawners] = new DraggableImage("normalmodule.png", new Vector2(0, 0), creatorMatrix);
//            }
            blueSpawner.pickUp(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            blueSpawner.transientMove(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            int i;
            for(i = 0; i < blueSpawners; i++) {
                dragImages[i].pickUp(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
                dragImages[i].transientMove(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            }
            //dragImage1.pickUp(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            //dragImage1.transientMove(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            //System.out.println(Gdx.input.getX());
        } else {
            //System.out.println("Unclicked");
            //dragImage1.place(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            Vector2 blueSpawnLocation = blueSpawner.place(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            if(blueSpawnLocation.x > 0) {
                System.out.println("Spawning new Module");
                dragImages[blueSpawners] = new DraggableImage("normalmodule.png", blueSpawnLocation, creatorMatrix);
                blueSpawners++;
            }

            int i;
            for(i = 0; i < blueSpawners; i++) {
                dragImages[i].place(new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM));
            }
        }
    }

    private void update(float dt) {
        handleInput(dt);
        //System.out.println(blueSpawner.pickedUp);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //game.batch.setProjectionMatrix(gameCam.combined);
        stage.act();
        stage.draw();
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();



        blueSpawner.render(game.batch);
        // Render blue modules
        int i;
        for(i = 0; i < blueSpawners; i++) {
            dragImages[i].render(game.batch);
        }

        //dragImage1.render(game.batch);
        creatorMatrix.render(game.batch);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        //menuButton.setPosition(Gdx.graphics.getWidth() - menuButton.getWidth(), 0);
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
        stage.dispose();
        skin.dispose();
    }
}
