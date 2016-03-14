package com.mygdx.game;

import com.badlogic.gdx.Game;

public class GameProject extends Game {

    @Override
    public void create() {
        setScreen(new TestGame(this));
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void render() {
        super.render();
    }
}
