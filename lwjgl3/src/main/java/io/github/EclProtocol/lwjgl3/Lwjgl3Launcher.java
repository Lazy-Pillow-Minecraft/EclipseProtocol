package io.github.EclProtocol.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowConfiguration;
import io.github.EclProtocol.Main;
import io.github.EclProtocol.debug.RegistryViewer;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {

    private static final boolean DEBUG = false;

    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;

        Main.setCallback(() -> {
            if (DEBUG) {
                System.out.println("Callback received! Creating secondary window...");
                new Thread(() -> {
                    try {
                        Lwjgl3WindowConfiguration config = new Lwjgl3WindowConfiguration();
                        config.setTitle("Registry Viewer");
                        config.setWindowedMode(400, 600);
                        config.setWindowPosition(1000, 100);
                        if (Gdx.app instanceof Lwjgl3Application) {
                            ((Lwjgl3Application) Gdx.app).newWindow(new RegistryViewer(), config);
                            System.out.println("Window created successfully via Callback!");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            } else {
                System.out.println("Registry Viewer is disabled.");
            }
        });

        createApplication();
    }

    private static void createApplication() {
        new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("EclipseProtocol");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(640, 480);
        return configuration;
    }
}
