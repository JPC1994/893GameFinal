package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/* Created by Rueban Rasaselvan on 30/03/2016.
*/

//more types of enemies will be added
public class FastEnemy extends Entity {

    FastEnemy(World world, Vector2 V2posSpawn, String sSpriteLocation) {
        super(world, V2posSpawn, sSpriteLocation);
    }

    void move(float fPlayerX) {
        if (bdyMain.getLinearVelocity().x == 0) {
            bdyMain.applyLinearImpulse(0, 50, bdyMain.getPosition().x, bdyMain.getPosition().y, true);
            isIdle = true;
            bRight = false;
        }
        if (fPlayerX < bdyMain.getPosition().x) {
            bdyMain.setLinearVelocity(-50, bdyMain.getLinearVelocity().y);
            bRight = false;
            isIdle = false;
        } else if (fPlayerX > bdyMain.getPosition().x) {
            bdyMain.setLinearVelocity(50, bdyMain.getLinearVelocity().y);
            bRight = true;
            isIdle = false;
        }
    }
}
