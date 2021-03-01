package uk.ac.sussex.clue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputHandler extends Stage {

    /**
     * Calls the superfunction with our maincontroller's viewport
     */
    public InputHandler() {
        super(MainController.instance.getViewport());
    }

    /**
     * Is called when a key on the keyboard is pressed, calls the mc's keyDown function.
     * @param keycode the key which is pressed, see {@link com.badlogic.gdx.Input.Buttons}
     * @return always returns false, return variable is irrelevant.
     * @see uk.ac.sussex.clue.MainController#keyDown(int) 
     */
    public boolean keyDown (int keycode) {
        MainController.instance.keyDown(keycode);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        int height = Gdx.graphics.getHeight();
        int width = Gdx.graphics.getWidth();
        System.out.println(screenX);
        System.out.println(width);
        System.out.println(screenY);
        System.out.println(height);
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
