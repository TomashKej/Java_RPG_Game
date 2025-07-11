/**
 * Abstract base class for all characters in the game.
 * Implements Serializable to allow saving/loading character state.
 * Tracks name, health points, damage, and provides basic character logic.
 */
package models;

/**
 * @author TomaszKaczmarek
 */

import java.io.Serializable;

public abstract class Character implements Serializable {
    protected String name;
    protected int hp;
    private int maxHp;
    protected int dmg;

    public Character(String name, int maxHp, int dmg) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.dmg = dmg;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public int getDmg() { return dmg; }
    public void setDmg(int dmg) { this.dmg = dmg; }

    /**
     * Apply damage to the character, reducing HP but not below zero.
     * @param amount amount of damage received
     */
    public void receiveDamage(int amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    /**
     * Increase the character's maximum HP.
     * @param amount amount to increase max HP
     */
    public void increaseMaxHp(int amount) {
        this.maxHp += amount;
    }

    /**
     * Check if character is still alive.
     * @return true if HP > 0
     */
    public boolean isAlive() {
        return hp > 0;
    }

    /**
     * Perform an attack. Implementation is defined by subclasses.
     * @return damage dealt
     */
    public abstract int attack();
}
