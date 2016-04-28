package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by k9sty on 2016-03-12.
 */


public class Entity {
    // what benefits are you getting from your main being an Actor? None, unless you are going to get a stage on the screen.
    //State state; // enumeration definition below
    Body bdyMain;
    BodyDef bdefMain;
    FixtureDef fdefMain, fdefFoot;
    Fixture footSensor;
    PolygonShape shape;
    String sSprLoc;
    TextureAtlas taIdle, taRun;
    //Sprite[] arSprIdle = new Sprite[9];
    //Sprite[] arSprRun = new Sprite[9];
    TextureRegion trMain; // since an animation returns a TextureRegion, and not a Sprite.
    Animation aniIdle, aniRun;
    float fElapsedTime = 0;
    World world;
    float fX, fY, fW, fH; // for main dimensions.

    boolean bRight = true;
    boolean isIdle; // getting this attribute out of the State enumeration.

    boolean isGrounded = true;

    /*
        enum State {
            left, right
            // enumeration for animations
            // why not iL iR, rL, rR ???
        }
    */
    Entity(World world, Vector2 V2posSpawn, String sSprLoc) {
        this.sSprLoc = sSprLoc;
        this.world = world;
        createbdyMain(V2posSpawn);
        createFootSensor();
    }

    private void createbdyMain(Vector2 V2posSpawn) {// v2SpawnPt
        isIdle = true;
        taRun = new TextureAtlas(Gdx.files.internal(sSprLoc + "/run/run.pack"));
        taIdle = new TextureAtlas(Gdx.files.internal(sSprLoc + "/idle/idle.pack"));
        //this.state = state.idle;
/*
        for (int i = 1; i < 10; i++) {
            arSprIdle[i - 1] = new Sprite(taIdle.findRegion("idle (" + i + ")"));
            arSprRun[i - 1] = new Sprite(taRun.findRegion("run (" + i + ")"));
        }
*/
        // the next two variables are needed in the draw function.
        //fW = arSprIdle[0].getWidth();
        //fH = arSprIdle[0].getHeight();
        // An easier way to populate an animation:
        aniIdle = new Animation(10, taIdle.getRegions());
        aniRun = new Animation(10, taRun.getRegions());
        // get one textureRegion in order to get the frame's height and width. These 2 variables clean up a lot of code.
        trMain = aniIdle.getKeyFrame(fElapsedTime, true);
        fW = trMain.getRegionWidth();
        fH = trMain.getRegionHeight();
        //aniIdle = new Animation(10, arSprIdle); // the first int is the frame duration. Idle is slower than running.
        //aniRun = new Animation(5, arSprRun);
        bdefMain = new BodyDef();
        shape = new PolygonShape();

        bdefMain.position.set(new Vector2(V2posSpawn.x / 2, V2posSpawn.y / 2));
        bdefMain.type = BodyDef.BodyType.DynamicBody;
        bdyMain = world.createBody(bdefMain);
        bdyMain.setFixedRotation(true);

        shape.setAsBox(fW / 4, fH / 4);
        fdefMain = new FixtureDef();
        fdefMain.shape = shape;
        fdefMain.filter.categoryBits = 0;
        fdefMain.friction = 1;
        bdyMain.setSleepingAllowed(false);
        bdyMain.createFixture(fdefMain);
        shape.dispose();
        // set categorybit to 0 so it collides with nothing
    }

    private void createFootSensor() {
        PolygonShape shape = new PolygonShape();

        TextureRegion trPlayer = aniIdle.getKeyFrame(0);
        int width = trPlayer.getRegionWidth();

        //shape.setAsBox(width / 4, 0.2f, new Vector2(body.getWorldCenter().x / 4 - width / 4 + 0.5f, body.getPosition().y / 4 - height - 9.5f), 0);
        shape.setAsBox(width / 8, 0.2f, new Vector2(bdyMain.getLocalCenter().x, bdyMain.getLocalCenter().y - 10f), 0);

        FixtureDef fdefFoot = new FixtureDef();
        fdefFoot.shape = shape;
        fdefFoot.isSensor = false;

        footSensor = bdyMain.createFixture(fdefFoot);
        shape.dispose();
        // create a foot sensor to detect whether or not the player is grounded
    }

    Vector3 getPosition() {
        return new Vector3(bdyMain.getPosition().x, bdyMain.getPosition().y, 0);
    }

    void draw(SpriteBatch sb) {
        // drawing sprite on main body using default library, not using animatedbox2dsprite because it doesn't loop the animation
        fElapsedTime++;
        // I will comment out the code below to show how I will try to optimize it. Don's code is logical, but the isIdle
        // may be able to clean it up. I will use his bRight variable to get rid of the need for any "states".
        // Don's code is commented below
        fX = bdyMain.getPosition().x;
        fY = bdyMain.getPosition().y;
        // the next two vars should be populated in the body creation, since the values never change.
        // since these values are the same as the main running, they are all good.
        // update - I added the fW and fH population in the createBdyMain function since we only need to do it once.


        if (isIdle) {
            trMain = aniIdle.getKeyFrame(fElapsedTime, true);
            if (bRight) {
                //sb.draw(aniIdle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
                //sb.draw(aniIdle.getKeyFrame(fElapsedTime, true), fX - fW / 4, fY - fH / 4, fW / 2, fH / 2);
                sb.draw(trMain, fX - fW / 4, fY - fH / 4, fW / 2, fH / 2);
            } else {
                //sb.draw(aniIdle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
                sb.draw(trMain, fX + fW / 4, fY - fH / 4, -fW / 2, fH / 2);
            }
        } else {
            trMain = aniRun.getKeyFrame(fElapsedTime, true);
            if (bRight) {
                sb.draw(trMain, bdyMain.getPosition().x - fW / 4, bdyMain.getPosition().y - fH / 4, fW / 2, fH / 2);
            } else {
                sb.draw(trMain, bdyMain.getPosition().x + fW / 4, bdyMain.getPosition().y - fH / 4, -fW / 2, fH / 2);
            }
        }

        /*
        if (this.state == state.idle) {
            if (bRight) {
                sb.draw(idle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
            } else {
                sb.draw(idle.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprIdle[0].getWidth() / 2, arSprIdle[0].getHeight() / 2);
            }
        } else if (this.state == state.right) {
            sb.draw(run.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x - arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, arSprRun[0].getWidth() / 2, arSprRun[0].getHeight() / 2);
        } else if (this.state == state.left) {
            sb.draw(run.getKeyFrame(fElapsedTime, true), bdyMain.getPosition().x + arSprIdle[0].getWidth() / 4, bdyMain.getPosition().y - arSprIdle[0].getHeight() / 4, -arSprRun[0].getWidth() / 2, arSprRun[0].getHeight() / 2);
        }*/
    }

    void stop() {
        // stop movement on release of keycode
        //this.state = state.idle;
        isIdle = true;
        bdyMain.setLinearVelocity(0, bdyMain.getLinearVelocity().y);
    }

    void jump() {
        bdyMain.applyLinearImpulse(0, 100, bdyMain.getPosition().x, bdyMain.getPosition().y, true);
    }

}
