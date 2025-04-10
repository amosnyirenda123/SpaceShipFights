package com.aen.spaceship_fights;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.dsl.components.*;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitters;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import static com.aen.spaceship_fights.Config.LEVEL_START_DELAY;
import static com.almasb.fxgl.dsl.FXGL.*;

public class GameEntityFactory implements EntityFactory {

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return entityBuilder(data)
                .view(new Rectangle(getAppWidth(), getAppHeight()))
                .build();
    }

    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PLAYER)
                .viewWithBBox("player.png")
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("fff")
    public Entity newFFFPlane(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.FFF)
                .viewWithBBox(texture("fff.png", 130, 130))
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("cc")
    public Entity newCCPlane(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.CC)
                .viewWithBBox(texture("cc.png", 130, 130))
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("fighter")
    public Entity newFighter(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.FIGHTER)
                .viewWithBBox("fighter.png")
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("durrrSpaceShip")
    public Entity newDurrrSpaceShip(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.DURRRSPACESHIP)
                .viewWithBBox("DurrrSpaceShip.png")
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("PPP")
    public Entity newPPP(SpawnData data) {
        return entityBuilder(data)
                .type(EntityType.PPP)
                .viewWithBBox("ppp.png")
                .with(new PlayerComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        var hp = new HealthIntComponent(2);

        var hpView = new ProgressBar(false);
        hpView.setFill(Color.LIGHTGREEN);
        hpView.setMaxValue(2);
        hpView.setWidth(85);
        hpView.setTranslateY(90);
        hpView.currentValueProperty().bind(hp.valueProperty());

        return entityBuilder(data)
                .type(EntityType.ENEMY)
                .viewWithBBox(
                        texture("enemy" + ((int)(Math.random() * 3) + 1) + ".png")
                                .outline(Color.BLACK)
                                .toAnimatedTexture(2, Duration.seconds(2))
                                .loop()
                )
                .view(hpView)
                .with(hp)
                .with(new EnemyComponent(), new EffectComponent())
                .collidable()
                .build();

    }

    class SuperSlowTimeEffect extends Effect {

        public SuperSlowTimeEffect() {
            super(Duration.seconds(0.5));
        }

        @Override
        public void onStart(Entity entity) {
            entity.getComponent(TimeComponent.class).setValue(0.05);
        }

        @Override
        public void onEnd(Entity entity) {
            entity.getComponent(TimeComponent.class).setValue(3.0);
        }
    }

    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        Point2D dir = data.get("dir");
        double speed = 500;
        return entityBuilder(data)
                .type(EntityType.BULLET)
                .viewWithBBox("bullet.png")
                .with(new ProjectileComponent(dir, speed))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    @Spawns("e_bullet")
    public Entity newEBullet(SpawnData data) {
        Point2D dir = data.get("dir");

        var effectComponent = new EffectComponent();
        double speed = 500;
        var e = entityBuilder(data)
                .type(EntityType.ENEMY_BULLET)
                .viewWithBBox("enemy_bullet.png")
                .with(new ProjectileComponent(dir, speed))
                .with(new OffscreenCleanComponent())
                .with(new TimeComponent())
                .with(effectComponent)
                .collidable()
                .build();

        e.setOnActive(()->{
            effectComponent.startEffect(new SuperSlowTimeEffect());
        });
        return e;
    }

    @Spawns("scoreText")
    public Entity newScoreText(SpawnData data) {
        String text = data.get("text");
        var e = entityBuilder(data)
                .view(getUIFactoryService().newText(text, 24))
                .with(new ExpireCleanComponent(Duration.seconds(0.55)).animateOpacity())
                .build();

        animationBuilder()
                .duration(Duration.seconds(0.55))
                .interpolator(Interpolators.EXPONENTIAL.EASE_OUT())
                .translate(e)
                .from(new Point2D(data.getX(), data.getY()))
                .to(new Point2D(e.getX(), e.getY() - 35))
                .buildAndPlay();

        return e;
    }

    @Spawns("explosion")
    public Entity newExplosion(SpawnData data) {
        play("explosion.wav");

        var emitter = ParticleEmitters.newExplosionEmitter(350);
        emitter.setMaxEmissions(1);
        emitter.setSize(2, 10);
        emitter.setStartColor(Color.WHITE);
        emitter.setEndColor(Color.BLUE);
        emitter.setSpawnPointFunction(i -> new Point2D(64, 64));

        return entityBuilder(data)
                .view(texture("explosion.png").toAnimatedTexture(16, Duration.seconds(0.66)).play())
                .with(new ExpireCleanComponent(Duration.seconds(0.66)))
                .with(new ParticleComponent(emitter))
                .build();
    }

    @Spawns("levelInfo")
    public Entity newLevelInfo(SpawnData data) {
        Text levelText = getUIFactoryService().newText("Level " + geti("level"), Color.AQUAMARINE, 44);

        Entity levelInfo = entityBuilder()
                .view(levelText)
                .with(new ExpireCleanComponent(Duration.seconds(LEVEL_START_DELAY)))
                .build();

        animationBuilder()
                .interpolator(Interpolators.BOUNCE.EASE_OUT())
                .duration(Duration.seconds(LEVEL_START_DELAY - 0.1))
                .translate(levelInfo)
                .from(new Point2D(getAppWidth() / 2.0 - levelText.getLayoutBounds().getWidth() / 2, 0))
                .to(new Point2D(getAppWidth() / 2.0 - levelText.getLayoutBounds().getWidth() / 2, getAppHeight() / 2.0))
                .buildAndPlay();

        return levelInfo;
    }

}
