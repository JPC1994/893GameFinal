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
    Body mainBody, footSensor;
    BodyDef bdefMain, bdefFoot;
    FixtureDef fdefPlayer, fdefFoot;
    PolygonShape shape;
    TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("player/idle/idle.pack"));
    TextureAtlas taRun = new TextureAtlas(Gdx.files.internal("player/run/run.pack"));
    Sprite[] sIdle = new Sprite[9];
    Sprite[] sRun = new Sprite[9];
    Animation idle, run;
    float elapsedTime = 0;
    World world;

    boolean bRight = true;

    enum State {
        idle, left, right
    }

    Player(World world, Vector2 spawnpoint) {
        this.world = world;
        createMainBody(spawnpoint);
        createFootSensor();
    }

    private void createMainBody(Vector2 spawnpoint) {
        this.state = state.idle;
        for (int i = 1; i < 10; i++) {
            sIdle[i - 1] = new Sprite(taIdle.findRegion("idle (" + i + ")"));
            sRun[i - 1] = new Sprite(taRun.findRegion("run (" + i + ")"));
        }
        idle = new Animation(10, sIdle);
        run = new Animation(5, sRun);
        bdefMain = new BodyDef();
        shape = new PolygonShape();

        bdefMain.position.set(new Vector2(spawnpoint.x / 2, spawnpoint.y / 2));
        bdefMain.type = BodyDef.BodyType.DynamicBody;
        mainBody = world.createBody(bdefMain);
        mainBody.setFixedRotation(true);

        shape.setAsBox(sIdle[0].getWidth() / 4, sIdle[0].getHeight() / 4);
        fdefPlayer = new FixtureDef();
        fdefPlayer.shape = shape;
        fdefPlayer.friction = 0;
        mainBody.setSleepingAllowed(false);
        mainBody.createFixture(fdefPlayer);
        shape.dispose();
    }

    private void createFootSensor() {
        shape = new PolygonShape();

        shape.setAsBox(sIdle[0].getWidth() / 4 - 4, 0.2f, new Vector2(mainBody.getWorldCenter().x / 4 - sIdle[0].getWidth() / 4 + 0.5f, mainBody.getPosition().y / 4 - sIdle[0].getHeight() - 9.5f), 0);
        fdefFoot = new FixtureDef();
        fdefFoot.isSensor = true;
        fdefFoot.shape = shape;

        mainBody.createFixture(fdefFoot);
        shape.dispose();
    }

    Vector3 getPosition() {
        return new Vector3(mainBody.getPosition().x, mainBody.getPosition().y, 0);
    }

    void draw(SpriteBatch sb) {
        elapsedTime++;
        if (this.state == state.idle) {
            if (bRight) {
                sb.draw(idle.getKeyFrame(elapsedTime, true), mainBody.getPosition().x - sIdle[0].getWidth() / 4, mainBody.getPosition().y - sIdle[0].getHeight() / 4, sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
            } else {
                sb.draw(idle.getKeyFrame(elapsedTime, true), mainBody.getPosition().x + sIdle[0].getWidth() / 4, mainBody.getPosition().y - sIdle[0].getHeight() / 4, -sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
            }
        } else if (this.state == state.right) {
            sb.draw(run.getKeyFrame(elapsedTime, true), mainBody.getPosition().x - sIdle[0].getWidth() / 4, mainBody.getPosition().y - sIdle[0].getHeight() / 4, sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
        } else if (this.state == state.left) {
            sb.draw(run.getKeyFrame(elapsedTime, true), mainBody.getPosition().x + sIdle[0].getWidth() / 4, mainBody.getPosition().y - sIdle[0].getHeight() / 4, -sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
        }
    }

    void move() {
        if (mainBody.getLinearVelocity().x > 100) {
            mainBody.getLinearVelocity().x--;
        } else if (mainBody.getLinearVelocity().x < -100) {
            mainBody.getLinearVelocity().x++;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            this.state = state.left;
            bRight = false;
            //mainBody.applyForceToCenter(-200, 0, true);
            mainBody.setLinearVelocity(-100, mainBody.getLinearVelocity().y);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            this.state = state.right;
            bRight = true;
            //mainBody.applyForceToCenter(200, 0, true);
            mainBody.setLinearVelocity(100, mainBody.getLinearVelocity().y);
        }
    }

    void stop() {
        this.state = state.idle;
        mainBody.setLinearVelocity(0, mainBody.getLinearVelocity().y);
    }

    boolean isGrounded = true;

    void jump() {
        mainBody.applyLinearImpulse(new Vector2(0, mainBody.getMass() * 500), mainBody.getWorldCenter(), true);
        //mainBody.setLinearVelocity(mainBody.getLinearVelocity().x, 50);
    }

    Vector2 getLinearVelocity() {
        return mainBody.getLinearVelocity();
    }

}
