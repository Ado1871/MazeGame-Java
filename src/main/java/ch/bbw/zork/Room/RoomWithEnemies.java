package ch.bbw.zork.Room;

import ch.bbw.zork.Enemy;

import java.util.ArrayList;
import java.util.List;

public class RoomWithEnemies extends Room {

    private List<Enemy> enemies;

    public RoomWithEnemies(String description) {
        super(description);
        this.enemies = new ArrayList<>();
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    public void removeEnemy(Enemy enemy) {
        enemies.remove(enemy);
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
