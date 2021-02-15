package uk.ac.sussex.clue;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainController extends Game {

	boolean test = false;
	Viewport viewport;
	Camera camera;
	int volume = 50;
	
	@Override
	public void create() {
		camera = new OrthographicCamera(1920, 1080);
		viewport = new ExtendViewport(1920, 1080, camera);
		Gdx.input.setInputProcessor(new InputHandler(this));
		this.setScreen(new Menu(this));
	}

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

	public Viewport getViewport() {
		return viewport;
	}

	public Camera getCamera() {
		return camera;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}
}
