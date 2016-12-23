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
import com.nic.projectevolve.uiComponents.ModuleDeleter;
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

    private DraggableImage[] modules;
    private int numModules;
    private CreatorMatrix creatorMatrix;
    private ModuleDeleter moduleDeleter;

    private ModuleSpawner[] spawners;
    private int numSpawners;

    public CreatorScreen(ProjectEvolve game) {
        //Gdx.input.setInputProcessor(stage);
        modules = new DraggableImage[19];
        numModules = 0;

        numSpawners = 2;
        spawners = new ModuleSpawner[numSpawners];

        // Set up UI (buttons, and other clickables)
        createButtons();

        this.game = game;

        batch = new SpriteBatch();

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
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

        TextButton menuButton = new TextButton("Back to Menu", skin);
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

        creatorMatrix = new CreatorMatrix("hexagongrid.png", new Vector2(ProjectEvolve.V_WIDTH / 2 / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / 2 / ProjectEvolve.PPM));

        // Load module locations from gameState
        for(int i = 0; i < ProjectEvolve.NUMMODULES; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                modules[numModules] = new DraggableImage(ProjectEvolve.state.getModule(i), creatorMatrix.getSocketLocation(i), creatorMatrix);
                modules[numModules].place(numModules, i);
                numModules++;
            }
        }

        spawners[0] = new ModuleSpawner(0, new Vector2(0, 0));
        spawners[1] = new ModuleSpawner(1, new Vector2(0, 50 / ProjectEvolve.PPM));
        moduleDeleter = new ModuleDeleter("delete.png", new Vector2(0, 100 / ProjectEvolve.PPM));
    }

    @Override
    public void show() {

    }

    private void handleInput() {
        // Pre-calculate the touch location so it only needs to be calculated once
        Vector2 touchLocation = new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM);

        // Handle if the screen was just touched (mostly pick up operations)
        if(Gdx.input.justTouched()) {
            // Check if any spawners need picked up
            for(int i = 0; i < numSpawners; i++) {
                spawners[i].tryPickUp(touchLocation);
            }

            // Check if any modules need picked up
            for(int i = 0; i < numModules; i++) {
                if(modules[i].getSocket() != 0) {
                    modules[i].tryPickUp(touchLocation);
                }
            }
        }

        // Handle if the screen is touched (not necessarily just touched) (mostly transient move operations)
        if (Gdx.input.isTouched()) {
            // Transient move any spawners (transient move checks if picked up)
            for(int i = 0; i < numSpawners; i++) {
                spawners[i].transientMove(touchLocation);
            }

            // Transient move any modules (transient move checks if picked up)
            for(int i = 0; i < numModules; i++) {
                modules[i].transientMove(touchLocation);
            }
        }

        // Handle if there is no click (mostly place operations)
        else {
            // Test for spawners; is the spawner picked up?
            for(int i = 0; i < numSpawners; i++) {
                if (spawners[i].isPickedUp()) {
                    // Has a valid location been picked? (on the matrix)
                    int blueSpawnLocation = creatorMatrix.testDrop(touchLocation);
                    if (blueSpawnLocation > -1) {
                        // Valid location has been chosen
                        // If there is already a module in the socket, then delete it
                        if (creatorMatrix.getModuleIndex(blueSpawnLocation) != -1) {
                            DeleteModule(creatorMatrix.getModuleIndex(blueSpawnLocation));
                            creatorMatrix.removeModule(blueSpawnLocation);
                        }
                        // Test to see if an adjacent socket is occupied
                        if (creatorMatrix.adjacentOccupied(blueSpawnLocation, -1)) {
                            // Create a new module in the chosen socket
                            modules[numModules] = new DraggableImage(spawners[i].getType(), creatorMatrix.getSocketLocation(blueSpawnLocation), creatorMatrix);
                            modules[numModules].place(numModules, blueSpawnLocation);
                            numModules++;
                        }
                    }
                    spawners[i].setPickedUp(false);
                }
            }

            // Test for each of the Modules being moved
            for(int i = 0; i < numModules; i++) {
                // Is the Module picked up?
                if(modules[i].isPickedUp()) {
                    // Has a valid location been picked? (on the matrix)
                    int newSocket = creatorMatrix.testDrop(touchLocation);
                    if(newSocket > -1 && newSocket != modules[i].getSocket()) {
                        // Valid location has been chosen
                        // If there is already a module in the socket, then delete it
                        if(creatorMatrix.getModuleIndex(newSocket) != -1) {
                            DeleteModule(creatorMatrix.getModuleIndex(newSocket));
                            creatorMatrix.removeModule(newSocket);
                        }
                        // Test to see if an adjacent socket is occupied
                        if(creatorMatrix.adjacentOccupied(newSocket, modules[i].getSocket())) {
                            // Move the Module
                            creatorMatrix.removeModule(modules[i].getSocket());
                            modules[i].place(i, newSocket);
                        }
                    } else if(moduleDeleter.intersects(touchLocation)) {
                        // Location of deleter has been chosen; delete the picked up module
                        if(creatorMatrix.adjacentOccupied(-1, modules[i].getSocket())) {
                            DeleteModule(i);
                            creatorMatrix.removeModule(modules[i].getSocket());
                        }
                    }
                    modules[i].setPickedUp(false);
                }
            }
        }
    }

    private void update() {
        handleInput();

    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();

        // Render module spawners
        for(int i = 0; i < numSpawners; i++) {
            spawners[i].render(game.batch);
        }

        // Render Module Deleter
        moduleDeleter.render(game.batch);

        // Render blue modules
        int i;
        for(i = 0; i < numModules; i++) {
            modules[i].render(game.batch);
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

    // Handles everything needed to delete a module that is no longer needed
    public void DeleteModule(int index) {
        // Let DraggableImage dispose of resources it needs to
        modules[index].dispose();

        // Remove reference to deleted module in the module array
        numModules--;
        int i;
        for(i = index; i < numModules; i++) {
            modules[i] = modules[i + 1];
        }
    }
}
