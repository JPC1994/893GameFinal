package com.mygdx.game;

import com.badlogic.gdx.maps.MapLayer;
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

    Box2DMapObjectParser b2dmop;
    String mapName;
    BGM bgm;

    Map(World world, String mapName) {
        this.mapName = mapName;
        b2dmop = new Box2DMapObjectParser();
        b2dmop.load(world, new TmxMapLoader().load("maps/" + mapName + ".tmx"));
        // body that always exists is "level", which wraps the whole map
        b2dmop.getBodies();
        // fixtures is everything else, used so i can define filters
        b2dmop.getFixtures();
        b2dmop.getJoints();
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
        b2dmop.setUnitScale(0.5f);
        // IMPORTANT!!!
        // b2dmop's UnitScale is set to a half because of the way box2d works.
        // box2d uses meters, and there is a cap on velocity. i scale everything down so i don't
        // get stuck on the velocity cap and have to apply over 100 000 in a forcetocenter for a basic jump
        return b2dmop.getUnitScale();
    }

    public String getBGM() {
        String BGM = (String) getMap().getLayers().get("Object Layer 1").getObjects().get("level").getProperties().get("bgm");
        System.out.println(BGM);
        // in the .tmx file, the entire level is wrapped in one object named "level" which contains the name of the background music
        return BGM;
    }

    public Vector2 getSpawnpoint() {
        MapLayer layer = this.getMap().getLayers().get("Object Layer 1");
        RectangleMapObject spawnpoint = (RectangleMapObject) layer.getObjects().get("spawn point");
        return new Vector2(spawnpoint.getRectangle().getX(), spawnpoint.getRectangle().getY());
        // basic spawnpoint, it's a object that the player is relocated to and created
    }

    void pauseBGM() {
        bgm.pause();
    }
}
