package com.aen.spaceship_fights.levels;

import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.ENEMIES_PER_ROW;
import static com.aen.spaceship_fights.Config.ENEMY_ROWS;
import static com.almasb.fxgl.core.math.FXGLMath.cos;
import static com.almasb.fxgl.core.math.FXGLMath.sin;
import static com.almasb.fxgl.dsl.FXGL.*;
import static java.lang.Math.pow;

public class Level0 extends GameLevel {

    @Override
    public void init() {
        double t = 0;

        for (int y = 0; y < ENEMY_ROWS; y++) {
            for (int x = 0; x < ENEMIES_PER_ROW; x++) {

                getGameTimer().runOnceAfter(() -> {

                    Entity enemy = spawnEnemy(50, 50);

                    enemy.addComponent(new MoveComponent());

                }, Duration.seconds(t));

                t += 0.25;
            }
        }

    }

    @Override
    public void playInCutscene(Runnable onFinished) {

        showStoryPane();

        Text text = getUIFactoryService().newText("DEFENSE SYSTEM: ALERT! ALERT! ALERT!", Color.WHITE, 24.0);
        text.setWrappingWidth(getAppWidth() - 50);

        updateStoryText(text);

        runOnce(() -> {
            text.setText("DEFENSE SYSTEM: Loading defense system........");
        }, Duration.seconds(3));

        runOnce(this::placeBoss, Duration.seconds(6));

        runOnce(() -> {
            hideStoryPane();
            onFinished.run();

        }, Duration.seconds(26));
    }



    private void placeBoss() {
        Texture boss = texture("instructor_2.png");
        boss.setOpacity(0);
        boss.setTranslateX(getAppWidth() / 2.0 - boss.getWidth() / 2.0);
        boss.setTranslateY(0);

        var view = new GameView(boss, 4000);

        getGameScene().addGameView(view);

        animationBuilder()
                .duration(Duration.seconds(2))
                .fadeIn(boss)
                .buildAndPlay();

        updateAlienStoryText("HQ: Listen up, fighter. Our base is under attack, and we don’t have time for hesitation.");



        runOnce(() -> {
            updateAlienStoryText("HQ: Let’s see if you’re as good as they say. Am putting you on trial.");
        }, Duration.seconds(10));

        runOnce(() -> {
            updateAlienStoryText("HQ: Your orders are simple: survive, adapt, and eliminate the enemy. Now move out");
        }, Duration.seconds(10));


        runOnce(() -> {
            updateAlienStoryText("HQ: You want to be a warrior? Then fight like one. Pick up that weapon and get to work!");
        }, Duration.seconds(12));



        runOnce(() -> {
            animationBuilder()
                    .onFinished(() -> getGameScene().removeGameView(view))
                    .duration(Duration.seconds(2))
                    .fadeOut(boss)
                    .buildAndPlay();
        }, Duration.seconds(13.5));

    }

    private static class MoveComponent extends Component {

        private double t = 0;

        @Override
        public void onUpdate(double tpf) {
            entity.setPosition(curveFunction().add(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100));

            t += tpf;
        }

        private Point2D curveFunction() {

            double x = 16 * pow(sin(t), 3);
            double y = 13 * cos(t) - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t);



            return new Point2D(x, -y).multiply(28);
        }
    }
}
