/**
 * Hero class that represents the player-controlled character.
 * Extends Character and tracks level, experience, and equipped weapon.
 */
package models;

/**
 * @author TomaszKaczmarek
 */

import java.io.Serializable;

public class Hero extends Character implements Serializable {
    private int exp;
    private int level;
    private Weapon weapon;

    public Hero(String name) {
        super(name, 100, 10);
        this.exp = 0;
        this.level = 1;
        this.weapon = null;
    }

    // Getters and setters
    public int getExp() { return exp; }
    public int getLevel() { return level; }
    public Weapon getWeapon() { return weapon; }
    public void setExp(int exp) { this.exp = exp; }
    public void setLevel(int level) { this.level = level; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }

    /**
     * Fully restores the hero's HP to the maximum.
     */
    public void restoreHealth() {
        setHp(getMaxHp());
    }

    /**
     * Adds experience points and checks for level up.
     * Levels up when experience reaches threshold.
     */
    public void addExp(int amount) {
        this.exp += amount;
        while (exp >= 100 * level) {
            exp -= 100 * level;
            level++;
            increaseMaxHp(20);
            restoreHealth();
            dmg += 5;
            System.out.println(name + " leveled up to level " + level + "!");
        }
    }

    /**
     * Equips a new weapon and updates damage.
     * Base damage is 10 plus the weapon's damage.
     */
    public void equipWeapon(Weapon weapon) {
        this.weapon = weapon;
        this.dmg = 10 + (weapon != null ? weapon.getDamage(): 0);
    }

    @Override
    public int attack() {
        return dmg;
    }

    @Override
    public String toString() {
        return name + " [HP: " + hp + ", ATK: " + dmg + ", EXP: " + exp + ", LEVEL: "
                + level + ", Weapon: " + (weapon != null ? weapon.getName(): "None") + "]";
    }
}
