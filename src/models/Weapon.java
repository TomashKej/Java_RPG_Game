/**
 * Represents a weapon item that a player can equip.
 * Includes weapon name, damage value, and rarity tier.
 */
package models;

/**
 * @author TomaszKaczmarek
 */

import java.io.Serializable;

public class Weapon implements Serializable {
    private String name;      // weapon name
    private int damage;       // weapon damage value
    private String rarity;    // weapon rarity (e.g., common, rare, epic)

    public Weapon(String name, int damage, String rarity) {
        this.name = name;
        this.damage = damage;
        this.rarity = rarity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }

    @Override
    public String toString() {
        return name + " [+" + damage + " DMG, " + rarity + "]";
    }
}
