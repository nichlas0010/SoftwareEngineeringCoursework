package uk.ac.sussex.clue.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import uk.ac.sussex.clue.MainController;

import java.awt.*;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = true;
		config.height = 1080;
		config.width = 1920;
		config.title = "Clue";
		new LwjglApplication(new MainController(), config);
	}
}
