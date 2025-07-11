/**
 * SaveManager class handles saving and loading the game state to/from a file.
 */
package server;

/**
 * @author TomaszKaczmarek
 */

import models.Hero;
import java.io.*;

public class SaveManager {

    private static final String SAVE_FILE = "save.txt";

    /**
     * Saves the current state of the player to a file.
     * @param player the player character to save
     */
    public static void saveGame(Hero player) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(player);
            System.out.println("Game saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the saved player state from the file.
     * @return the restored player character, or null if loading failed
     */
    public static Hero loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            Hero player = (Hero) in.readObject();
            System.out.println("Looking for save file at: " + new File(SAVE_FILE).getAbsolutePath());

            System.out.println("Game state loaded.");
            return player;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Looking for save file at: " + new File(SAVE_FILE).getAbsolutePath());

            System.out.println("No save found or failed to load.");
            return null;
        }
    }

    /**
     * Checks if a saved game file exists.
     * @return true if the save file exists
     */
    public static boolean doesSaveExist() {
        return new File(SAVE_FILE).exists();
    }

    /**
     * Deletes the existing save file, if present.
     */
    public static void deleteSave() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("Save deleted.");
        }
    }
}
