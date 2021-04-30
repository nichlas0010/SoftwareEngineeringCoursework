package uk.ac.sussex.clue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Our maincontroller, is the class that's in charge of always existing. Is the middleground between different windows
 */
public class MainController extends Game {

	// The viewport we're using, see libgdx viewport
	private Viewport viewport;
	// The camera we're using, see libgdx camera
	private Camera camera;
	// The volume of the programme in percent, 50% by default.
	private int volume = 50;
	// The instance of our maincontroller. Any class that needs to access the maincontroller can use MainController.instance to do so.
	public static MainController instance;

	/**
	 * Functions as the constructor for the class, is called by the desktop launcher.
	 */
	@Override
	public void create() {
		MainController.instance = this;
		camera = new OrthographicCamera(1920, 1080);
		viewport = new ExtendViewport(1920, 1080, camera);
		Gdx.input.setInputProcessor(new InputHandler());
		this.setScreen(new Menu());
	}

	/**
	 *
	 * Is called by our Inputhandler whenever a key is pressed.
	 * If the key isn't used by the controller, it's passed to the active screen.
	 *
	 * @param keycode which key is pressed, see {@link com.badlogic.gdx.Input.Buttons}
	 */
	public void keyDown(int keycode){

		// This is where we handle it if the game needs the input.
		boolean isUsed = false;
		if(Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) && keycode == Input.Keys.ENTER) {
			isUsed = true;
			if(Gdx.graphics.isFullscreen()) {
				Gdx.graphics.setWindowedMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			} else {
				Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
			}
		}


		// This is where we send it down the chain if it's not been used.
		if(!isUsed) {
			Window currentWindow = (Window) screen;
			currentWindow.keyDown(keycode);
		}
	}

	/**
	 * Gets the maincontroller's viewport.
	 *
	 * @return The viewport our main controller is set to use
	 * @see com.badlogic.gdx.utils.viewport.Viewport
	 */
	public Viewport getViewport() {
		return viewport;
	}

	/**
	 * Gets the maincontroller's camera.
	 *
	 * @return The camera our main controller is set to use
	 * @see com.badlogic.gdx.graphics.Camera
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * Returns the volume of the programme, as a percentage.
	 * @return the volume of the programme, in percent
	 */
	public int getVolume() {
		return volume;
	}

	/**
	 * Sets the volume of the programme (in percent) to the given value. Clamps the value between 0 and 100.
	 * @param volume the volume the game should be set to
	 */
	public void setVolume(int volume) {
		if(volume < 0) {
			volume = 0;
		} else if(volume > 100) {
			volume = 100;
		}
		this.volume = volume;
	}
}
