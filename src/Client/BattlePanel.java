/**
 * Combat panel that handles turn-based battles between the player and enemy.
 * Provides stats display, controls for attacking, using spells, escaping, and exiting.
 */
package Client;

/**
 * @author TomaszKaczmarek
 */

import models.Hero;
import models.Enemy;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import server.WeaponGenerator;
import server.SaveManager;

public class BattlePanel extends JPanel implements KeyListener {

    private final GUI gui;
    private Hero player;
    private Enemy enemy;

    // Player components
    private JLabel playerNameLabel, playerDmgLabel, playerXpLabel, playerWeaponLabel, playerLvlLabel;
    private JProgressBar playerHpBar;

    // Enemy components
    private JLabel enemyNameLabel;
    private JProgressBar enemyHpBar;
    private JLabel imageLabel;
    private JTextArea enemyStatsArea;

    // Log panel
    private JTextArea combatLog;

    // Buttons
    private JButton attackButton, spellButton, fleeButton, exitButton;
    private boolean spellUsed = false;

    public BattlePanel(GUI gui) {
        this.gui = gui;

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);

        setLayout(new BorderLayout(10, 10));

        // === BOTTOM PANEL ===
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2));
        bottomPanel.setPreferredSize(new Dimension(0, 150));

        // --- PLAYER PANEL ---
        JPanel playerPanel = new JPanel(new BorderLayout(5, 5));
        JPanel playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        Font fontLarge = new Font("Arial", Font.BOLD, 16);
        playerNameLabel = new JLabel();
        playerLvlLabel = new JLabel();
        playerDmgLabel = new JLabel();
        playerXpLabel = new JLabel();
        playerWeaponLabel = new JLabel();
        for (JLabel lbl : new JLabel[]{playerNameLabel, playerLvlLabel, playerDmgLabel, playerXpLabel, playerWeaponLabel}) {
            lbl.setFont(fontLarge);
            playerInfoPanel.add(lbl);
        }

        playerHpBar = new JProgressBar();
        playerHpBar.setForeground(Color.red);
        playerHpBar.setStringPainted(true);

        JPanel hpBarWrapper = new JPanel(new BorderLayout());
        hpBarWrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        hpBarWrapper.add(playerHpBar, BorderLayout.CENTER);

        playerPanel.add(playerInfoPanel, BorderLayout.CENTER);
        playerPanel.add(hpBarWrapper, BorderLayout.SOUTH);

        // --- BUTTON PANEL ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        attackButton = new JButton("Attack");
        spellButton = new JButton("Cast Spell");
        fleeButton = new JButton("Flee");
        exitButton = new JButton("Exit");

        for (JButton btn : new JButton[]{attackButton, spellButton, fleeButton, exitButton}) {
            btn.setBackground(Color.WHITE);
            btn.setFont(new Font("Arial", Font.BOLD, 14));
            buttonPanel.add(btn);
        }

        bottomPanel.add(playerPanel);
        bottomPanel.add(buttonPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        // === CENTER PANEL ===
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));

        // --- LEFT PANEL ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        enemyNameLabel = new JLabel("Enemy: ");
        enemyStatsArea = new JTextArea(5, 15);
        enemyStatsArea.setEditable(false);
        leftPanel.add(enemyNameLabel);
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(new JScrollPane(enemyStatsArea));

        // --- MIDDLE PANEL ---
        JPanel middlePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        enemyHpBar = new JProgressBar(0, 100);
        enemyHpBar.setForeground(Color.red);
        enemyHpBar.setStringPainted(true);
        middlePanel.add(imageLabel, BorderLayout.CENTER);
        middlePanel.add(enemyHpBar, BorderLayout.SOUTH);

        // --- RIGHT PANEL ---
        combatLog = new JTextArea();
        combatLog.setEditable(false);
        combatLog.setLineWrap(true);
        combatLog.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(combatLog);
        logScroll.setPreferredSize(new Dimension(150, 0));

        centerPanel.add(leftPanel, BorderLayout.WEST);
        centerPanel.add(middlePanel, BorderLayout.CENTER);
        centerPanel.add(logScroll, BorderLayout.EAST);
        add(centerPanel, BorderLayout.CENTER);

        // Action listeners
        attackButton.addActionListener(e -> performAttack());
        spellButton.addActionListener(e -> castSpell());
        fleeButton.addActionListener(e -> {
            combatLog.append("You flee from battle!\n");
            gui.showPanel("Map");
        });
        exitButton.addActionListener(e -> {
            combatLog.append("You leave the battlefield!\n");
            gui.showPanel("Map");
        });
    }

    public void startCombat(Hero player, Enemy enemy) {
        this.player = player;
        this.enemy = enemy;
        this.spellUsed = false;
        combatLog.setText("");
        player.restoreHealth();

        attackButton.setEnabled(true);
        spellButton.setEnabled(true);
        fleeButton.setEnabled(true);
        exitButton.setEnabled(false);

        playerNameLabel.setText("Player: " + player.getName());
        playerLvlLabel.setText("Level: " + player.getLevel());
        playerXpLabel.setText("XP: " + player.getExp());
        playerDmgLabel.setText("DMG: " + player.getDmg());
        playerWeaponLabel.setText("Weapon: " + (player.getWeapon()!= null ? player.getWeapon().getName()+ " / Damage: " + player.getWeapon().getDamage() : "Fists"));

        updatePlayerHpBar();
        enemyNameLabel.setText("Enemy: " + enemy.getName());
        enemyHpBar.setMaximum(enemy.getMaxHp());
        enemyHpBar.setValue(enemy.getHp());
        enemyStatsArea.setText("HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + "\nAttack: " + enemy.getDmg() + "\n");

        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(enemy.getImageName()));
            int width = imageLabel.getWidth();
            int height = imageLabel.getHeight();
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            e.printStackTrace();
            imageLabel.setIcon(null);
        }

        SwingUtilities.invokeLater(() -> this.requestFocusInWindow());
    }

    private void castSpell() {
        if (spellUsed) {
            combatLog.append("Spell already used!\n");
            return;
        }
        int healing = player.getMaxHp() / 2;
        player.setHp(Math.min(player.getHp() + healing, player.getMaxHp()));
        combatLog.append("You cast a healing spell. Restored: " + healing + " HP.\n");
        spellUsed = true;

        playerHpBar.setValue(player.getHp());
        updatePlayerHpBar();
        playerNameLabel.setText("Player: " + player.getName());
        this.requestFocusInWindow();
    }

    private void performAttack() {
        int playerDmg = player.attack();
        enemy.receiveDamage(playerDmg);
        combatLog.append("You dealt " + playerDmg + " damage\n");

        enemyHpBar.setValue(enemy.getHp());
        enemyStatsArea.setText("HP: " + enemy.getHp() + "/" + enemy.getMaxHp() + "\nAttack: " + enemy.getDmg() + "\n");

        if (!enemy.isAlive()) {
            combatLog.append("Enemy defeated!\n");
            player.addExp(enemy.getExpReward());
            gui.getClient().sendToServer("LOOT");
            SaveManager.saveGame(player);
            attackButton.setEnabled(false);
            spellButton.setEnabled(false);
            fleeButton.setEnabled(false);
            exitButton.setEnabled(true);
        } else {
            enemyAttack();
        }
        updatePlayerHpBar();
        this.requestFocusInWindow();
    }

    private void enemyAttack() {
        int dmg = enemy.attack();
        player.receiveDamage(dmg);
        combatLog.append(enemy.getName() + " hits you for " + dmg + " damage.\n");
        updatePlayerHpBar();

        if (!player.isAlive()) {
            combatLog.append("You have been defeated!\n");
            attackButton.setEnabled(false);
            spellButton.setEnabled(false);
            fleeButton.setEnabled(false);
            exitButton.setEnabled(true);
        }
    }

    private void updatePlayerHpBar() {
        playerHpBar.setMaximum(player.getMaxHp());
        playerHpBar.setValue(player.getHp());
        playerHpBar.setString(player.getHp() + "/" + player.getMaxHp());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            gui.showPauseMenu();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
