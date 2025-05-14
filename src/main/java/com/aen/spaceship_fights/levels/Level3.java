package com.aen.spaceship_fights.levels;


import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.ENEMIES_PER_ROW;
import static com.aen.spaceship_fights.Config.ENEMY_ROWS;
import static com.almasb.fxgl.dsl.FXGL.*;

public class Level3 extends GameLevel {
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
        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            t += tpf;

            double radius = t * 50;
            double angle = t * 2 * Math.PI;

            double x = Math.cos(angle) * radius;
            double y = t * 100;


            entity.setPosition(getAppWidth() / 2.0 + x, y);
        }
    }
}
