/**
 * Weapon generator class that randomly selects a weapon based on enemy experience reward.
 * Categorizes weapons by rarity and scales drop chance accordingly.
 */
package server;

/**
 * @author TomaszKaczmarek
 */

import models.Weapon;
import models.Enemy;
import java.util.*;

public class WeaponGenerator {
    private static final List<Weapon> weapons = new ArrayList<>();

    static {
        // List of available weapons
        weapons.add(new Weapon("Stick", 2, "common"));
        weapons.add(new Weapon("Rusty Sword", 4, "common"));
        weapons.add(new Weapon("Spiked Club", 5, "common"));
        weapons.add(new Weapon("Worn Bow", 5, "common"));
        weapons.add(new Weapon("Old Sword", 7, "common"));
        weapons.add(new Weapon("Solid Bludgeon", 10, "rare"));
        weapons.add(new Weapon("Sharp Sword", 11, "rare"));
        weapons.add(new Weapon("Hunter Bow", 11, "rare"));
        weapons.add(new Weapon("Battle Staff", 12, "rare"));
        weapons.add(new Weapon("Squire Sword", 17, "unique"));
        weapons.add(new Weapon("Elite Crossbow", 18, "unique"));
        weapons.add(new Weapon("Captain's Mace", 16, "unique"));
        weapons.add(new Weapon("Mage Staff", 19, "unique"));
        weapons.add(new Weapon("Executioner's Verdict", 30, "epic"));
        weapons.add(new Weapon("Bone Crusher", 32, "epic"));
        weapons.add(new Weapon("AK-001 Crossbow", 33, "epic"));
        weapons.add(new Weapon("Storm Staff", 34, "epic"));
        weapons.add(new Weapon("Super Sharp Sword", 50, "legendary"));
        weapons.add(new Weapon("Something You Don't Want to Get Hit With", 51, "legendary"));
        weapons.add(new Weapon("Robin Hood's Bow", 52, "legendary"));
        weapons.add(new Weapon("Flamethrower", 55, "legendary"));
    }

    /**
     * Randomly generates a weapon based on enemy's experience reward.
     * Higher experience means better chances for rare items.
     * @param enemy the defeated enemy
     * @return randomly selected weapon
     */
    public static Weapon generateWeapon(Enemy enemy) {
        int exp = enemy.getExpReward();
        Random rand = new Random();
        int chance = rand.nextInt(100);

        if (exp > 150 && chance > 90) {
            return getWeaponByRarity("legendary");
        } else if (exp > 90 && chance > 75) {
            return getWeaponByRarity("epic");
        } else if (exp > 60 && chance > 60) {
            return getWeaponByRarity("unique");
        } else if (exp > 40 && chance > 40) {
            return getWeaponByRarity("rare");
        } else {
            return getWeaponByRarity("common");
        }
    }

    /**
     * Selects a random weapon of the specified rarity.
     * @param rarity the weapon rarity to filter by
     * @return a random matching weapon, or fallback if none found
     */
    public static Weapon getWeaponByRarity(String rarity) {
        List<Weapon> matching = new ArrayList<>();
        for (Weapon b : weapons) {
            if (b.getRarity().equalsIgnoreCase(rarity)) {
                matching.add(b);
            }
        }

        if (matching.isEmpty()) {
            return weapons.get(0); // fallback default
        }
        return matching.get(new Random().nextInt(matching.size()));
    }
}
