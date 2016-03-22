package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

public class ScrTest implements Screen, InputProcessor {
    Game game;
    World world;
    Map map;
    OrthographicCamera camera;
    Box2DDebugRenderer b2dr;
    TiledMapRenderer tiledMapRenderer;
    Player player;
    SpriteBatch batch = new SpriteBatch();

    ScrTest(Game game) {
        this.game = game;

        initializeWorld();
        initializeCamera();
        initializePlayer();
    }

    private void initializeWorld() {
        world = new World(new Vector2(0, -200), true);
        // create contact listener in the class itself so i don't need to turn every variable into a static when i call it
        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact c) {

            }

            @Override
            public void endContact(Contact c) {
                Fixture fa = c.getFixtureA();
                Fixture fb = c.getFixtureB();
                // only checking if one of the fixtures is the foot sensor - if the foot sensor is one of the contacts,
                // then the other fixture is something it's allowed to collide with (maskBit = 1)
                if (fa.getFilterData().categoryBits == 1) {
                    player.isGrounded = false;
                } else if (fb.getFilterData().categoryBits == 1) {
                    player.isGrounded = false;
                }
            }

            @Override
            public void preSolve(Contact c, Manifold oldManifold) {
                // i use presolve rather than beginContact because presolve runs right when the collision occurs
                // beginContact occurs when they begin to touch, leading to loss of accuracy
                // basically begincontact sucks
                Fixture fa = c.getFixtureA();
                Fixture fb = c.getFixtureB();

                if (fa.getFilterData().categoryBits == 1) {
                    player.isGrounded = true;
                } else if (fb.getFilterData().categoryBits == 1) {
                    player.isGrounded = true;
                }
            }

            @Override
            public void postSolve(Contact c, ContactImpulse impulse) {
                Fixture fa = c.getFixtureA();
                Fixture fb = c.getFixtureB();
            }
        });
        map = new Map(world, "debugroom");
        // pass world and desired map
    }

    private void initializeCamera() {
        b2dr = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 32 * (19 / 2), 32 * (10 / 2));
        // tile size * first two digits of resolution give you a solid camera, i just divide by 2 for a better view
        // two is a magic number
        camera.update();
        tiledMapRenderer = new OrthogonalTiledMapRenderer(map.getMap(), map.getUnitScale());
        // important: go to getUnitScale function in Map
    }

    private void initializePlayer() {
        player = new Player(world, map.getSpawnpoint());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(1 / 60f, 6, 2);
        camera.position.set(player.getPosition());
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        b2dr.render(world, camera.combined);

        batch.setProjectionMatrix(camera.combined);
        // set the projection matrix as the camera so the tile layer on the map lines up with the bodies
        // if this line wasn't here it wouldn't scale down
        batch.begin();
        player.draw(batch);
        batch.end();

        player.move();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.X && player.isGrounded) {
            player.jump();
            player.isGrounded = false;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == com.badlogic.gdx.Input.Keys.LEFT || keycode == com.badlogic.gdx.Input.Keys.RIGHT) {
            player.stop();
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
