package com.nic.projectevolve.uiComponents;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.nic.projectevolve.ProjectEvolve;

/**
 * Created by nic on 2/5/17.
 *
 * This is a wrapper for the TextButton class. The built in TextButton class doesn't scale inputs
 * properly when the window is resized, so this rescales the input to fix those issues.
 */
public class CustomTextButton {

    private TextButton textButton;
    private Vector2 position;
    private Vector2 size;

    private Stage stage;

    private boolean active;

    public CustomTextButton(String text, Skin skin) {
        textButton = new TextButton(text, skin);
        stage = new Stage();
        stage.addActor(textButton);
        active = true;
    }

    public void setPosition(Vector2 position) {
        textButton.setPosition(position.x, position.y);
        this.position = position;
    }

    public void setSize(Vector2 size) {
        textButton.setSize(size.x, size.y);
        this.size = size;
    }

    public float getWidth() {
        return size.x;
    }

    public float getHeight() {
        return size.y;
    }

    public void update(Vector2 touchPosition) {
        textButton.setChecked(false);
        if (active && touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM) {

            // Highlight the thing
            //System.out.print(type);
            //System.out.println(" highlighted");
            //icon.setAlpha(1.0f);
            textButton.setChecked(true);
        }
    }

    public void drawStage() {
        stage.draw();
    }

    public boolean click(Vector2 touchPosition) {
        return (active && touchPosition.x > position.x / ProjectEvolve.PPM &&
                touchPosition.x < (position.x + size.x) / ProjectEvolve.PPM &&
                touchPosition.y > position.y / ProjectEvolve.PPM &&
                touchPosition.y < (position.y + size.y) / ProjectEvolve.PPM);
    }

    public void setActive(boolean val) {
        active = val;
    }

    public void setColor(Color color) {
        textButton.setColor(color);
    }
}
