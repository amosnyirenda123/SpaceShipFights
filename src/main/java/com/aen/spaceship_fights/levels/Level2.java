/*
package com.aen.spaceship_fights.levels;
*/
/***Les ennemies poursuivent le joueur*//*


import com.almasb.fxgl.entity.Entity;

*/
/**
 * Obtenir la position du joueur.
 *
 * Calculer une direction vers le joueur.
 *
 * Déplacer l’ennemi progressivement dans cette direction à chaque frame.*//*


public class Level2 extends GameLevel{
    //cree un ennemi qui suit le joueur avec une vitesse
     @Override
    public void init(){

         // Créer un ennemi qui suit le joueur
         Entity player = spawnPlayer()
                 getGameWorld().getSingleton(EntityType.PLAYER); // On récupère l'entité joueur
         var enemy = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
         enemy.addComponent(new FollowPlayerComponent(player, 50));
    }

    Entity enemy = spawnEnemy(x, y);
    enemy.addComponent(new FollowPlayerComponent(getPlayer(), 100));






}


*/
