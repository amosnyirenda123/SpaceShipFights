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
                    enemy.addComponent(new SquarePathComponent());
                }, Duration.seconds(t));
                t += 0.25;
            }
        }
    }

    private static class SquarePathComponent extends Component {
        private double time = 0;

        @Override
        public void onUpdate(double tpf) {
            time += tpf * 50;

            double phase = (time % 400);
            double x = 0, y = 0;

            if (phase < 100)
                x = phase;
            else if (phase < 200)
                x = 100;
            else if (phase < 300)
                x = 300 - phase;
            else
                x = 0;

            if (phase < 100)
                y = 0;
            else if (phase < 200)
                y = phase - 100;
            else if (phase < 300)
                y = 100;
            else
                y = 400 - phase;

            entity.setPosition(getAppWidth() / 2 + x, getAppHeight() / 2 + y);
        }
    }
}
