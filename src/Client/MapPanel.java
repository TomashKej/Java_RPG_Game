/**
 * Game map panel that renders the game world and handles player movement.
 * Handles collision detection, map rendering, and enemy/boss encounters.
 */
package Client;

/**
 * @author TomaszKaczmarek
 */

import models.Hero;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.ImageIO;

public class MapPanel extends JPanel implements KeyListener {
    private Hero player;
    private final int mapSize = 10;
    private JLabel[][] mapGrid = new JLabel[mapSize][mapSize];
    private TileType[][] tileTypes = new TileType[mapSize][mapSize];
    private int playerX = 0, playerY = 0;

    private ImageIcon grassIcon, playerIcon,
            stoneIcon, fieldIcon,
            bossIcon, fieldSpecialIcon,
            miniBossIcon, brownRockIcon;

    private final GUI gui;

    private enum TileType {
        GRASS, FIELD, STONE, PLAYER, BOSS, MINIBOSS, BROWNROCK;
    }

    private final char[][] mapLayout = {
        {'S','S','S','S','S','S','S','S','S','S'},
        {'S','G','G','F','F','F','F','F','B','S'},
        {'S','G','F','F','G','S','S','S','S','S'},
        {'S','G','F','G','G','G','S','S','S','S'},
        {'S','G','F','G','S','F','O','S','S','S'},
        {'S','G','F','G','F','O','G','O','S','S'},
        {'S','G','F','G','F','F','F','O','O','S'},
        {'S','F','F','G','F','O','F','M','O','S'},
        {'S','P','F','G','O','O','O','O','O','S'},
        {'S','S','S','S','S','S','S','S','S','S'}
    };

    public MapPanel(GUI gui, Hero player) {
        this.gui = gui;
        this.player = player;
        setLayout(new GridLayout(mapSize, mapSize));
        setOpaque(false);

        loadIcons();
        generateMap();

        setFocusable(true);
        addKeyListener(this);
        SwingUtilities.invokeLater(() -> requestFocusInWindow());
        requestFocusInWindow();
    }

    private void loadIcons() {
        playerIcon = loadIcon("/Resources/heroOption2.png");
        grassIcon = loadIcon("/Resources/grass.png");
        stoneIcon = loadIcon("/Resources/skalyV1.png");
        fieldIcon = loadIcon("/Resources/field.png");
        bossIcon = loadIcon("/Resources/mapBoss.png");
        miniBossIcon = loadIcon("/Resources/potworSkalny.png");
        brownRockIcon = loadIcon("/Resources/brownRock.png");
    }

    private ImageIcon loadIcon(String path) {
        try {
            Image img = ImageIO.read(getClass().getResourceAsStream(path));
            Image scaledImage = img.getScaledInstance(120, 100, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (IOException | NullPointerException e) {
            System.out.println("Missing image: " + path);
            return null;
        }
    }

    private void generateMap() {
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                JLabel tile = new JLabel();
                tile.setHorizontalAlignment(SwingConstants.CENTER);
                tile.setVerticalAlignment(SwingConstants.CENTER);

                char symbol = mapLayout[i][j];

                switch (symbol) {
                    case 'S' -> {
                        tileTypes[i][j] = TileType.STONE;
                        tile.setIcon(stoneIcon);
                    }
                    case 'G' -> {
                        tileTypes[i][j] = TileType.GRASS;
                        tile.setIcon(grassIcon);
                    }
                    case 'F' -> tileTypes[i][j] = TileType.FIELD;
                    case 'P' -> {
                        tileTypes[i][j] = TileType.PLAYER;
                        tile.setIcon(playerIcon);
                        playerX = j;
                        playerY = i;
                    }
                    case 'B' -> {
                        tileTypes[i][j] = TileType.BOSS;
                        tile.setIcon(bossIcon);
                    }
                    case 'M' -> {
                        tileTypes[i][j] = TileType.MINIBOSS;
                        tile.setIcon(miniBossIcon);
                    }
                    case 'O' -> {
                        tileTypes[i][j] = TileType.BROWNROCK;
                        tile.setIcon(brownRockIcon);
                    }
                    default -> tileTypes[i][j] = TileType.FIELD;
                }

                mapGrid[i][j] = tile;
                add(tile);
            }
        }
        tileTypes[playerY][playerX] = TileType.PLAYER;
        mapGrid[playerY][playerX].setIcon(playerIcon);
    }

    private void movePlayer(int x, int y) {
        int newX = playerX + x;
        int newY = playerY + y;

        if (newX < 0 || newX >= mapSize || newY < 0 || newY >= mapSize) return;
        if (tileTypes[newY][newX] == TileType.STONE || tileTypes[newY][newX] == TileType.BROWNROCK) return;

        int oldX = playerX;
        int oldY = playerY;

        playerX = newX;
        playerY = newY;

        if (tileTypes[playerY][playerX] == TileType.GRASS) {
            double chance = 0.35;
            if (Math.random() < chance) {
                JOptionPane.showMessageDialog(this, "You found an enemy hiding in the grass!");
                gui.getClient().sendToServer("FIGHT");
            }
        }

        if (tileTypes[playerY][playerX] == TileType.BOSS) {
            JOptionPane.showMessageDialog(this, "You encountered the Dark Knight!");
            gui.getClient().sendToServer("FIGHT_BOSS");
        }

        if (tileTypes[playerY][playerX] == TileType.MINIBOSS) {
            JOptionPane.showMessageDialog(this, "You encountered the Stone Golem!");
            gui.getClient().sendToServer("FIGHT_MINIBOSS");
        }

        char symbol = mapLayout[oldY][oldX];
        TileType type = getTileTypeFromSymbol(symbol);
        tileTypes[oldY][oldX] = type;

        if (type != TileType.FIELD) {
            mapGrid[oldY][oldX].setIcon(getIconForTile(type));
        } else {
            mapGrid[oldY][oldX].setIcon(null);
        }

        mapGrid[playerY][playerX].setIcon(playerIcon);
    }

    private TileType getTileTypeFromSymbol(char symbol) {
        return switch (symbol) {
            case 'G' -> TileType.GRASS;
            case 'S' -> TileType.STONE;
            case 'B' -> TileType.BOSS;
            case 'M' -> TileType.MINIBOSS;
            case 'O' -> TileType.BROWNROCK;
            case 'P', 'F' -> TileType.FIELD;
            default -> TileType.FIELD;
        };
    }

    private ImageIcon getIconForTile(TileType type) {
        return switch (type) {
            case GRASS -> grassIcon;
            case STONE -> stoneIcon;
            case FIELD -> fieldIcon;
            case BOSS -> bossIcon;
            case MINIBOSS -> miniBossIcon;
            case BROWNROCK -> brownRockIcon;
            default -> fieldIcon;
        };
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP, KeyEvent.VK_W -> movePlayer(0, -1);
            case KeyEvent.VK_DOWN, KeyEvent.VK_S -> movePlayer(0, 1);
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> movePlayer(1, 0);
            case KeyEvent.VK_LEFT, KeyEvent.VK_A -> movePlayer(-1, 0);
            case KeyEvent.VK_ESCAPE -> gui.showPauseMenu();
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (fieldIcon != null) {
            g.drawImage(fieldIcon.getImage(), 0, 0, getWidth(), getHeight(), this);
        }
    }
}
