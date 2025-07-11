package Client;

/**
 * @author TomaszKaczmarek
 */

import models.Weapon;
import models.Hero;
import models.Enemy;
import tools.Audio;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import server.SaveManager;

/**
 * Main graphical interface class for the RPG Client.
 * Manages the game's main window, views (Menu, Map, Fight), and GUI-based interactions.
 */
public class GUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel cardPanel;
    private BattlePanel fightPanel;
    private Client client;

    public static final String MENU = "Menu";
    public static final String MAP = "Map";
    public static final String FIGHT = "Fight";

    /**
     * Constructs the main GUI window.
     * @param client the client instance associated with the player
     */
    public GUI(Client client) {
        super("RPG Client");
        this.client = client;
        client.getPlayer();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // setExtendedState(JFrame.MAXIMIZED_BOTH); // Uncomment to start maximized
        setSize(1200, 1000);
        setLocationRelativeTo(null);  // Center the window on screen
        setLayout(new BorderLayout());

        // Start background music on game launch
        Audio.play("/resources/soundtrack/ambient/ambientGra.wav");

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(new MenuPanel(this), MENU);
        cardPanel.add(new MapPanel(this, client.getPlayer()), MAP);
        fightPanel = new BattlePanel(this);
        cardPanel.add(fightPanel, FIGHT);

        add(cardPanel, BorderLayout.CENTER);
    }

    public Client getClient() {
        return client;
    }

    public Hero getPlayer() {
        return client.getPlayer();
    }

    public void setPlayer(Hero player) {
        client.setPlayer(player);
    }

    /**
     * Switches the view to a specific panel.
     * @param panelName panel identifier to show (Menu, Map, Fight)
     */
    public void showPanel(String panelName) {
        cardLayout.show(cardPanel, panelName);
        if (panelName.equals(MAP)) {
            MapPanel mapPanel = (MapPanel) cardPanel.getComponent(1);
            mapPanel.requestFocusInWindow();
        }
    }

    /**
     * Displays the fight panel and starts a battle between the player and an enemy.
     * @param player the player character
     * @param enemy the enemy
     */
    public void showFight(Hero player, Enemy enemy) {
        fightPanel.startCombat(client.getPlayer(), enemy);
        showPanel(FIGHT);
    }

    /**
     * Displays a loot dialog when a new weapon is acquired.
     * @param weapon the weapon obtained
     */
    public void showLootDialog(Weapon weapon) {
        String message = "You acquired a new weapon: " + weapon.getName()
                + " (damage: " + weapon.getDamage() + ")\n"
                + "Would you like to equip it?";
        String[] options = {"Equip", "Discard"};
        int choice = JOptionPane.showOptionDialog(null, message, "Loot Acquired!",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
                options, options[0]);
        if (choice == 0) {
            client.getPlayer().equipWeapon(weapon);
            JOptionPane.showMessageDialog(null, "New weapon equipped: " + weapon.getName());
        } else {
            JOptionPane.showMessageDialog(null, "Weapon was discarded.");
        }
    }

    /**
     * Displays the pause menu with options: continue, save & exit, settings.
     */
    public void showPauseMenu() {
        Object[] options = {"Continue", "Save & Exit", "Settings"};
        int choice = JOptionPane.showOptionDialog(
                null,
                "What would you like to do?",
                "Game Paused",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
        if (choice == 1) {
            SaveManager.saveGame(getPlayer());
            try {
                getClient().sendToServer("END_SESSION");
                getClient().endConection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.exit(0);
        } else if (choice == 2) {
            String[] settingOptions = {"Music: " + (Audio.status() ? "Disable" : "Enable")};
            int settingChoice = JOptionPane.showOptionDialog(
                    null,
                    "Settings:",
                    "Settings",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    settingOptions,
                    settingOptions[0]
            );
            if (settingChoice == 0) {
                if (Audio.status()) {
                    Audio.disable();
                } else {
                    Audio.enable();
                }
            }
        } else {
            // If continue or window is closed, resume the game
        }
    }
}
