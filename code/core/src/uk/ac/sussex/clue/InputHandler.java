package uk.ac.sussex.clue;

import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Our inputhandler, which sends input to the MainController if need be
 */
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
}
