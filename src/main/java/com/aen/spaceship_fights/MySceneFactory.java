package com.aen.spaceship_fights;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;

public class MySceneFactory extends SceneFactory {
    @Override
    public FXGLMenu newMainMenu() {
        return new MainMenu();
    }

    @Override
    public FXGLMenu newGameMenu() {
        return new MainMenu();
    }
}
