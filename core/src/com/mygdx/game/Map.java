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
        b2dmop.getBodies();
        b2dmop.getFixtures();
        b2dmop.getJoints();
        bgm = new BGM(getBGM());
        bgm.setLooping(true);
        bgm.setVolume(0.1f);
        bgm.play();
    }

    public TiledMap getMap() {
        return new TmxMapLoader().load("maps/" + this.mapName + ".tmx");
    }

    public float getUnitScale() {
        b2dmop.setUnitScale(0.5f);
        return b2dmop.getUnitScale();
    }

    public String getBGM() {
        String BGM = (String) getMap().getLayers().get("Object Layer 1").getObjects().get("level").getProperties().get("bgm");
        System.out.println(BGM);
        return BGM;
    }

    public Vector2 getSpawnpoint() {
        MapLayer layer = this.getMap().getLayers().get("Object Layer 1");
        RectangleMapObject spawnpoint = (RectangleMapObject) layer.getObjects().get("spawn point");
        return new Vector2(spawnpoint.getRectangle().getX(), spawnpoint.getRectangle().getY());
    }

    void pauseBGM() {
        bgm.pause();
    }
}
