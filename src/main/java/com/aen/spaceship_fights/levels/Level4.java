package com.aen.spaceship_fights.levels;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.ENEMIES_PER_ROW;
import static com.aen.spaceship_fights.Config.ENEMY_ROWS;
import static com.almasb.fxgl.dsl.FXGL.*;

public class Level4 extends GameLevel{
    @Override
    public void init() {
        double t = 0;
        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                getGameTimer().runOnceAfter(() -> {
                    Entity enemy = spawnEnemy(0, 0);
                    enemy.addComponent(new DiagonalWaveComponent());
                }, Duration.seconds(t));
                t += 0.25;
            }
        }
    }

    private static class DiagonalWaveComponent extends Component {
        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            t += tpf;
            double x = t * 100;
            double y = Math.sin(t * 3) * 100 + t * 100;
            entity.setPosition(x, y);
        }
    }

}
