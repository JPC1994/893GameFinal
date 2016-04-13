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

/**
 * Created by k9sty on 2016-03-12.
 */
public class ScrTest implements Screen, InputProcessor {
	Game game;
	World world;
	Map map;
	OrthographicCamera camera;
	Box2DDebugRenderer debugRenderer;
	TiledMapRenderer tiledMapRenderer;
	Player player;
	//used array of sprites concept from the drop project at: https://github.com/Mrgfhci/Drop/blob/master/core/src/com/mygdx/drop/Drop.java line 58
	Enemy[] arEnemies; // TODO: Name?
	SpriteBatch spriteBatch;

	ScrTest(Game game) {
		this.game = game;

		arEnemies = new Enemy[2];
		spriteBatch = new SpriteBatch();

		initializeWorld();
		initializeCamera();
		initializePlayer();
		initializeEnemy();
	}

	private void initializeWorld() {
		world = new World(new Vector2(0, -200), true);
		// create contact listener in the class itself so i don't need to turn every variable into a static when i call it
		world.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// Unlike presolve, beginContact is called for sensor. If you want to move the
				// other hit detection code to presolve, go ahead, just leave the sensor code
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();

				if (fixtureA.isSensor() && fixtureB.isSensor())
					return; // Who cares about that?

				if (fixtureA == player.footSensor)
					player.isGrounded = true;

				else if (fixtureB == player.footSensor)
					player.isGrounded = true;

				//http://box2d.org/manual.html#_Toc258082970 source for the way mask bits and categoryBits worked
				if (fixtureA.getFilterData().categoryBits == 5 && fixtureB.getFilterData().categoryBits == 16) {
					if (player.nCurHealth > 0) {
						player.nCurHealth -= 1;
						System.out.println("***************************************************************************" + player.nCurHealth);
					} else {
						System.out.println("You are dead!");
					}
				} else if (fixtureB.getFilterData().categoryBits == 5 && fixtureA.getFilterData().categoryBits == 16) {
					if (player.nCurHealth > 0) {
						player.nCurHealth -= 1;
						System.out.println("***************************************************************************" + player.nCurHealth);

					} else {
						System.out.println("You are dead!");
					}
				}
			}

			@Override
			public void endContact(Contact contact) {
				Fixture fixtureA = contact.getFixtureA();
				Fixture fixtureB = contact.getFixtureB();
				// only checking if one of the fixtures is the foot sensor - if the foot sensor is one of the contacts,
				// then the other fixture is something it's allowed to collide with (maskBit = 1)
				if (fixtureA.isSensor() && fixtureB.isSensor())
					return; // Who cares about that?

				if (fixtureA == player.footSensor)
					player.isGrounded = false;

				else if (fixtureB == player.footSensor)
					player.isGrounded = false;
			}

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {

			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {

			}
		});
		map = new Map(world, "debugroom");
		// pass world and desired map
	}

	private void initializeCamera() {
		debugRenderer = new Box2DDebugRenderer();
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
		player.nCurHealth = player.nFinHealth;
	}

	private void initializeEnemy() {
		for (int i = 0; i < 2; i++) {
			arEnemies[i] = new Enemy(world, map.getEnemySpawn());
		}
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
		debugRenderer.render(world, camera.combined);

		spriteBatch.setProjectionMatrix(camera.combined);
		// set the projection matrix as the camera so the tile layer on the map lines up with the bodies
		// if this line wasn't here it wouldn't scale down
		spriteBatch.begin();
		player.draw(spriteBatch);
		for (int i = 0; i < 2; i++) {
			arEnemies[i].draw(spriteBatch);
		}
		spriteBatch.end();

		player.move();
		for (int i = 0; i < 2; i++) {
			arEnemies[i].move(player.getPosition().x);
		}
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
