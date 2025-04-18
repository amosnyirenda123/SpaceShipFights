package com.aen.spaceship_fights.levels;

import com.aen.spaceship_fights.EntityType;
import com.almasb.fxgl.dsl.components.FollowComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class Level1 extends GameLevel {


    @Override
    public void init() {
        double t = 0;

        var entity = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
        entity.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

        for (int i = 0; i < 31; i++) {
            getGameTimer().runOnceAfter(() -> {

                Entity enemy = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                enemy.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

                getGameWorld().getRandom(EntityType.ENEMY).ifPresent(e -> {
                    enemy.addComponent(new FollowComponent(e, random(100, 400), random(15, 25), random(30, 40)));
                });

            }, Duration.seconds(t));

            t += 0.25;
        }
    }


}
