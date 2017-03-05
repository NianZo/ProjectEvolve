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
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 12/22/16.
 *
 * Similar to the Module Spawner, this class represents an icon. This icon can have Modules dropped
 * on it to delete them. This class contains a sprite which holds the texture to be drawn.
 */
public class ModuleDeleter {
    private Sprite sprite;
    private Sprite backgroundSprite;
    private Vector2 position;

    private Stage stage;
    private Skin skin;
    private Vector2 size;

    public ModuleDeleter(String textureName, Vector2 position, Vector2 size) {
        this.position = position;
        this.size = size;

        Texture texture = new Texture(textureName);
        sprite = new Sprite(texture);
        sprite.setBounds(0, 0, size.y / 2 / ProjectEvolve.PPM, size.y / 2 / ProjectEvolve.PPM);


        createTextField();
        TextButton description = new TextButton("Delete Module", skin);
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

    public boolean intersects(Vector2 touchPosition) {
        return (touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM);
    }

    public void update(Vector2 touchPosition) {
        backgroundSprite.setAlpha(.5f);
        sprite.setAlpha(.75f);
        if (touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM) {
            backgroundSprite.setAlpha(.75f);
            sprite.setAlpha(1f);
        }
    }

    public void render(SpriteBatch batch) {
        backgroundSprite.draw(batch);
        sprite.draw(batch);
    }

    public void drawStage() {
        stage.draw();
    }
}
