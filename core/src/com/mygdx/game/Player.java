package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by k9sty on 2016-03-12.
 */


public class Player extends Entity {
    // what benefits are you getting from your player being an Actor? None, unless you are going to get a stage on the screen.
    final int nFinHealth = 3;
    int nCurHealth;
    boolean isGrounded = true;

    Player(World world, Vector2 V2posSpawn, String sSpriteLocation) {
        super(world, V2posSpawn, sSpriteLocation);
    }

    void move() {
        if (Gdx.input.isTouched()) {

            int screenHeight = Gdx.graphics.getHeight();
            int screenWidth = Gdx.graphics.getWidth();
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY(); // Don't get near this guy ;}

            if (touchX > screenWidth - (screenWidth / 3) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom right, move right
                bdyMain.setLinearVelocity(100f, bdyMain.getLinearVelocity().y);
                bRight = true;
                isIdle = false;
            } else if (touchX < (screenWidth / 3f) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom left, move left
                bdyMain.setLinearVelocity(-100f, bdyMain.getLinearVelocity().y);
                bRight = false;
                isIdle = false;
            } else if (isGrounded && touchY > screenHeight - (screenHeight / 3)) { // Bottom middle, jump
                jump();
            } else { // Not tapping anywhere important
                isIdle = true;
                stop();
            }
        } else { // Not tapping anywhere
            isIdle = true;
            stop();
        }
        if (bdyMain.getLinearVelocity().x > 100) {
            bdyMain.getLinearVelocity().x--;
        } else if (bdyMain.getLinearVelocity().x < -100) {
            bdyMain.getLinearVelocity().x++;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //this.state = state.left;
            bRight = false;
            isIdle = false;
            //entity.bdyMain.applyForceToCenter(-200, 0, true);
            bdyMain.setLinearVelocity(-100, bdyMain.getLinearVelocity().y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            //this.state = state.right;
            bRight = true;
            isIdle = false;
            //entity.bdyMain.applyForceToCenter(200, 0, true);
            bdyMain.setLinearVelocity(100, bdyMain.getLinearVelocity().y);
        }
    }


    Vector2 getLinearVelocity() {
        return bdyMain.getLinearVelocity();
    }

}
