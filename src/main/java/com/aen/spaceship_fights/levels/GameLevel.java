package com.aen.spaceship_fights.levels;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.*;

public abstract class GameLevel {

    private List<Entity> enemies = new ArrayList<>();

    private Pane storyPane = new Pane();
    private Pane rootPane;


    public GameLevel() {
        var bg = new Rectangle(getAppWidth() - 20, 100, Color.color(0, 0, 0, 0.6));
        bg.setArcWidth(25);
        bg.setArcHeight(25);
        bg.setStroke(Color.color(0.4, 0.0, 0.0, 1));
        bg.setStrokeWidth(3);

        DropShadow softShadow = new DropShadow();
        softShadow.setColor(Color.BLACK);
        softShadow.setRadius(15);
        bg.setEffect(softShadow);

        storyPane.setTranslateX(10);
        storyPane.setTranslateY(25);


        rootPane = new Pane(bg, storyPane);
        rootPane.setTranslateX(10);
        rootPane.setTranslateY(getAppHeight() - 100);
    }

    public abstract void init();

    public void onUpdate(double tpf) {

    }

    public void destroy() {

    }

    public List<Entity> getEnemies() {
        return enemies;
    }

    public void playInCutscene(Runnable onFinished) {
        onFinished.run();
    }

    public void playOutCutscene(Runnable onFinished) {
        onFinished.run();
    }

    protected void showStoryPane() {
        addUINode(rootPane);
    }

    protected void hideStoryPane() {
        removeUINode(rootPane);
    }

    protected void updateStoryText(Node node) {
        storyPane.getChildren().setAll(node);
    }

    protected void updateAlienStoryText(String data) {
        List<Text> texts = new ArrayList<>();
        double bounds = 0;

        List<Character> characters = new ArrayList<>();

        for (char c : data.toCharArray()) {
            characters.add(c);
        }

        for (char c : characters) {
            Text t = new Text(c + "");
            t.setFill(Color.WHITE);
            t.setFont(Font.font(24));
            t.setTranslateX(bounds);

            texts.add(t);

            t.setUserData(new Point2D(t.getTranslateX(), t.getTranslateY()));

            bounds += t.getLayoutBounds().getWidth();
        }

        bounds = 0;


        for (Text t : texts) {
            t.setTranslateX(bounds);

            bounds += t.getLayoutBounds().getWidth();

            Point2D p = (Point2D) t.getUserData();

            animationBuilder()
                    .duration(Duration.seconds(2))
                    .delay(Duration.seconds(1))
                    .fadeIn(t)
                    .translate(t)
                    .from(new Point2D(t.getTranslateX(), t.getTranslateY()))
                    .to(p)
                    .buildAndPlay();
        }

        storyPane.getChildren().setAll(texts);
    }

    protected void addEnemy(Entity entity) {
        enemies.add(entity);
    }

    public boolean isFinished() {
        return enemies.stream().noneMatch(Entity::isActive);
    }

    protected Entity spawnEnemy(double x, double y) {
        Entity enemy = spawn("enemy", x, y);

        addEnemy(enemy);

        animationBuilder()
                .interpolator(Interpolators.ELASTIC.EASE_OUT())
                .duration(Duration.seconds(FXGLMath.random(0.0, 1.0) * 2))
                .scale(enemy)
                .from(new Point2D(0, 0))
                .to(new Point2D(1, 1))
                .buildAndPlay();

        return enemy;
    }
}
