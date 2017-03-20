package com.nic.projectevolve.scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;

import java.util.Locale;

/**
 * Created by nic on 8/4/16.
 *
 * This class holds logic for the HUD for the PlayScreen. This grabs the game score from GameState,
 * and the player's energy is passed in to the update function so it can be displayed
 */
public class Hud {
    public Stage stage;

    // Declare labels that need to be accessed outside of the constructor
    private Label energyLabel;
    private Label scoreLabel;

    public Hud(SpriteBatch sb) {
        // Create a stage for the HUD to be contained in
        stage = new Stage(new FitViewport(ProjectEvolve.V_WIDTH, ProjectEvolve.V_HEIGHT, new OrthographicCamera()), sb);

        // Create a table to hold the HUD components
        Table table = new Table();

        // Declare labels that only need to be accessed in the constructor
        Label energyTitleLabel;
        Label levelLabel;
        Label levelTitleLabel;
        Label scoreTitleLabel;

        // Initialize all labels
        energyLabel = new Label(String.format(Locale.US, "%03d", 0), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format(Locale.US, "%06d", GameState.geneticMaterial), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        energyTitleLabel = new Label("Energy", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label("1", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelTitleLabel = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreTitleLabel = new Label("Nucleotides", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        // Layout the HUD in the table
        table.top();
        table.setFillParent(true);
        table.add(scoreTitleLabel).expandX().padTop(10);
        table.add(levelTitleLabel).expandX().padTop(10);
        table.add(energyTitleLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(energyLabel).expandX();
        // Add the table to the stage as an actor
        stage.addActor(table);
    }

    public void update(int playerEnergy) {
        // Update the energy meter and the score counter
        scoreLabel.setText(String.format(Locale.US, "%06d", GameState.geneticMaterial));
        energyLabel.setText(String.format(Locale.US, "%06d", playerEnergy));
    }

    public void dispose() {
        stage.dispose();
    }
}
