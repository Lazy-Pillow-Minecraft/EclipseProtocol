package org;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.EclProtocol.Main;

public class DesktopLauncher{
    public static void main (String[] arg) {
        createApplication();
    }

    private static Lwjgl3Application createApplication () {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration () {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("EclipseProtocol");
        configuration.setWindowedMode(800, 600);
        return configuration;
    }
}
