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
import com.nic.projectevolve.ProjectEvolve;
import com.nic.projectevolve.uiComponents.CustomTextButton;

/**
 * Created by nic on 9/25/16.
 *
 * This class handles the main menu of the game. This will have options to go to the game, the
 * the character modifier, and any other areas including options and any micro-transaction menus.
 */
public class MenuScreen implements Screen {
    private Skin skin;
    private Stage stage;
    private ProjectEvolve game;

    private FitViewport gamePort;

    private Vector2 inputScaleAdjuster;

    private CustomTextButton playButton;
    private CustomTextButton creatorButton;
    private CustomTextButton infoButton;

    private OrthographicCamera gameCam;
    private Sprite backgroundSprite;

    public MenuScreen(ProjectEvolve game) {
        create();
        this.game = game;
        ProjectEvolve.setScl((int) (Gdx.graphics.getHeight() / 8));

        //OrthographicCamera gameCam;
        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);

        //gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
        inputScaleAdjuster = new Vector2(1, 1);
        gamePort.update(ProjectEvolve.V_WIDTH, ProjectEvolve.V_HEIGHT);
        inputScaleAdjuster.x = (float) ProjectEvolve.V_WIDTH / (ProjectEvolve.V_WIDTH - gamePort.getLeftGutterWidth() - gamePort.getRightGutterWidth());
        inputScaleAdjuster.y = (float) ProjectEvolve.V_HEIGHT / (ProjectEvolve.V_HEIGHT - gamePort.getTopGutterHeight() - gamePort.getBottomGutterHeight());

        backgroundSprite = new Sprite(new Texture(ProjectEvolve.BACKGROUND_TEXTURE_NAMES[1]));
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
        skin.getFont("default").getData().setScale(2.0f);

        CustomTextButton playButtonB;
        playButtonB = new CustomTextButton("Select Level", skin);
        playButtonB.setSize(new Vector2(Gdx.graphics.getWidth() / 3, Gdx.graphics.getHeight() / 5));
        playButtonB.setPosition(new Vector2(Gdx.graphics.getWidth() / 2 - playButtonB.getWidth() / 2, Gdx.graphics.getHeight() / 2 + 1 * playButtonB.getHeight() / 2));
        playButtonB.setActive(false);
        playButtonB.setColor(new Color(0.75f, 0.25f, 0.25f, 0.5f));

        playButton = new CustomTextButton("Select Level", skin);
        playButton.setSize(new Vector2(Gdx.graphics.getWidth() / 3 - 10, Gdx.graphics.getHeight() / 5 - 10));
        playButton.setPosition(new Vector2(Gdx.graphics.getWidth() / 2 - playButtonB.getWidth() / 2 + 5, Gdx.graphics.getHeight() / 2 + 1 * playButtonB.getHeight() / 2 + 5));


        creatorButton = new CustomTextButton("Cell Arranger", skin);
        creatorButton.setSize(new Vector2(Gdx.graphics.getWidth() / 3 - 10, Gdx.graphics.getHeight() / 5 - 10));
        creatorButton.setPosition(new Vector2(Gdx.graphics.getWidth() / 2 - playButtonB.getWidth() / 2 + 5, Gdx.graphics.getHeight() / 2 - 1 * playButtonB.getHeight() / 2 + 5));

        infoButton = new CustomTextButton("About Game", skin);
        infoButton.setSize(new Vector2(Gdx.graphics.getWidth() / 3 - 10, Gdx.graphics.getHeight() / 5 - 10));
        infoButton.setPosition(new Vector2(Gdx.graphics.getWidth() / 2 - playButtonB.getWidth() / 2 + 5, Gdx.graphics.getHeight() / 2 - 3 * playButtonB.getHeight() / 2 + 5));
    }

    @Override
    public void show() {

    }

    public void update() {
        Vector2 touchLocation = new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, Gdx.graphics.getHeight() / ProjectEvolve.PPM - Gdx.input.getY() / ProjectEvolve.PPM);
        //Vector2 touchLocation = new Vector2((Gdx.input.getX() - gamePort.getLeftGutterWidth()) * inputScaleAdjuster.x / ProjectEvolve.PPM, (-Gdx.input.getY() + gamePort.getTopGutterHeight() + gamePort.getScreenHeight()) * inputScaleAdjuster.y / ProjectEvolve.PPM);
        playButton.update(touchLocation);
        //System.out.println(touchLocation.x);
        //System.out.println(touchLocation.y);
        creatorButton.update(touchLocation);
        infoButton.update(touchLocation);
        if(Gdx.input.justTouched() && playButton.click(touchLocation)) {
            game.setScreen(new LevelSelectScreen(game));
            dispose();
        }
        if(Gdx.input.justTouched() && creatorButton.click(touchLocation)) {
            game.setScreen(new CreatorScreen(game));
            dispose();
        }
        if(Gdx.input.justTouched() && infoButton.click(touchLocation)) {
            game.setScreen(new InfoScreen(game));
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
        playButton.drawStage();
        creatorButton.drawStage();
        infoButton.drawStage();
    }

    @Override
    public void resize(int width, int height) {
        //stage.setViewport(width, height, false);
        gamePort.update(width, height);
        inputScaleAdjuster.x = (float) ProjectEvolve.V_WIDTH / (width - gamePort.getLeftGutterWidth() - gamePort.getRightGutterWidth());
        inputScaleAdjuster.y = (float) ProjectEvolve.V_HEIGHT / (height - gamePort.getTopGutterHeight() - gamePort.getBottomGutterHeight());
        //gameCam.update();

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
