package io.github.some_example_name.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

/**
 * Lwjgl3Launcher — Entry point for the desktop (LWJGL3) build.
 *
 * Instantiates {@link GameMaster} via its no-argument constructor so that
 * all non-LibGDX-dependent managers are constructed before the LibGDX
 * application starts. LibGDX then calls {@link GameMaster#create()} once
 * the graphics context is ready, where the remaining setup occurs.
 */
public class Lwjgl3Launcher {

    public static void main(String[] args) {

        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new GameMaster(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("OOP Project");
        configuration.useVsync(true);
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        configuration.setWindowedMode(640, 480);
        configuration.setResizable(true);
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuration;
    }
}
