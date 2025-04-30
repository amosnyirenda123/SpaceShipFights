
package com.aen.spaceship_fights.levels;

public class Level9 {
}


/*on a un ennemi principal qui suit un mouvement aléatoire et plusieurs ennemis qui apparaissent avec des intervalles. Il n'y a pas encore d'interaction avec le joueur, juste des ennemis qui se déplacent.*//*

class Level1 extends GameLevel {

    @Override
    public void init() {
        double t = 0;

        // Créer un ennemi central qui suit un chemin sinusoïdal
        var entity = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
        entity.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));

        // Créer des ennemis supplémentaires avec des mouvements aléatoires
        for (int i = 0; i < 5; i++) {
            getGameTimer().runOnceAfter(() -> {
                Entity enemy = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                enemy.addComponent(new RandomMoveComponent(new Rectangle2D(0, 0, getAppWidth(), getAppHeight() / 2.0), 50));
            }, Duration.seconds(t));
            t += 0.25;
        }
    }
}

 */
/* on a des ennemis qui suivent un point fixe. L'idée est d'ajouter une complexité où chaque ennemi suit la même trajectoire ou un chemin pré-déterminé.

  *//*

class Level2 extends GameLevel {

    @Override
    public void init() {
        double t = 0;

        // Créer un ennemi qui suit un point cible
        double targetX = getAppWidth() / 2.0 - 100;
        double targetY = getAppHeight() / 2.0 - 100;
        var entity = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
        entity.addComponent(new FollowTargetComponent(targetX, targetY, 50));

        // Créer des ennemis supplémentaires
        for (int i = 0; i < 5; i++) {
            getGameTimer().runOnceAfter(() -> {
                Entity enemy = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                enemy.addComponent(new FollowTargetComponent(targetX, targetY, random(50, 100))); // Suivre un point cible
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
            entity.translate(direction.getX() * speed * tpf, direction.getY() * speed * tpf);
            t += tpf;
        }
    }
}

*/
/*les ennemis suivent activement le joueur, ce qui rend le jeu plus dynamique.*//*

class Level3 extends GameLevel {

    @Override
    public void init() {
        double t = 0;

        // Créer un ennemi qui suit le joueur
        Entity player = getGameWorld().getSingleton(EntityType.PLAYER); // On récupère l'entité joueur
        var enemy = spawnEnemy(getAppWidth() / 2.0, getAppHeight() / 2.0 - 100);
        enemy.addComponent(new FollowPlayerComponent(player, 50));

        // Créer des ennemis supplémentaires qui suivent également le joueur
        for (int i = 0; i < 5; i++) {
            getGameTimer().runOnceAfter(() -> {
                Entity newEnemy = spawnEnemy(random(50, getAppWidth() - 100), random(50, getAppHeight() / 2.0 - 100));
                newEnemy.addComponent(new FollowPlayerComponent(player, random(50, 100)));
            }, Duration.seconds(t));
            t += 0.25;
        }
    }

    private static class FollowPlayerComponent extends Component {
        private Entity target;
        private double speed;

        public FollowPlayerComponent(Entity target, double speed) {
            this.target = target;
            this.speed = speed;
        }

        @Override
        public void onUpdate(double tpf) {
            // Calculer la direction vers le joueur
            Point2D direction = target.getPosition().subtract(entity.getPosition()).normalize();
            entity.translate(direction.getX() * speed * tpf, direction.getY() * speed * tpf);
        }
    }
}*/
