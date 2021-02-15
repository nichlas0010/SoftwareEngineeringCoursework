package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import java.util.ArrayList;

public class Window extends ScreenAdapter {
    MainController mc;
    SpriteBatch batch;
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
