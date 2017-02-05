package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.nic.projectevolve.GameState;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 1/24/17.
 *
 * This class is a wrapper for several UI elements comprising the buttons to upgrade modules using
 * in game currency. These will act as buttons, should highlight when moused over, and should be able
 * to be clicked on and deactivated.
 */
public class UpgradeButton {
    // TODO highly experimental

    private Sprite icon;
    private Texture iconTexture;
    private TextButton description;
    private Sprite background;
    private Vector2 position;
    private Vector2 size;
    private boolean active;

    private Skin skin;
    private Stage stage;

    private int type;

    public UpgradeButton(int type, Vector2 position, Vector2 size) {
        this.type = type;
        iconTexture = new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[type]);
        icon = new Sprite(iconTexture);
        //icon.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        icon.setBounds(0, 0, size.y / 2 / ProjectEvolve.PPM, size.y / 2 / ProjectEvolve.PPM);
        //icon.setSize(size.y / 2 / ProjectEvolve.PPM, size.y / 2 / ProjectEvolve.PPM);
        icon.setPosition((position.x + size.y / 2) / ProjectEvolve.PPM - icon.getHeight() / 2, (position.y + size.y / 2) / ProjectEvolve.PPM - icon.getHeight() / 2);
        //icon.setPosition(0 / ProjectEvolve.PPM, 300 / ProjectEvolve.PPM);

        this.position = position;
        this.size = size;

        createTextField();
        description = new TextButton(ProjectEvolve.UPGRADE_DESCRIPTIONS[type], skin);
        description.setPosition(position.x + size.y, position.y);
        description.setSize(size.x - size.y, size.y);
        stage.addActor(description);
    }

    private void createTextField() {
        stage = new Stage();

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
    }

    public void click(Vector2 touchPosition) {
        if (touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM && active) {

            //pickedUp = true;
            GameState.geneticMaterial -= ProjectEvolve.UPGRADE_COSTS[type][GameState.moduleLevels[type]];
            GameState.moduleLevels[type]++;
        }

    }

    public void render(SpriteBatch batch) {
        icon.draw(batch);
    }

    public void drawStage() {
        stage.draw();
    }

    public void update(Vector2 touchPosition) {
        if (touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM) {

            // Highlight the thing
            System.out.print(type);
            System.out.println(" highlighted");
        }
        active = GameState.geneticMaterial >= ProjectEvolve.UPGRADE_COSTS[type][GameState.moduleLevels[type]];
    }
}
