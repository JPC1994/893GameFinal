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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by k9sty on 2016-03-12.
 */


public class Player extends Actor {
	State state;
	Body body;
	Fixture footSensor;
	TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("player/idle/idle.pack"));
	TextureAtlas taRun = new TextureAtlas(Gdx.files.internal("player/run/run.pack"));
	Sprite[] sIdle = new Sprite[9];
	Sprite[] sRun = new Sprite[9];
	Animation aniIdle, aniRun;
	float elapsedTime = 0;
	World world;
	final int nFinHealth = 3;
	public int nCurHealth;

	boolean bRight = true, isGrounded = true;

	enum State {
		IDLE, LEFT, RIGHT
		// enumeration for animations
	}

	Player(World world, Vector2 spawnpoint) {
		this.world = world;
		createMainBody(spawnpoint);
		createFootSensor();
	}

	private void createMainBody(Vector2 spawnpoint) {
		state = State.IDLE;
		for(int i = 1; i < 10; i++) {
			sIdle[i - 1] = new Sprite(taIdle.findRegion("idle (" + i + ")"));
			sRun[i - 1] = new Sprite(taRun.findRegion("run (" + i + ")"));
		}
		aniIdle = new Animation(10, sIdle);
		aniRun = new Animation(5, sRun);
		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();

		bodyDef.position.set(new Vector2(spawnpoint.x / 2, spawnpoint.y / 2));
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bodyDef);
		body.setFixedRotation(true);

		shape.setAsBox(sIdle[0].getWidth() / 4, sIdle[0].getHeight() / 4);
		FixtureDef fdefPlayer = new FixtureDef();
		fdefPlayer.shape = shape;
		fdefPlayer.friction = 1;
		body.setSleepingAllowed(false);
		body.createFixture(fdefPlayer);
		shape.dispose();
		// set categorybit to 0 so it collides with nothing
	}

	private void createFootSensor() {
		PolygonShape shape = new PolygonShape();

		//shape.setAsBox(sIdle[0].getWidth() / 4, 0.2f, new Vector2(body.getWorldCenter().x / 4 - sIdle[0].getWidth() / 4 + 0.5f, body.getPosition().y / 4 - sIdle[0].getHeight() - 9.5f), 0);
		shape.setAsBox(sIdle[0].getWidth() / 8, 0.2f, new Vector2(body.getWorldCenter().x / 4 - sIdle[0].getWidth() / 4 + 0.5f, body.getPosition().y / 4 - sIdle[0].getHeight() - 9.5f), 0);

		FixtureDef fdefFootSensor = new FixtureDef();
		fdefFootSensor.shape = shape;
		fdefFootSensor.isSensor = true;

		footSensor = body.createFixture(fdefFootSensor);
		shape.dispose();
		// create a foot sensor to detect whether or not the player is grounded
	}

	Vector3 getPosition() {
		return new Vector3(body.getPosition().x, body.getPosition().y, 0);
	}

	void draw(SpriteBatch spriteBatch) {
		// drawing sprite on player body using default library, not using animatedbox2dsprite because it doesn't loop the animation
		elapsedTime++;
		if(this.state == State.IDLE) {
			if(bRight) {
				spriteBatch.draw(aniIdle.getKeyFrame(elapsedTime, true), body.getPosition().x - sIdle[0].getWidth() / 4, body.getPosition().y - sIdle[0].getHeight() / 4, sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
			} else {
				spriteBatch.draw(aniIdle.getKeyFrame(elapsedTime, true), body.getPosition().x + sIdle[0].getWidth() / 4, body.getPosition().y - sIdle[0].getHeight() / 4, -sIdle[0].getWidth() / 2, sIdle[0].getHeight() / 2);
			}
		} else if(state == State.RIGHT) {
			spriteBatch.draw(aniRun.getKeyFrame(elapsedTime, true), body.getPosition().x - sIdle[0].getWidth() / 4, body.getPosition().y - sIdle[0].getHeight() / 4, sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
		} else if(state == State.LEFT) {
			spriteBatch.draw(aniRun.getKeyFrame(elapsedTime, true), body.getPosition().x + sIdle[0].getWidth() / 4, body.getPosition().y - sIdle[0].getHeight() / 4, -sRun[0].getWidth() / 2, sRun[0].getHeight() / 2);
		}
	}

	void move() {
		if(Gdx.input.isTouched()) {
			int screenHeight = Gdx.graphics.getHeight();
			int screenWidth = Gdx.graphics.getWidth();
			int touchX = Gdx.input.getX();
			int touchY = Gdx.input.getY(); // Don't get near this guy ;}

			if(touchX > screenWidth - (screenWidth / 3) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom right
				body.setLinearVelocity(100f, body.getLinearVelocity().y);
				state = State.RIGHT;
			} else if(touchX < (screenWidth / 3f) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom left
				body.setLinearVelocity(-100f, body.getLinearVelocity().y);
				state = State.LEFT;
			} else if(isGrounded && touchY > screenHeight - (screenHeight / 3)) { // Bottom middle
				jump();
			}
		} else {
			state = State.IDLE;
			stop();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
				body.setLinearVelocity(-100f, body.getLinearVelocity().y);
				state = State.LEFT;
			} else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
				body.setLinearVelocity(100f, body.getLinearVelocity().y);
				state = State.RIGHT;
			} else stop();

			if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
				jump();
			}
		}
	}

	void stop() {
		// stop movement on release of keycode
		state = State.IDLE;
		body.setLinearVelocity(0, body.getLinearVelocity().y);
	}

	void jump() {
		// using getmass so i can get around being limited by gravity
		// gravity sucks
		// there is one issue: jumping while moving gives you a lower jump height than jumping while standing
		// used applyForceToCenter and setLinearVelocity, same result
		body.applyLinearImpulse(new Vector2(0, body.getMass() * 500), body.getWorldCenter(), true);
	}

	Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

}
