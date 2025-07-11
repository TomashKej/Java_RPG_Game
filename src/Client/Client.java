package Client;

/**
 * @author TomaszKaczmarek
 */

import models.Weapon;
import models.Hero;
import models.Enemy;
import javax.swing.SwingUtilities;
import java.io.*;
import java.net.*;

/**
 * Client class responsible for network communication with the game server
 * and launching the game GUI.
 */
public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private GUI gui;
    private Hero player = new Hero(null);

    /**
     * Returns the current player object.
     * Initializes a default player if none exists.
     */
    public Hero getPlayer() {
        if (player == null) {
            player = new Hero("PLAYER1");
        }
        return player;
    }

    /**
     * Allows overwriting the current player instance (e.g., loading a save).
     */
    public void setPlayer(Hero player) {
        this.player = player;
    }

    /**
     * Constructs the client and connects to the server socket.
     * Starts a background thread to listen for server messages.
     */
    public Client() throws IOException {
        socket = new Socket("localhost", 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        new Thread(this::listen).start(); // Starts listener thread to receive server messages
    }

    public void setGui(GUI gui) {
        this.gui = gui;
    }

    /**
     * Listens for incoming messages from the server and processes them.
     */
    private void listen() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Received: " + line);
                String[] parts = line.split(";", 2);
                String komenda = parts[0];
                String parametry = parts.length > 1 ? parts[1] : "";

                switch (komenda) {
                    case "FIGHT":
                        Enemy p = parseEnemy(parametry);
                        Hero g = getPlayer();
                        SwingUtilities.invokeLater(() -> gui.showFight(g, p));
                        break;
                    case "LOOT":
                        Weapon b = parseWeapon(parametry);
                        SwingUtilities.invokeLater(() -> gui.showLootDialog(b));
                        break;
                    default:
                        System.out.println("Unknown command: " + komenda);
                }
            }
        } catch (IOException e) {
            if (socket.isClosed()) {
                System.out.println("Connection closed by user.");
            } else {
                System.out.println("Lost connection to server.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Parses enemy data from a string and returns a Przeciwnik object.
     * Expected format: name;hp;dmg;xp;image
     */
    private Enemy parseEnemy(String dane) {
        try {
            String[] d = dane.split(";");
            return new Enemy(
                    d[0],
                    Integer.parseInt(d[1]),
                    Integer.parseInt(d[2]),
                    Integer.parseInt(d[3]),
                    d[4]
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new Enemy("Unknown", 10, 5, 2, null);
        }
    }

    /**
     * Parses weapon data from a string and returns a Bron object.
     * Expected format: name;damage;rarity
     */
    private Weapon parseWeapon(String dane) {
        try {
            String[] d = dane.split(";");
            return new Weapon(d[0], Integer.parseInt(d[1]), d[2]);
        } catch (Exception e) {
            e.printStackTrace();
            return new Weapon("Unknown Weapon", 0, "common");
        }
    }

    /**
     * Sends a message/command to the server.
     */
    public void sendToServer(String msg) {
        out.println(msg);
    }

    /**
     * Closes client connections.
     */
    public void endConection() throws IOException {
        if (socket != null) socket.close();
        if (in != null) in.close();
        if (out != null) out.close();
    }

    /**
     * Main entry point: initializes the client and GUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Client client = new Client();
                GUI gui = new GUI(client);
                client.setGui(gui);
                gui.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to connect to the server.");
            }
        });
    }
}
