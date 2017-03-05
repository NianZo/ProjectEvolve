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
 * Created by nic on 12/21/16.
 *
 * This object allows for spawning modules. Basically, this is a modified DraggableImage which maintains
 * the same position, but allows modules to be spawned at the drop location. The spawning code is handled
 * in CreatorScreen, though. CreatorScreen simply tests to see if the pickedUp flag is true to see
 * if a module can be spawned at the drop location
 */
public class ModuleSpawner {
    private Sprite sprite;
    private Sprite transientSprite;
    private Vector2 position;
    private Vector2 transientPosition;
    private boolean pickedUp;

    private short moduleType;

    private Stage stage;
    private Skin skin;
    private TextButton description;
    private Vector2 size;

    private int type;

    private Sprite backgroundSprite;

    public ModuleSpawner(int type, Vector2 position, Vector2 size) {
        moduleType = (short) type;
        Texture texture = new Texture(ProjectEvolve.MODULE_TEXTURE_NAMES[type]);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, size.y / 2 / ProjectEvolve.PPM, size.y / 2 / ProjectEvolve.PPM);

        this.type = type;

        transientSprite = new Sprite(texture);
        transientSprite.setBounds(0, 0, 50 / ProjectEvolve.PPM, 50 / ProjectEvolve.PPM);
        transientSprite.setAlpha(0.5f);

        this.position = position;
        this.size = size;
        transientPosition = position;

        pickedUp = false;

        createTextField();
        description = new TextButton(ProjectEvolve.SPAWNER_DESCRIPTIONS[type]+GameState.moduleLevels[type], skin);
        description.setPosition(position.x, position.y);
        description.setSize(size.x - size.y, size.y);
        stage.addActor(description);
        sprite.setPosition((position.x + size.y / 2) / ProjectEvolve.PPM - sprite.getHeight() / 2 + description.getWidth() / ProjectEvolve.PPM, (position.y + size.y / 2) / ProjectEvolve.PPM - sprite.getHeight() / 2);

        backgroundSprite = new Sprite(new Texture(ProjectEvolve.BACKGROUND_TEXTURE_NAMES[3]));
        backgroundSprite.setPosition(position.x / ProjectEvolve.PPM, position.y / ProjectEvolve.PPM);
        backgroundSprite.setSize(size.x / ProjectEvolve.PPM, size.y / ProjectEvolve.PPM);
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
        Color clear = new Color(1, 1, 1, 0);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("background", clear);
        textButtonStyle.down = skin.newDrawable("background", Color.DARK_GRAY);
        textButtonStyle.checked = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.over = skin.newDrawable("background", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");
        skin.add("default", textButtonStyle);
    }

    public void tryPickUp(Vector2 touchPosition) {
        if(!pickedUp) {
            if (touchPosition.x > position.x / ProjectEvolve.PPM &&
                    touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                    touchPosition.y > position.y / ProjectEvolve.PPM &&
                    touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM) {
                pickedUp = true;
            }
        }
    }

    public void transientMove(Vector2 position) {
        if (pickedUp) {
            transientPosition = new Vector2(position.x - 24 / ProjectEvolve.PPM, position.y - 24 / ProjectEvolve.PPM);
            transientSprite.setPosition(transientPosition.x, transientPosition.y);
        }
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setPickedUp(boolean condition) {
        pickedUp = condition;
    }

    public void update(Vector2 touchPosition) {
        sprite.setAlpha(.75f);
        backgroundSprite.setAlpha(.5f);
        if (touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM) {

            // Highlight the thing
            //System.out.print(type);
            //System.out.println(" highlighted");
            sprite.setAlpha(1.0f);
            backgroundSprite.setAlpha(.75f);
        }
        description.setText(ProjectEvolve.SPAWNER_DESCRIPTIONS[type]+GameState.moduleLevels[type]);
    }

    public void render(SpriteBatch batch) {
        backgroundSprite.draw(batch);
        sprite.draw(batch);
        if (pickedUp) {
            transientSprite.draw(batch);
        }
    }

    public void drawStage() {
        stage.draw();
    }

    public int getType() {
        return moduleType;
    }
}
