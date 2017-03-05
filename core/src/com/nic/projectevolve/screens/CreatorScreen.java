package com.nic.projectevolve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.uiComponents.CreatorMatrix;
import com.nic.projectevolve.uiComponents.CustomTextButton;
import com.nic.projectevolve.uiComponents.DraggableImage;
import com.nic.projectevolve.uiComponents.ModuleDeleter;
import com.nic.projectevolve.uiComponents.ModuleSpawner;
import com.nic.projectevolve.uiComponents.UpgradeButton;

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

    private UpgradeButton[] upgradeButtons;
    //private int numUpgradeButtons;

    private TextButton resourceButton;

    private CustomTextButton menuButton;
    private Vector2 inputScaleAdjuster;

    private Sprite backgroundSprite;

    public CreatorScreen(ProjectEvolve game) {
        //Gdx.input.setInputProcessor(stage);
        modules = new DraggableImage[19];
        numModules = 0;

        numSpawners = 3;
        spawners = new ModuleSpawner[numSpawners];

        int numUpgradeButtons = 3;
        upgradeButtons = new UpgradeButton[numUpgradeButtons];

        inputScaleAdjuster = new Vector2(1, 1);

        this.game = game;

        batch = new SpriteBatch();

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);

        gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        // Set up UI (buttons, and other clickables)
        // Moved since this needs to reference gamePort for sizing
        createButtons();

        backgroundSprite = new Sprite(new Texture(ProjectEvolve.BACKGROUND_TEXTURE_NAMES[2]));
        backgroundSprite.setPosition(0, 0);
        backgroundSprite.setSize(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM);
    }

    private void createButtons() {
        stage = new Stage();
        //Gdx.input.setInputProcessor(stage);

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
        Color uncheckedColor = new Color(0.25f, 0.25f, 0.25f, 0.7f);
        Color checkedColor = new Color(0.25f, 0.25f, 0.25f, 0.95f);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", uncheckedColor);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("background", checkedColor);
        textButtonStyle.over = skin.newDrawable("background", checkedColor);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);

//        TextButton menuButton = new TextButton("Back to Menu", skin);
//        menuButton.setSize(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 4);
//        menuButton.setPosition(ProjectEvolve.V_WIDTH - menuButton.getWidth(), 0);
//        stage.addActor(menuButton);
//
//        // Create event listener for menuButton
//        menuButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                game.setScreen(new MenuScreen(game));
//                dispose();
//            }
//        });
        menuButton = new CustomTextButton("Return to Menu", skin);
        menuButton.setSize(new Vector2(Gdx.graphics.getWidth() * 3 / 11, Gdx.graphics.getHeight() / 4));
        menuButton.setPosition(new Vector2(Gdx.graphics.getWidth() * 8 / 11, 0));

        creatorMatrix = new CreatorMatrix("hexagon_grid.png", new Vector2(ProjectEvolve.V_WIDTH / 2 / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / 2 / ProjectEvolve.PPM));

        // Load module locations from gameState
        for(int i = 0; i < ProjectEvolve.NUM_MODULES; i++) {
            if(ProjectEvolve.state.getModule(i) != -1) {
                modules[numModules] = new DraggableImage(ProjectEvolve.state.getModule(i), creatorMatrix.getSocketLocation(i), creatorMatrix);
                modules[numModules].place(numModules, i);
                numModules++;
            }
        }

        resourceButton = new TextButton("Genetic Material: "+GameState.geneticMaterial, skin);
        resourceButton.setSize(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 10);
        resourceButton.setPosition(Gdx.graphics.getWidth() / 2 - resourceButton.getWidth() / 2, Gdx.graphics.getHeight() - resourceButton.getHeight());
        stage.addActor(resourceButton);

        spawners[0] = new ModuleSpawner(0, new Vector2(0, Gdx.graphics.getHeight() * 3 / 4), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
        spawners[1] = new ModuleSpawner(1, new Vector2(0, Gdx.graphics.getHeight() * 2 / 4), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
        spawners[2] = new ModuleSpawner(2, new Vector2(0, Gdx.graphics.getHeight() / 4), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
        moduleDeleter = new ModuleDeleter("delete.png", new Vector2(0, 0), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
        upgradeButtons[0] = new UpgradeButton(0, new Vector2(Gdx.graphics.getWidth() * 8 / 11, Gdx.graphics.getHeight() * 3 / 4), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
        upgradeButtons[1] = new UpgradeButton(1, new Vector2(Gdx.graphics.getWidth() * 8 / 11, Gdx.graphics.getHeight() * 2 / 4), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
        upgradeButtons[2] = new UpgradeButton(2, new Vector2(Gdx.graphics.getWidth() * 8 / 11, Gdx.graphics.getHeight() / 4), new Vector2(3 * Gdx.graphics.getWidth() / 11, Gdx.graphics.getHeight() / 4));
    }

    @Override
    public void show() {

    }

    private void handleInput() {
        // Pre-calculate the touch location so it only needs to be calculated once
        //Vector2 touchLocation = new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, -Gdx.input.getY() / ProjectEvolve.PPM + ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM);
        Vector2 touchLocation = new Vector2((Gdx.input.getX() - gamePort.getLeftGutterWidth()) * inputScaleAdjuster.x / ProjectEvolve.PPM, (-Gdx.input.getY() + gamePort.getTopGutterHeight() + gamePort.getScreenHeight()) * inputScaleAdjuster.y / ProjectEvolve.PPM);

        // Update UpgradeButtons here since they need touchLocation
        upgradeButtons[0].update(touchLocation);
        upgradeButtons[1].update(touchLocation);
        upgradeButtons[2].update(touchLocation);
        spawners[0].update(touchLocation);
        spawners[1].update(touchLocation);
        spawners[2].update(touchLocation);
        menuButton.update(touchLocation);
        moduleDeleter.update(touchLocation);

        // Handle if the screen was just touched (mostly pick up operations)
        if(Gdx.input.justTouched()) {
            // Check if menuButton was clicked
            if(menuButton.click(touchLocation)) {
                game.setScreen(new MenuScreen(game));
                dispose();
            }
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

            upgradeButtons[0].click(touchLocation);
            upgradeButtons[1].click(touchLocation);
            upgradeButtons[2].click(touchLocation);
            System.out.print("Genetic Material: ");
            System.out.println(GameState.geneticMaterial);
            System.out.print("Module Levels: ");
            System.out.println(GameState.moduleLevels[0]);
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
        resourceButton.setText("Genetic Material: "+GameState.geneticMaterial);
    }

    @Override
    public void render(float delta) {
        update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        backgroundSprite.draw(game.batch);
        game.batch.end();

        // Draw custom button
        menuButton.drawStage();

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
        //int i;
        for(int i = 0; i < numModules; i++) {
            modules[i].render(game.batch);
        }

        creatorMatrix.render(game.batch);

        upgradeButtons[0].render(game.batch);
        upgradeButtons[1].render(game.batch);
        upgradeButtons[2].render(game.batch);

        //dragImage1.render(game.batch);

        game.batch.end();

        upgradeButtons[0].drawStage();
        upgradeButtons[1].drawStage();
        upgradeButtons[2].drawStage();
        spawners[0].drawStage();
        spawners[1].drawStage();
        spawners[2].drawStage();
        moduleDeleter.drawStage();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        //menuButton.setPosition(Gdx.graphics.getWidth() - menuButton.getWidth(), 0);
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
        stage.dispose();
        skin.dispose();
        ProjectEvolve.state.saveStateToFile();
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
