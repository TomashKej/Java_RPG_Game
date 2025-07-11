/**
 * Enemy class representing hostile entities in the game.
 * Extends Character and includes reward experience and associated image.
 */
package models;

/**
 * @author TomaszKaczmarek
 */

import java.io.Serializable;

public class Enemy extends Character implements Serializable {
    private int expReward;
    private String imageName; // image file name associated with this enemy

    public Enemy() {
        super("?", 0, 0);
    }

    public Enemy(String name, int hp, int dmg, int expReward, String imageName) {
        super(name, hp, dmg);
        this.expReward = expReward;
        this.imageName = imageName;
    }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public int getExpReward() { return expReward; }
    public void setExpReward(int expReward) { this.expReward = expReward; }

    /**
     * Applies incoming damage to the enemy.
     * @param dmg amount of damage taken
     */
    public void takeDamage(int dmg) {
        this.hp -= dmg;
    }

    @Override
    public String toString() {
        return name + " [HP: " + hp + ", ATK: " + dmg + "]";
    }

    @Override
    public int attack() {
        return dmg;
    }
}
