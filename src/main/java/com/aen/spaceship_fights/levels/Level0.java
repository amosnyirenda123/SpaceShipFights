package com.aen.spaceship_fights.levels;

import com.aen.spaceship_fights.Config;
import com.aen.spaceship_fights.EntityType;
import com.almasb.fxgl.dsl.components.FollowComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.core.math.FXGLMath.cos;
import static com.almasb.fxgl.core.math.FXGLMath.sin;
import static com.almasb.fxgl.dsl.FXGL.*;
import static com.almasb.fxgl.dsl.FXGL.random;

public class Level0 extends GameLevel {

    @Override
    public void init() {
        double t = 0;

        // Créer un premier ennemi
        var entity = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
        entity.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

        // Créer des ennemis supplémentaires avec un délai
        for (int i = 0; i < Config.ENEMIES_PER_LEVEL1; i++) {
            getGameTimer().runOnceAfter(() -> {
                Entity enemy = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                enemy.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

                // Optionnel : Suivre un ennemi existant également
              /*  getGameWorld().getRandom(EntityType.ENEMY).ifPresent(e -> {
                    enemy.addComponent(new FollowComponent(e, random(100, 400), random(15, 25), random(30, 40)));
                });*/

            }, Duration.seconds(t));

            t += 0.25;
        }
    }

}