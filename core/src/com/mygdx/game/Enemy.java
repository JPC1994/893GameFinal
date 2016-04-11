package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/* Created by Rueban Rasaselvan on 30/03/2016.
*/
public class Enemy extends Actor {
    Body enemyMainBody;
    BodyDef bDefEnemyMain;
    State state;
    FixtureDef fdefEnemy, fdefFoot;
    PolygonShape shape;
    World world;
    TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("player/idle/idle.pack"));
    TextureAtlas taRun = new TextureAtlas(Gdx.files.internal("player/run/run.pack"));
    Sprite[] sIdle = new Sprite[9];
    Sprite[] sRun = new Sprite[9];
    Animation idle, run;
    float elapsedTime = 0;

    boolean bRight = true, bGrounded = false;

    Enemy(World world, Vector2 enemyspawn) {
        this.world = world;
        createMainBody(enemyspawn);
        createFootSensor();
    }

    private void createMainBody(Vector2 enemyspawn) {
        this.state = State.idle;
        for (int i = 1; i < 10; i++) {
            sIdle[i - 1] = new Sprite(taIdle.findRegion("idle (" + i + ")"));
            sRun[i - 1] = new Sprite(taRun.findRegion("run (" + i + ")"));
        }
        idle = new Animation(10, sIdle);
        run = new Animation(5, sRun);
        bDefEnemyMain = new BodyDef();
        shape = new PolygonShape();

        bDefEnemyMain.position.set(new Vector2(enemyspawn.x / 2, enemyspawn.y / 2));
        bDefEnemyMain.type = BodyDef.BodyType.DynamicBody;
        enemyMainBody = world.createBody(bDefEnemyMain);
        enemyMainBody.setFixedRotation(true);

        shape.setAsBox(sIdle[0].getWidth() / 4, sIdle[0].getHeight() / 4);
        fdefEnemy = new FixtureDef();
        fdefEnemy.shape = shape;
        fdefEnemy.filter.categoryBits = 5;
        //http://box2d.org/manual.html#_Toc258082970 to set maskbits such that enemies don't collide with each other
        fdefEnemy.filter.maskBits = 16;
        fdefEnemy.friction = 1;
        enemyMainBody.setSleepingAllowed(false);
        enemyMainBody.createFixture(fdefEnemy);
        shape.dispose();
        // set categorybit to 0 so it collides with nothing
    }

    private void createFootSensor() {
        shape = new PolygonShape();

        shape.setAsBox(sIdle[0].getWidth() / 4, 0.2f, new Vector2(enemyMainBody.getWorldCenter().x / 4 - sIdle[0].getWidth() - 22f, enemyMainBody.getPosition().y / 4 - sIdle[0].getHeight() - 38f), 0);
        fdefFoot = new FixtureDef();
        fdefFoot.shape = shape;
        fdefFoot.filter.categoryBits = 1;

        enemyMainBody.createFixture(fdefFoot);
        shape.dispose();
        // create a foot sensor to detect whether or not the player is grounded
    }

    void draw(SpriteBatch sb) {
        // drawing sprite on player body using default library, not using animatedbox2dsprite because it doesn't loop the animation
        elapsedTime++;
        if (this.state == State.idle) {
            if (bRight) {
                sb.draw(idle.getKeyFrame(elapsedTime, true), enemyMainBody.getPosition().x - sIdle[0].getWidth() / 4, enemyMainBody.getPosition().y - sIdle[0].getHeight() / 4, sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
            } else {
                sb.draw(idle.getKeyFrame(elapsedTime, true), enemyMainBody.getPosition().x + sIdle[0].getWidth() / 4, enemyMainBody.getPosition().y - sIdle[0].getHeight() / 4, -sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
            }
        } else if (this.state == State.right) {
            sb.draw(run.getKeyFrame(elapsedTime, true), enemyMainBody.getPosition().x - sIdle[0].getWidth() / 4, enemyMainBody.getPosition().y - sIdle[0].getHeight() / 4, sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
        } else if (this.state == State.left) {
            sb.draw(run.getKeyFrame(elapsedTime, true), enemyMainBody.getPosition().x + sIdle[0].getWidth() / 4, enemyMainBody.getPosition().y - sIdle[0].getHeight() / 4, -sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
        }
    }

    void move(float fPlayerX) {
        if (enemyMainBody.getLinearVelocity().x == 0) {
            enemyMainBody.applyLinearImpulse(0, 75, enemyMainBody.getPosition().x, enemyMainBody.getPosition().y, true);
        }
        if (fPlayerX < enemyMainBody.getPosition().x) {
            enemyMainBody.setLinearVelocity(-75, enemyMainBody.getLinearVelocity().y);
            this.state = State.left;
        } else if (fPlayerX > enemyMainBody.getPosition().x) {
            enemyMainBody.setLinearVelocity(70, enemyMainBody.getLinearVelocity().y);
            this.state = State.right;
        }
    }

    enum State {
        idle, right, left
    }
}
