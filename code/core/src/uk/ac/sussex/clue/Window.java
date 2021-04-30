package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * The superclass for all our windows.
 *
 * All windows extend this, and use the font and batch implemented here.
 */
public class Window extends ScreenAdapter {
    // spritebatch we're using to draw on the screen
    SpriteBatch batch;
    // The font all of our text will be drawn with
    BitmapFont font;

    /**
     * Creates the spritebatch and the font for the window to use
     */
    public Window() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/font_large.fnt"), Gdx.files.internal("data/font_large.png"), false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    /**
     * Called whenever a key is pressed. This method just returns, exists for subclasses to override it
     * @param keycode the key that is pressed, @see com.badlogic.gdx.Input#Keys
     */
    public void keyDown(int keycode) {
        return;
    }

    /**
     * Method to add an actor to our inputhandler. Allows us to add anything to be clickable
     * @param a The actor to be added
     */
    public void addActor(Actor a) {
        InputHandler i = (InputHandler) Gdx.input.getInputProcessor();
        i.addActor(a);
    }

    /**
     * Cleanup method, empties the batch
     */
    @Override
    public void hide() {
        batch.dispose();
    }

    /**
     * Gets our font
     * @return the BitmapFont we are drawing with
     */
    public BitmapFont getFont() {
        return font;
    }

}
