/**
 * Enemy generator class that provides random enemy encounters.
 * Ensures fresh enemy instances with default stats to prevent shared state issues.
 */
package server;

/**
 * @author TomaszKaczmarek
 */

import models.Enemy;
import java.util.*;

public class EnemyGenerator {
    private static final List<Enemy> enemies = new ArrayList<>();

    static {
        // List of available enemies
        enemies.add(new Enemy("Rat", 30, 5, 40, "/resources/przeciwnikSzczur.jpg"));
        enemies.add(new Enemy("Young Wolf", 50, 10, 50, "/resources/przeciwnikMlodyWilk.jpg"));
        enemies.add(new Enemy("Wolf", 70, 15, 65, "/resources/przeciwnikWilk.jpg"));
        enemies.add(new Enemy("Goblin", 95, 24, 80, "/resources/przeciwnikOrk.jpg"));
        enemies.add(new Enemy("Orc", 120, 33, 95, "/resources/przeciwnikOrk.jpg"));
        enemies.add(new Enemy("Orc Warrior", 150, 40, 110, "/resources/przeciwnikOrkWojownik.jpg"));
        enemies.add(new Enemy("Orc Shaman", 185, 50, 130, "/resources/przeciwnikOrkSzaman.jpg"));
        enemies.add(new Enemy("Troll", 200, 65, 160, "/resources/przeciwnikTroll.jpg"));
        enemies.add(new Enemy("Mountain Troll", 250, 76, 200, "/resources/przeciwnikTrollGorski.jpg"));
        enemies.add(new Enemy("Troll Champion", 300, 87, 235, "/resources/przeciwnikTrollChampion.jpg"));
        enemies.add(new Enemy("Green Dragon", 340, 100, 265, "/resources/przeciwnikZielonySmok.jpg"));
        enemies.add(new Enemy("Red Dragon", 370, 125, 300, "/resources/przeciwnikCzerwonySmok.jpg"));
        enemies.add(new Enemy("Black Dragon", 450, 170, 500, "/resources/przeciwnikCzarnySmok.jpg"));
    }

    /**
     * Returns a deep copy of a randomly selected enemy to avoid shared state bugs.
     * @return a new instance of a randomly selected enemy
     */
    public static Enemy getRandomEnemy() {
        Random rand = new Random();
        Enemy template = enemies.get(rand.nextInt(enemies.size()));
        return new Enemy(
                template.getName(),
                template.getHp(),
                template.getDmg(),
                template.getExpReward(),
                template.getImageName()
        );
    }
}
