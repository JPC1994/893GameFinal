package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by k9sty on 2016-03-12.
 */


public class Player extends Actor {
    State state;
    Body bplayer;
    BodyDef bdef;
    FixtureDef fdefPlayer;
    PolygonShape shape;
    TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("player/idle/idle.pack"));
    TextureAtlas taRun = new TextureAtlas(Gdx.files.internal("player/run/run.pack"));
    Sprite[] sIdle = new Sprite[9];
    Sprite[] sRun = new Sprite[9];
    Animation idle, run;
    float elapsedTime = 0;

    boolean bRight = true;

    enum State {
        idle, left, right
    }

    Player(World world, Vector2 spawnpoint) {
        this.state = state.idle;
        for (int i = 1; i < 10; i++) {
            sIdle[i - 1] = new Sprite(taIdle.findRegion("idle (" + i + ")"));
            sRun[i - 1] = new Sprite(taRun.findRegion("run (" + i + ")"));
        }
        idle = new Animation(10, sIdle);
        run = new Animation(5, sRun);
        bdef = new BodyDef();
        shape = new PolygonShape();

        bdef.position.set(new Vector2(spawnpoint.x / 2, spawnpoint.y / 2));
        bdef.type = BodyDef.BodyType.DynamicBody;
        bplayer = world.createBody(bdef);

        shape.setAsBox(sIdle[0].getWidth() / 4, sIdle[0].getHeight() / 4);
        fdefPlayer = new FixtureDef();
        fdefPlayer.shape = shape;
        fdefPlayer.filter.categoryBits = 1;
        bplayer.setSleepingAllowed(false);

        bplayer.setLinearDamping(1);
        bplayer.createFixture(fdefPlayer);
    }

    Vector3 getPosition() {
        return new Vector3(bplayer.getPosition().x, bplayer.getPosition().y, 0);
    }

    void draw(SpriteBatch sb) {
        elapsedTime++;
        if (this.state == state.idle) {
            if (bRight) {
                sb.draw(idle.getKeyFrame(elapsedTime, true), bplayer.getPosition().x - sIdle[0].getWidth() / 4, bplayer.getPosition().y - sIdle[0].getHeight() / 4, sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
            } else {
                sb.draw(idle.getKeyFrame(elapsedTime, true), bplayer.getPosition().x + sIdle[0].getWidth() / 4, bplayer.getPosition().y - sIdle[0].getHeight() / 4, -sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
            }
        } else if (this.state == state.right) {
            sb.draw(run.getKeyFrame(elapsedTime, true), bplayer.getPosition().x - sIdle[0].getWidth() / 4, bplayer.getPosition().y - sIdle[0].getHeight() / 4, sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
        } else if (this.state == state.left) {
            sb.draw(run.getKeyFrame(elapsedTime, true), bplayer.getPosition().x + sIdle[0].getWidth() / 4, bplayer.getPosition().y - sIdle[0].getHeight() / 4, -sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
        }
    }

    void move() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.state = state.left;
            bRight = false;
            //bplayer.setLinearVelocity(-100, bplayer.getLinearVelocity().y);
            if (bplayer.getLinearVelocity().x != -200) {
                bplayer.applyForceToCenter(-200, 0, true);
            }
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.state = state.right;
            bRight = true;
            if (bplayer.getLinearVelocity().x != 200) {
                bplayer.applyForceToCenter(200, 0, true);
            }
        }
    }

    void stop() {
        this.state = state.idle;
        bplayer.setLinearVelocity(0, bplayer.getLinearVelocity().y);
    }


    boolean isGrounded = true;

    void jump() {
        bplayer.setLinearVelocity(bplayer.getLinearVelocity().x, 200);
    }

}
