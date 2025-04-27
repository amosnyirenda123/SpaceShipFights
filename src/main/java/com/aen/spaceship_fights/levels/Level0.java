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

        // Définir la cible (par exemple, un point fixe sur l'écran)
        double targetX = getAppWidth() / 2.0-100;
        double targetY = getAppHeight() / 2.0-100;

        // Créer un premier ennemi
        var entity = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
        entity.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

        // Ajouter le comportement de suivre la cible fixe
        entity.addComponent(new FollowTargetComponent(targetX, targetY, 50));

        // Créer des ennemis supplémentaires avec un délai
        for (int i = 0; i < Config.ENEMIES_PER_LEVEL1; i++) {
            getGameTimer().runOnceAfter(() -> {
                Entity enemy = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                enemy.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

               /* // Ajouter un comportement pour suivre la cible fixe
                enemy.addComponent(new FollowTargetComponent(targetX, targetY, random(50, 100)));
*/
                // Optionnel : Suivre un ennemi existant également
              /*  getGameWorld().getRandom(EntityType.ENEMY).ifPresent(e -> {
                    enemy.addComponent(new FollowComponent(e, random(100, 400), random(15, 25), random(30, 40)));
                });*/

            }, Duration.seconds(t));

            t += 0.25;
        }
    }



    private static class FollowTargetComponent extends Component {
        private Point2D target;
        private double speed;
        private double t = 0;

        public FollowTargetComponent(double targetX, double targetY, double speed) {
            this.target = new Point2D(targetX, targetY);
            this.speed = speed;
        }

        @Override
        public void onUpdate(double tpf) {

            // Calculer la direction vers la cible
            Point2D direction = target.subtract(entity.getPosition()).normalize();
            entity.setPosition(curveFunction());
            // Déplacer l'entité vers la cible
            entity.translate(direction.getX() * speed * tpf, direction.getY() * speed * tpf);

            t += tpf;
        }

        public void setTarget(double x, double y) {
            this.target = new Point2D(x, y);
        }
        private Point2D curveFunction() {

            double x = 100 * sin(t);
            double y = 100 * cos(t);

            return new Point2D(x, -y).multiply(28);
        }
    }


}