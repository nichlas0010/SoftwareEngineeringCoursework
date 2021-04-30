package uk.ac.sussex.clue.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import uk.ac.sussex.clue.MainController;

import java.awt.*;

/**
 * This class is merely a launcher. We use it to launch the LIBGDX application
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		// Create the application config
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		// Set the properties
		config.fullscreen = true;
		config.height = 1080;
		config.width = 1920;
		config.title = "Clue";
		// Create the application
		new LwjglApplication(new MainController(), config);
	}
}
