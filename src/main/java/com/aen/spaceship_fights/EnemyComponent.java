package com.aen.spaceship_fights;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;



import static com.almasb.fxgl.dsl.FXGL.*;

public class EnemyComponent extends Component {
    protected LocalTimer attackTimer;
    protected Duration nextAttack = Duration.seconds(2);

    @Override
    public void onAdded() {
        attackTimer = FXGL.newLocalTimer();
        attackTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (attackTimer.elapsed(nextAttack)) {
            if (FXGLMath.randomBoolean(0.3f)) {
                shoot();
            }
            nextAttack = Duration.seconds(5 * Math.random());
            attackTimer.capture();
        }
    }

    protected void shoot() {
        Point2D to = new Point2D(getAppWidth() , getAppHeight() );

        spawn("e_bullet", new SpawnData(0, 0).put("dir", to));

        play("shoot" + (int)(Math.random() * 4 + 1) + ".wav");
    }

    public void die() {
        spawn("explosion", entity.getCenter());

        entity.removeFromWorld();
//        fire(new GameEvent(GameEvent.ENEMY_KILLED));
    }
}
