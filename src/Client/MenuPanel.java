/**
 * Main menu panel for the RPG game.
 * Provides buttons to start a new game, load a saved game, adjust settings, or exit.
 * Includes hover effects and a custom background.
 */
package Client;

/**
 * @author TomaszKaczmarek
 */

import models.Hero;
import tools.Audio;
import server.SaveManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class MenuPanel extends JPanel implements ActionListener {

    private JButton newGameButton, continueButton, settingsButton, exitButton;
    private GUI gui;

    private BufferedImage background;

    /**
     * Constructs the main menu with buttons and background.
     * @param gui reference to the main GUI frame
     */
    public MenuPanel(GUI gui) {
        this.gui = gui;

        try {
            background = ImageIO.read(getClass().getResource("/resources/menuBackground.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize buttons
        newGameButton = new JButton("New Game");
        continueButton = new JButton("Continue");
        settingsButton = new JButton("Settings");
        exitButton = new JButton("Exit Game");

        setStyle(newGameButton);
        setStyle(continueButton);
        setStyle(settingsButton);
        setStyle(exitButton);

        addHoverEffect(newGameButton);
        addHoverEffect(continueButton);
        addHoverEffect(settingsButton);
        addHoverEffect(exitButton);

        newGameButton.addActionListener(this);
        continueButton.addActionListener(this);
        settingsButton.addActionListener(this);
        exitButton.addActionListener(this);

        gbc.gridy = 1; add(newGameButton, gbc);
        gbc.gridy = 2; add(continueButton, gbc);
        gbc.gridy = 3; add(settingsButton, gbc);
        gbc.gridy = 4; add(exitButton, gbc);
    }

    /**
     * Applies styling to a JButton used in the menu.
     * @param button button to style
     */
    public void setStyle(JButton button) {
        button.setPreferredSize(new Dimension(250, 70));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 30));
    }

    /**
     * Adds a hover effect to a button: enlarge on hover, shrink on exit.
     * @param button button to enhance
     */
    public void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(32f));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setFont(button.getFont().deriveFont(28f));
            }
        });
    }

    /**
     * Responds to menu button actions: start game, load save, settings, or quit.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newGameButton) {
            String name = JOptionPane.showInputDialog(this, "Enter hero's name:");
            if (name == null) return;

            if (!name.trim().isEmpty()) {
                Hero player = new Hero(name);
                SaveManager.deleteSave();
                gui.getClient().setPlayer(player);
                gui.showPanel(GUI.MAP);
            } else {
                JOptionPane.showMessageDialog(this, "Name cannot be empty");
            }
        }

        if (e.getSource() == continueButton) {
            Hero loaded = SaveManager.loadGame();
            if (loaded != null) {
                gui.setPlayer(loaded);
                gui.showPanel("Map");
            } else {
                JOptionPane.showMessageDialog(this, "No save file found");
            }
        }

        if (e.getSource() == settingsButton) {
            String[] options = {"Music: " + (Audio.status() ? "Disable" : "Enable")};
            JOptionPane.showOptionDialog(this, "Settings", "Settings",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            if (Audio.status()) {
                Audio.disable();
            } else {
                Audio.enable();
            }
        }

        if (e.getSource() == exitButton) {
            try {
                gui.getClient().sendToServer("END_SESSION");
                gui.getClient().endConection();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
            System.exit(0);
        }
    }

    /**
     * Paints the background image for the menu panel.
     * @param g graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
