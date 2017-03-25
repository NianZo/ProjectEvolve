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
 * Created by nic on 2/5/17.
 *
 * A screen class holding a background sprite with information about the game on it.
 */
public class InfoScreen implements Screen {

    private ProjectEvolve game;

    private OrthographicCamera gameCam;
    private FitViewport gamePort;
    private Stage stage;

    private Sprite backgroundSprite;

    private CustomTextButton menuButton;

    public InfoScreen(ProjectEvolve inputGame) {
        game = inputGame;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);
        //gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
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

        // Create button with default TextButtonStyle (3rd parameter can be used to specify a style other than "default")
        menuButton = new CustomTextButton("Menu", skin);
        menuButton.setSize(new Vector2(Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 6));
        menuButton.setPosition(new Vector2(Gdx.graphics.getWidth() - menuButton.getWidth(), 0));
        //stage.addActor(menuButton);

//        menuButton.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                game.setScreen(new MenuScreen(game));
//                dispose();
//            }
//        });

        backgroundSprite = new Sprite(new Texture(ProjectEvolve.BACKGROUND_TEXTURE_NAMES[4]));
        backgroundSprite.setPosition(-ProjectEvolve.V_WIDTH / ProjectEvolve.PPM / 2, -ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM / 2);
        backgroundSprite.setSize(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM);
    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Vector2 touchLocation = new Vector2(Gdx.input.getX() / ProjectEvolve.PPM, Gdx.graphics.getHeight() / ProjectEvolve.PPM - Gdx.input.getY() / ProjectEvolve.PPM);
        menuButton.update(touchLocation);
        if(Gdx.input.justTouched() && menuButton.click(touchLocation)) {
            game.setScreen(new MenuScreen(game));
            dispose();
        }
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(gameCam.combined);
        game.batch.begin();
        backgroundSprite.draw(game.batch);
        game.batch.end();

        stage.act();
        stage.draw();
        menuButton.drawStage();
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
        //inputScaleAdjuster.x = (float) ProjectEvolve.V_WIDTH / (width - gamePort.getLeftGutterWidth() - gamePort.getRightGutterWidth());
        //inputScaleAdjuster.y = (float) ProjectEvolve.V_HEIGHT / (height - gamePort.getTopGutterHeight() - gamePort.getBottomGutterHeight());
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
