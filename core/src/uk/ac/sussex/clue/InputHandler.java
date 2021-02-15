package uk.ac.sussex.clue;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class InputHandler extends Stage {
    // Our main controller.
    private MainController mc;

    /**
     * Calls the superfunction, and sets our mc variable
     * @param mc The game's main controller.
     */
    public InputHandler(MainController mc) {
        super(mc.getViewport());
        this.mc = mc;
    }

    /**
     * Is called when a key on the keyboard is pressed, calls the mc's keyDown function.
     * @param keycode the key which is pressed, see {@link com.badlogic.gdx.Input.Buttons}
     * @return always returns false, return variable is irrelevant.
     * @see uk.ac.sussex.clue.MainController#keyDown(int) 
     */
    public boolean keyDown (int keycode) {
        mc.keyDown(keycode);
        return false;
    }

}
