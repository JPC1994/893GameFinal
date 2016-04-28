package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Rueban Rasaselvan on 03/04/2016.
 */
public class EnemySpawner {
    Body mainSpawner;
    BodyDef bDefMainSpawner;
    PolygonShape shape;
    World world;
    //can be set to spawn any number of enemies
    //not able to set custom number of enemies for each spawner yet
    FastEnemy arObFastEnemy[] = new FastEnemy[2];
    int elapsedtime = 0, e = 0, i;
    float fPlayerDist, fPX, fPY;
    Vector2 v2ESpawnP;
    boolean bSpawn = false;

    EnemySpawner(World world, Vector2 enemyspawn) {
        this.world = world;
        createMainBody(enemyspawn);
    }

    //same code as the player's body but instead of a dynamic body, a static body is used instead
    private void createMainBody(Vector2 enemyspawn) {
        bDefMainSpawner = new BodyDef();
        shape = new PolygonShape();

        bDefMainSpawner.position.set(new Vector2(enemyspawn.x / 2, enemyspawn.y / 2));
        bDefMainSpawner.type = BodyDef.BodyType.StaticBody;
        mainSpawner = world.createBody(bDefMainSpawner);
        mainSpawner.setFixedRotation(true);

        shape.setAsBox(10, 10);
        shape.dispose();
    }

    void initializeEnemy() {
        arObFastEnemy[e - 1] = new FastEnemy(world, v2ESpawnP, "enemy");
    }

    void draw(int e, SpriteBatch sb) {
        //only the enemies that have already been spawned in are updated
        for (i = 0; i < e; i++) {
            arObFastEnemy[i].move(fPX);
            arObFastEnemy[i].draw(sb);
        }
    }

    public void update(float fPlayerX, float fPlayerY, Vector2 enemyspawnpoint, float timer) {
        fPlayerDist = Vector2.dst(mainSpawner.getPosition().x, mainSpawner.getPosition().y, fPlayerX, fPlayerY);
        elapsedtime += (timer / timer);
        v2ESpawnP = enemyspawnpoint;
        fPX = fPlayerX;
        fPY = fPlayerY;
        //only if the player enters within the region of radius 500 from the spawner block
        //then the enemies will spawn.
        if (fPlayerDist <= 500) {
            //every 5 seconds a new enemy is spawned
            if (elapsedtime > 50) {
                elapsedtime = 0;
                //keeps track of the number of enemies that were spawned so they can be updated
                if (e < arObFastEnemy.length) {
                    e += 1;
                    initializeEnemy();
                    bSpawn = true;
                }
            }
        }
    }
}


