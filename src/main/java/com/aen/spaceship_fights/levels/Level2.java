package com.aen.spaceship_fights.levels;

/***Les ennemies poursuivent le joueur*/


import com.aen.spaceship_fights.Config;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.aen.spaceship_fights.EntityType;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;
import javafx.util.Duration;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

/**
 * Obtenir la position du joueur.
 *
 * Calculer une direction vers le joueur.
 *
 * Déplacer l’ennemi progressivement dans cette direction à chaque frame.*/
public class Level2 extends GameLevel {
    //cree un ennemi qui suit le joueur avec une vitesse
     @Override
    public void init(){
         double t = 0;
         // Créer un ennemi qui suit le joueur
         Entity player = getGameWorld().getSingleton(EntityType.PLAYER); // On récupère l'entité joueur
         var enemy = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
         enemy.addComponent(new FollowPlayerComponent(player, 10));

         // Créer des ennemis supplémentaires avec un délai
         for (int i = 0; i < Config.ENEMIES_PER_LEVEL1; i++){
             getGameTimer().runOnceAfter(() -> {
                 Entity enemy2 = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                 /*enemy.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));
                  */
                 //Ajouter un comportement pour suivre la cible fixe
                 enemy2.addComponent(new Level2.FollowPlayerComponent(player, random(0, 10)));
             }, Duration.seconds(t));
             t += 0.25;
         }
    }

    @Override
    public void onUpdate(double tpf) {
        Entity player = getGameWorld().getSingleton(EntityType.PLAYER);
        //Entity enemy = getGameWorld().getSingleton(EntityType.ENEMY);
        List<Entity> listEnnemeies = FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY);
        for (Entity enemy : listEnnemeies) {
            if (player != null && enemy != null) {
                // Obtenir les positions
                Point2D playerPos = player.getCenter();
                Point2D enemyPos = enemy.getCenter();

                // Calculer la direction normalisée
                Point2D direction = playerPos.subtract(enemyPos).normalize();

                // Vitesse de l'ennemi
                double speed = 10; // pixels par seconde

                // Déplacement progressif à chaque frame
                enemy.translate(direction.multiply(speed * tpf));
            }
        }


    /*Entity enemy = spawnEnemy(x, y);
    enemy.*/
        /*addComponent(new FollowPlayerComponent(player, 100));
         */


    }

    //Comportement à ajouter aux ennemies pour poursuivre le joueur
    public static class FollowPlayerComponent extends Component {

        private Entity player;
        private double speed;

        public FollowPlayerComponent(Entity player, double speed) {
            this.player = player;
            this.speed = speed;
        }

        @Override
        public void onUpdate(double tpf) {
            if (player == null) return;

            // Calcul direction normalisée
            Point2D direction = player.getPosition().subtract(entity.getPosition()).normalize();

            // Déplacement vers le joueur
            entity.translate(direction.getX() * speed * tpf, direction.getY() * speed * tpf);
        }

        /**recuperer l'entite du joueur*//*
    public Entity getPlayer() {
        return getGameWorld().getSingleton("player");
    }*/

    }

}



