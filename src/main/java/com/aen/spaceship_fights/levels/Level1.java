package com.aen.spaceship_fights.levels;

import com.aen.spaceship_fights.EntityType;
import com.almasb.fxgl.dsl.components.FollowComponent;
import com.almasb.fxgl.dsl.components.RandomMoveComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.ENEMIES_PER_ROW;
import static com.aen.spaceship_fights.Config.ENEMY_ROWS;
import static com.almasb.fxgl.core.math.FXGLMath.cos;
import static com.almasb.fxgl.core.math.FXGLMath.sin;
import static com.almasb.fxgl.dsl.FXGL.*;
import static java.lang.Math.pow;

public class Level1 extends GameLevel {


    @Override
    public void init() {
        double t = 0;
        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                getGameTimer().runOnceAfter(() -> {
                    Entity enemy = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0);
                    enemy.addComponent(new SpiralComponent());
                }, Duration.seconds(t));
                t += 0.25;
            }
        }
    }

    private static class SpiralComponent extends Component {
        private double angle = 0;
        private double radius = 200;

        @Override
        public void onUpdate(double tpf) {
            angle += tpf * 2;
            radius -= tpf * 10;

            double x = Math.cos(angle) * radius;
            double y = Math.sin(angle) * radius;

            entity.setPosition(getAppWidth() / 2 + x, getAppHeight() / 2 + y);
        }
    }
}
