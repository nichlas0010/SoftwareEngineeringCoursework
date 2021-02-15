package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputHandler extends Stage {
    MainController mc;

    public InputHandler(MainController mc) {
        super(mc.getViewport());
        this.mc = mc;
    }

    public boolean keyDown (int keycode) {
        mc.keyDown(keycode);
        return false;
    }

}
