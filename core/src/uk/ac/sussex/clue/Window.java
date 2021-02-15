package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Window extends ScreenAdapter {
    // the game's main controller
    MainController mc;
    // spritebatch we're using to draw on the screen
    SpriteBatch batch;
    // Our font
    BitmapFont font;

    public Window(MainController mc) {
        this.mc = mc;
    }

    @Override
    public void show () {
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

}
