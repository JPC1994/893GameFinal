package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by k9sty on 2016-03-12.
 */


public class Player extends Actor {
	Body body;
	Fixture footSensor;
	TextureAtlas taIdle = new TextureAtlas(Gdx.files.internal("player/idle/idle.pack"));
	TextureAtlas taRun = new TextureAtlas(Gdx.files.internal("player/run/run.pack"));
	Animation aniIdle, aniRun;
	float elapsedTime = 0;
	World world;
	final int nFinHealth = 3;
	public int nCurHealth;

	boolean bRight = true, isGrounded = true, isIdle = true;

	Player(World world, Vector2 spawnpoint) {
		this.world = world;
		createMainBody(spawnpoint);
		createFootSensor();
	}

	private void createMainBody(Vector2 spawnpoint) {
		aniIdle = new Animation(10, taIdle.getRegions());
		aniRun = new Animation(5, taRun.getRegions());

		TextureRegion trPlayer = aniIdle.getKeyFrame(0);
		int width = trPlayer.getRegionWidth();
		int height = trPlayer.getRegionHeight();

		BodyDef bodyDef = new BodyDef();
		PolygonShape shape = new PolygonShape();

		bodyDef.position.set(new Vector2(spawnpoint.x / 2, spawnpoint.y / 2));
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.fixedRotation = true;
		body = world.createBody(bodyDef);

		shape.setAsBox(width / 4, height / 4);
		FixtureDef fdefPlayer = new FixtureDef();
		fdefPlayer.shape = shape;
		fdefPlayer.friction = 1;
		body.setSleepingAllowed(false);
		body.createFixture(fdefPlayer);
		shape.dispose();
	}

	private void createFootSensor() {
		PolygonShape shape = new PolygonShape();

		TextureRegion trPlayer = aniIdle.getKeyFrame(0);
		int width = trPlayer.getRegionWidth();
		int height = trPlayer.getRegionHeight();

		//shape.setAsBox(width / 4, 0.2f, new Vector2(body.getWorldCenter().x / 4 - width / 4 + 0.5f, body.getPosition().y / 4 - height - 9.5f), 0);
		shape.setAsBox(width / 8, 0.2f, new Vector2(body.getWorldCenter().x / 4 - width / 4 + 0.5f, body.getPosition().y / 4 - height - 9.5f), 0);

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

		TextureRegion trCurrent;

		if (isIdle)
			trCurrent = aniIdle.getKeyFrame(elapsedTime, true);
		else
			trCurrent = aniRun.getKeyFrame(elapsedTime, true);

		int width = trCurrent.getRegionWidth();
		int height = trCurrent.getRegionHeight();

		if (bRight)
			spriteBatch.draw(trCurrent, body.getPosition().x - width / 4, body.getPosition().y - height / 4, width / 2, height / 2);
		else
			spriteBatch.draw(trCurrent, body.getPosition().x + width / 4, body.getPosition().y - height / 4, -width / 2, height / 2);

		elapsedTime++;
	}

	void move() {
		if (Gdx.input.isTouched()) {

			int screenHeight = Gdx.graphics.getHeight();
			int screenWidth = Gdx.graphics.getWidth();
			int touchX = Gdx.input.getX();
			int touchY = Gdx.input.getY(); // Don't get near this guy ;}

			if (touchX > screenWidth - (screenWidth / 3) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom right, move right
				body.setLinearVelocity(100f, body.getLinearVelocity().y);
				bRight = true;
				isIdle = false;
			} else if (touchX < (screenWidth / 3f) && touchY > screenHeight - (screenHeight / 3f)) { // Bottom left, move left
				body.setLinearVelocity(-100f, body.getLinearVelocity().y);
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
	}

	void stop() {
		// stop movement
		isIdle = true;
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
