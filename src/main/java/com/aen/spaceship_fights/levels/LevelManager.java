package com.aen.spaceship_fights.levels;

import java.util.List;
import java.util.ArrayList;
public class LevelManager {

    private final List<LevelData> levels = new ArrayList<>();
    private int currentLevelIndex = 0;

    public LevelManager() {
        initLevels();
    }

    private void initLevels() {
        levels.add(new LevelData(5, false, false, false, false, false));  // Niveau 1
        levels.add(new LevelData(10, true, false, false, false, true));   // Niveau 2
        levels.add(new LevelData(15, true, true, false, false, true));    // Niveau 3
        levels.add(new LevelData(1, false, true, true, false, false));    // Niveau 4 (boss)
        levels.add(new LevelData(20, true, true, false, true, true));     // Niveau 5
        levels.add(new LevelData(25, false, false, false, true, false));  // Niveau 6
        levels.add(new LevelData(30, true, true, false, false, true));    // Niveau 7
        levels.add(new LevelData(1, true, true, true, true, true));       // Niveau 8 (boss final)
    }

    public LevelData getCurrentLevel() {
        return levels.get(currentLevelIndex);
    }

    public boolean hasNextLevel() {
        return currentLevelIndex < levels.size() - 1;
    }

    public void nextLevel() {
        if (hasNextLevel()) {
            currentLevelIndex++;
        }
    }

    public int getCurrentLevelNumber() {
        return currentLevelIndex + 1;
    }

    public void reset() {
        currentLevelIndex = 0;
    }
}
