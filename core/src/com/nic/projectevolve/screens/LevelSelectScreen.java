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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.uiComponents.CustomTextButton;

/**
 * Created by nic on 2/14/17.
 *
 * Screen to hold the level selection mechanism. Allows player to play any level they have currently
 * unlocked. Unlocked levels are polled from the GameState class.
 */
public class LevelSelectScreen implements Screen{
    private Skin skin;
    private Stage stage;
    private ProjectEvolve game;

    private FitViewport gamePort;

    private Vector2 inputScaleAdjuster;

    private CustomTextButton[] levelButtons;
    private CustomTextButton menuButton;

    private Sprite backgroundSprite;
    private OrthographicCamera gameCam;

    public LevelSelectScreen(ProjectEvolve game) {
        levelButtons = new CustomTextButton[11];
        create();
        this.game = game;

        //OrthographicCamera gameCam;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);

        inputScaleAdjuster = new Vector2(1, 1);

        backgroundSprite = new Sprite(new Texture(ProjectEvolve.BACKGROUND_TEXTURE_NAMES[0]));
        backgroundSprite.setPosition(-ProjectEvolve.V_WIDTH / ProjectEvolve.PPM / 2, -ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM / 2);
        backgroundSprite.setSize(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM);
    }

    public void create() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin();

        // Creating a texture named white and adding it to skin
        Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();

        skin.add("white", new Texture(pixmap));

        // Store default libGdx font as "default"
        BitmapFont bFont = new BitmapFont();
        // skipping a scale line because BitmapFont.scale(int) is undefined
        skin.add("default", bFont);

        // Configuring a TextButtonStyle named "default"
        Color uncheckedColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
        Color checkedColor = new Color(0.5f, 0.5f, 0.5f, 0.75f);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", uncheckedColor);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", checkedColor);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        for(int i = 0; i < 5; i++) {
            levelButtons[i] = new CustomTextButton(ProjectEvolve.LEVEL_SELECT_NAMES[i], skin);
            levelButtons[i].setSize(new Vector2(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 3));
            levelButtons[i].setPosition(new Vector2(Gdx.graphics.getWidth() * i / 5, Gdx.graphics.getHeight() * 2 / 3));
            if(GameState.unlockedLevels[i] == 0) {
                levelButtons[i].setActive(false);
                levelButtons[i].setColor(new Color(0.75f, 0.25f, 0.25f, 0.5f));
            }
        }
        for(int i = 5; i < 10; i++) {
            levelButtons[i] = new CustomTextButton(ProjectEvolve.LEVEL_SELECT_NAMES[i], skin);
            levelButtons[i].setSize(new Vector2(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 3));
            levelButtons[i].setPosition(new Vector2(Gdx.graphics.getWidth() * (i - 5) / 5, Gdx.graphics.getHeight() / 3));
            if(GameState.unlockedLevels[i] == 0) {
                levelButtons[i].setActive(false);
                levelButtons[i].setColor(new Color(0.75f, 0.25f, 0.25f, 0.5f));
            }
        }
        levelButtons[10] = new CustomTextButton(ProjectEvolve.LEVEL_SELECT_NAMES[10], skin);
        levelButtons[10].setSize(new Vector2(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 3));
        levelButtons[10].setPosition(new Vector2(Gdx.graphics.getWidth() * 2 / 5, 0));
        if(GameState.unlockedLevels[10] == 0) {
            levelButtons[10].setActive(false);
            levelButtons[10].setColor(new Color(0.75f, 0.25f, 0.25f, 0.5f));
        }

        menuButton = new CustomTextButton("Menu", skin);
        menuButton.setSize(new Vector2(Gdx.graphics.getWidth() / 5, Gdx.graphics.getHeight() / 6));
        menuButton.setPosition(new Vector2(Gdx.graphics.getWidth() * 4 / 5, 0));

    }

    @Override
    public void show() {

    }

    public void update() {
        Vector2 touchLocation = new Vector2((Gdx.input.getX() - gamePort.getLeftGutterWidth()) * inputScaleAdjuster.x / ProjectEvolve.PPM, (-Gdx.input.getY() + gamePort.getTopGutterHeight() + gamePort.getScreenHeight()) * inputScaleAdjuster.y / ProjectEvolve.PPM);
        for(int i = 0; i < ProjectEvolve.NUM_LEVELS; i++) {
            levelButtons[i].update(touchLocation);
            if (Gdx.input.justTouched() && levelButtons[i].click(touchLocation) && GameState.unlockedLevels[i] == 1) {
                game.setScreen(new PlayScreen(game, i));
                dispose();
            }
        }
        menuButton.update(touchLocation);
        if(Gdx.input.justTouched() && menuButton.click(touchLocation)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
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

        stage.act();
        stage.draw();
        for(int i = 0; i < ProjectEvolve.NUM_LEVELS; i++) {
            levelButtons[i].drawStage();
        }
        menuButton.drawStage();
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
        stage.dispose();
        skin.dispose();
        ProjectEvolve.state.saveStateToFile();
    }
}
