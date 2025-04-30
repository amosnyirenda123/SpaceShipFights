package com.aen.spaceship_fights.levels;

public class LevelData {
    public int numberOfEnemies;
    public boolean enemiesFollowPlayer;
    public boolean enemiesShoot;
    public boolean hasBoss;
    public boolean hasObstacles;
    public boolean spawnPowerUps;

    public LevelData(int numberOfEnemies, boolean enemiesFollowPlayer, boolean enemiesShoot,
                     boolean hasBoss, boolean hasObstacles, boolean spawnPowerUps) {
        this.numberOfEnemies = numberOfEnemies;
        this.enemiesFollowPlayer = enemiesFollowPlayer;
        this.enemiesShoot = enemiesShoot;
        this.hasBoss = hasBoss;
        this.hasObstacles = hasObstacles;
        this.spawnPowerUps = spawnPowerUps;
    }
}

