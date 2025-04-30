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

                    Entity enemy = spawnEnemy(getAppWidth() * Math.random(), 0);

                    enemy.addComponent(new MoveComponent());

                }, Duration.seconds(t));

                t += 0.25;
            }
        }

    }

    private static class MoveComponent extends Component {

        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            entity.setPosition(moveFunction().add(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100));

            t += tpf;
        }

        private Point2D moveFunction() {

            double x = 2*pow(t, 2);
            double y = 13;



            return new Point2D(x, -y).multiply(28);
        }
    }
}
