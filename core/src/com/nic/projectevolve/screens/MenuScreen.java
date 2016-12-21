package com.nic.projectevolve.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nic.projectevolve.ProjectEvolve;

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

    private OrthographicCamera gameCam;
    private FitViewport gamePort;

    public MenuScreen(ProjectEvolve game) {
        create();
        this.game = game;

        gameCam = new OrthographicCamera();
        gamePort = new FitViewport(ProjectEvolve.V_WIDTH / ProjectEvolve.PPM, ProjectEvolve.V_HEIGHT / ProjectEvolve.PPM, gameCam);

        //gameCam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);
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

        // Store default libgdx font as "default"
        BitmapFont bfont = new BitmapFont();
        // skipping a scale line because BitmapFont.scale(int) is undefined
        skin.add("default", bfont);

        // Configuring a TextButtonStyle named "default"
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        // Create button with default TextButtonStyle (3rd parameter can be used to specify a style other than "default")
        TextButton playButton = new TextButton("PLAY", skin);
        playButton.setPosition(Gdx.graphics.getWidth() / 2 - playButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - playButton.getHeight() / 2);
        stage.addActor(playButton);

        TextButton createButton = new TextButton("Creator", skin);
        createButton.setPosition(Gdx.graphics.getWidth() / 2 - playButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 3 *playButton.getHeight() / 2);
        stage.addActor(createButton);

        // Create event handler for playButton
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayScreen(game));
                dispose();
            }
        });

        // Create event handler for createButton
        createButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CreatorScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
        //Table.drawDebug(stage);
    }

    @Override
    public void resize(int width, int height) {
        //stage.setViewport(width, height, false);
        gamePort.update(width, height);
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
