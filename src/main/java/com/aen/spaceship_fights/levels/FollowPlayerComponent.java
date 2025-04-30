package com.aen.spaceship_fights.levels;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

import static com.almasb.fxgl.dsl.FXGLForKtKt.getGameWorld;

//Comportement à ajouter aux ennemies pour poursuivre le joueur
public class FollowPlayerComponent extends Component {

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
