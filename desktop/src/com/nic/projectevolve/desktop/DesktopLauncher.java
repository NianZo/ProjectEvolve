package com.nic.projectevolve.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.nic.projectevolve.ProjectEvolve;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = ProjectEvolve.V_WIDTH;
		config.height = ProjectEvolve.V_HEIGHT;
		config.title = ProjectEvolve.TITLE;
		new LwjglApplication(new ProjectEvolve(), config);
	}
}
