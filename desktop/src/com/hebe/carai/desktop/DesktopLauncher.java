package com.hebe.carai.desktop;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hebe.carai.CarAI;

public class DesktopLauncher {

	public static int WIDTH = 1920;
	public static int HEIGHT = 1080;

	public static void main(String[] arg) {
		Toolkit tk = Toolkit.getDefaultToolkit();
	    Dimension d = tk.getScreenSize();
		
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = WIDTH;
		config.height = HEIGHT;
		config.x = d.width / 2 - WIDTH / 2;
		config.y = d.height / 2 - HEIGHT / 2;
		config.fullscreen = true;
		config.vSyncEnabled = false; // Setting to false disables vertical sync
		config.foregroundFPS = 0; // Setting to 0 disables foreground fps throttling
		config.backgroundFPS = 0; // Setting to 0 disables background fps throttling
		LwjglApplicationConfiguration.disableAudio = true; // Disable audio
		new LwjglApplication(new CarAI(), config);
	}
}
