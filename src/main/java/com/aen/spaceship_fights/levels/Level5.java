package com.aen.spaceship_fights.levels;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.ENEMIES_PER_ROW;
import static com.aen.spaceship_fights.Config.ENEMY_ROWS;
import static com.almasb.fxgl.dsl.FXGL.*;

public class Level5 extends GameLevel{
    @Override
    public void init() {
        double t = 0;
        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {
                getGameTimer().runOnceAfter(() -> {
                    Entity enemy = spawnEnemy(getAppWidth() / 10.0, getAppHeight() / 10.0);
                    enemy.addComponent(new EllipseComponent());
                }, Duration.seconds(t));
                t += 0.25;
            }
        }
    }

    private static class EllipseComponent extends Component {
        private double angle = 0;

        @Override
        public void onUpdate(double tpf) {
            angle += tpf * 2;

            double x = Math.cos(angle) * 400;
            double y = Math.min(Math.sin(angle) * 100 + angle * 10, 200);

            entity.setPosition(getAppWidth() / 1.5 + x, getAppHeight() / 1.5 + y);
        }
    }

}
