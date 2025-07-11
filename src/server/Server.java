/**
 * Simple socket-based server for handling RPG client requests.
 * Supports enemy encounters and loot generation.
 */
package server;

/**
 * @author TomaszKaczmarek
 */

import models.Weapon;
import models.Enemy;
import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        final int PORT = 12345;
        System.out.println("Server started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                try (
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                ) {
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    Enemy lastEnemy = null;
                    String line;

                    while ((line = in.readLine()) != null) {
                        System.out.println("Received: " + line);

                        switch (line.toUpperCase()) {
                            case "END_SESSION" -> {
                                System.out.println("Client ended the connection.");
                                break;
                            }
                            case "FIGHT" -> {
                                Enemy enemy = EnemyGenerator.getRandomEnemy();
                                lastEnemy = enemy;
                                out.println("FIGHT;" + enemy.getName() + ";" + enemy.getHp() + ";" + enemy.getDmg()
                                        + ";" + enemy.getExpReward() + ";" + enemy.getImageName());
                            }
                            case "FIGHT_BOSS" -> {
                                Enemy boss = new Enemy("Dark Knight", 1700, 60, 5000, "/resources/przeciwnikDarkKnight.jpg");
                                lastEnemy = boss;
                                out.println("FIGHT;" + boss.getName() + ";" + boss.getHp() + ";" + boss.getDmg()
                                        + ";" + boss.getExpReward() + ";" + boss.getImageName());
                            }
                            case "FIGHT_MINIBOSS" -> {
                                Enemy mini = new Enemy("Magic Golem", 1000, 40, 2000, "/resources/przeciwnikOgnistyGolem.jpg");
                                lastEnemy = mini;
                                out.println("FIGHT;" + mini.getName() + ";" + mini.getHp() + ";" + mini.getDmg()
                                        + ";" + mini.getExpReward() + ";" + mini.getImageName());
                            }
                            case "LOOT" -> {
                                if (lastEnemy != null) {
                                    Weapon weapon = WeaponGenerator.generateWeapon(lastEnemy);
                                    out.println("LOOT;" + weapon.getName()+ ";" + weapon.getDamage()+ ";" + weapon.getRarity());
                                } else {
                                    out.println("LOOT;none;0;common");
                                }
                            }
                            default -> out.println("Unknown command: " + line);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connection error: " + e.getMessage());
                }

                System.out.println("Connection closed.");
                System.exit(0);
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
