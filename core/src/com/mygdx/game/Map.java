package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import net.dermetfan.gdx.physics.box2d.Box2DMapObjectParser;

/**
 * Created by k9sty on 2015-11-20.
 */
public class Map {

	Box2DMapObjectParser mapObjectParser;
	String mapName;
	BGM bgm;
	int i, nSpawners;
	Vector2 arV2ESpwn[];


	Map(World world, String mapName) {
		this.mapName = mapName;
		mapObjectParser = new Box2DMapObjectParser();
		mapObjectParser.load(world, new TmxMapLoader().load("maps/" + mapName + ".tmx"));
		// body that always exists is "level", which wraps the whole map
		mapObjectParser.getBodies();
		// fixtures is everything else, used so i can define filters
		mapObjectParser.getFixtures();
		mapObjectParser.getJoints();
		// everything involving music below
		bgm = new BGM(getBGM());
		bgm.setLooping(true);
		bgm.setVolume(0.1f);
		bgm.play();
		// end music
	}

	public TiledMap getMap() {
		return new TmxMapLoader().load("maps/" + this.mapName + ".tmx");
	}

	public float getUnitScale() {
		mapObjectParser.setUnitScale(0.5f);
		// IMPORTANT!!!
		// mapObjectParser's UnitScale is set to a half because of the way box2d works.
		// box2d uses meters, and there is a cap on velocity. i scale everything down so i don't
		// get stuck on the velocity cap and have to apply over 100 000 in a forcetocenter for a basic jump
		return mapObjectParser.getUnitScale();
	}

	public String getBGM() {
		String BGM = (String) getMap().getLayers().get("World").getObjects().get("level").getProperties().get("bgm");
		System.out.println(BGM);
		// in the .tmx file, the entire level is wrapped in one object named "level" which contains the name of the background music
		return BGM;
	}

	public Vector2 getSpawnpoint() {
		MapLayer layer = this.getMap().getLayers().get("World");
		RectangleMapObject spawnpoint = (RectangleMapObject) layer.getObjects().get("spawn point");
		return new Vector2(spawnpoint.getRectangle().getX(), spawnpoint.getRectangle().getY());
		// basic spawnpoint, it's a object that the player is relocated to and created
	}

	public Vector2[] getEnemySpawn() {
		MapLayer layer = this.getMap().getLayers().get("Enemy Spawners");
		MapObjects objects = layer.getObjects();
		nSpawners = objects.getCount();
		arV2ESpwn = new Vector2[nSpawners];
		for (i = 0; i < nSpawners; i++) {
			RectangleMapObject enemyspawn = (RectangleMapObject) objects.get("Espawner" + (i + 1));
			arV2ESpwn[i] = new Vector2(enemyspawn.getRectangle().getX(), enemyspawn.getRectangle().getY());
		}
		return arV2ESpwn;
	}

	void pauseBGM() {
		bgm.pause();
	}
}
