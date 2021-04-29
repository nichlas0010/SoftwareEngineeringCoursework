package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Window extends ScreenAdapter {
    // spritebatch we're using to draw on the screen
    SpriteBatch batch;
    // The font all of our text will be drawn with
    BitmapFont font;

    public Window() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("data/font_large.fnt"), Gdx.files.internal("data/font_large.png"), false);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
    }

    public void keyDown(int keycode) {
        return;
    }

    public void addActor(Actor a) {
        InputHandler i = (InputHandler) Gdx.input.getInputProcessor();
        i.addActor(a);
    }

    @Override
    public void hide() {
        batch.dispose();
    }

    public BitmapFont getFont() {
        return font;
    }

}
