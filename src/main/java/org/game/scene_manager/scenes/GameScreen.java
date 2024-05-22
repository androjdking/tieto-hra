package org.game.scene_manager.scenes;

import org.game.actors.Player;
import org.game.actors.Projectile;
import org.game.actors.SpawnEnemy;
import org.game.scene_manager.IScene;
import org.game.scene_manager.SceneEnum;
import org.game.scene_manager.SceneManager;

import java.util.ArrayList;

public class GameScreen implements IScene {
    SceneManager manager;

    //variables for enemies
    ArrayList<SpawnEnemy> enemies = new ArrayList<>();
    ArrayList<Projectile> projectiles = new ArrayList<>();
    int spawnRate = (int) (width * Math.random());
    int pauseForSpawn = 3;

    @Override
    public void init(SceneManager manager) {
        this.manager = manager;
    }

    @Override
    public void update(String line) {
        Player.instance.alive = true;
        // spawn algorithm
        if (pauseForSpawn == 3) {
            pauseForSpawn = 0;
            for (int i = 0; i < spawnRate; i++) {
                SpawnEnemy enemy = new SpawnEnemy();
                if (!enemies.isEmpty()) {
                    for (SpawnEnemy checkEnemy : enemies) {
                        if (enemy.getXpos() == checkEnemy.getXpos() && enemy.getYpos() == checkEnemy.getYpos()) {
                            enemy = null;
                            break;
                        }
                    }
                }
                if (enemy != null) enemies.add(enemy);

            }
        }
        // moves enemies forward
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setYpos(enemies.get(i).getYpos() + 1);
            if (enemies.get(i).getYpos() == height) {
                enemies.remove(i);
                i--;
            }
        }
        pauseForSpawn++;
        //changing position of player
        switch (line) {
            case "a":
                if (!(Player.instance.getXpos() - 1 < 0) && Player.instance.alive) {
                    Player.instance.setXpos(Player.instance.getXpos() - 1);
                }
                break;
            case "d":
                if (!(Player.instance.getXpos() + 1 > width - 1) && Player.instance.alive) {
                    Player.instance.setXpos(Player.instance.getXpos() + 1);
                }
                break;
            case "w":
                Projectile projectile = new Projectile(Player.instance.getXpos(), Player.instance.getYpos());
                projectiles.add(projectile);
                break;
        }
        //removes projectiles if projectile reaches y position = 0
        if (!projectiles.isEmpty()) {
            for (int i = 0; i < projectiles.size(); i++) {
                if (projectiles.get(i).getYpos() == 0) {
                    projectiles.remove(i);
                    i--;
                    continue;
                }
                projectiles.get(i).setYpos(projectiles.get(i).getYpos() - 1);
            }
        }
        //checks for collision between projectile and enemies and removes them
        boolean deleteProjectile = false;
        for (int i = 0; i < projectiles.size(); i++) {
            for (int j = 0; j < enemies.size(); j++) {
                if (projectiles.get(i).getXpos() == enemies.get(j).getXpos() && projectiles.get(i).getYpos() == enemies.get(j).getYpos()) {
                    Player.instance.addScore(enemies.get(j).getScore());
                    enemies.remove(j);
                    j++;
                    deleteProjectile=true;
                }
            }
            if(deleteProjectile){
                projectiles.remove(i);
                i--;
                deleteProjectile=false;
            }

        }


        //checks for collision with player and kills them
        for (SpawnEnemy enemy : enemies) {
            if (enemy.getXpos() == Player.instance.getXpos() && enemy.getYpos() == Player.instance.getYpos()) {
                Player.instance.death();
                enemies.clear();
                projectiles.clear();
                Player.instance.setXpos(width / 2);
                Player.instance.setYpos(height - 1);
                manager.setCurrentScene(SceneEnum.DEATH);
                break;
            }
        }
        //adds one point after every turn
        Player.instance.addScore(1);
    }

    @Override
    public void render() {
        boolean charPresent = false;
        //console "clear"
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        // display graphics
        for (int rows = 0; rows < height; rows++) {
            System.out.print("|");
            for (int columns = 0; columns < width; columns++) {
                //check if current position matches player's position
                if (columns == Player.instance.getXpos() && rows == Player.instance.getYpos()) {
                    System.out.print(Player.instance.getIcon());
                    continue;
                }
                for (Projectile projectile : projectiles) {
                    if (projectile.getXpos() == columns && projectile.getYpos() == rows) {
                        System.out.print(projectile.getIcon());
                        charPresent = true;
                    }
                }

                //if enemy is present on current row and columns then it prints them
                if (!charPresent) {
                    for (SpawnEnemy enemy : enemies) {
                        if (enemy.getXpos() == columns && enemy.getYpos() == rows) {
                            System.out.print(enemy.getIcon());
                            charPresent = true;
                        }
                    }
                }
                if (!charPresent) {
                    System.out.print(' ');
                }
                charPresent = false;
            }
            System.out.println("|");
        }
    }
}