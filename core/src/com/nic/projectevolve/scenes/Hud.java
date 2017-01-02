package com.nic.projectevolve.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;

import java.util.Locale;

/**
 * Created by nic on 8/4/16.
 */
public class Hud {
    // TODO this class needs a lot of refactoring and cleaning
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    //private float timeCount;
    private static Integer score;

    Label countDownLabel;
    static Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;
    Label marioLabel;

    public Hud(SpriteBatch sb) {
        worldTimer = 300;
        //timeCount = 0;
        score = 0;

        viewport = new FitViewport(ProjectEvolve.V_WIDTH, ProjectEvolve.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countDownLabel = new Label(String.format(Locale.US, "%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format(Locale.US, "%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label("MARIO", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        stage.addActor(table);

    }

    public void update(int playerEnergy) {
        scoreLabel.setText(String.format(Locale.US, "%06d", GameState.geneticMaterial));
        countDownLabel.setText(String.format(Locale.US, "%06d", playerEnergy));
    }

//    public static void addScore(int newScore) {
//        score += newScore;
//    }
}
