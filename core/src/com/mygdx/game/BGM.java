package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BGM implements Music {
    Music mBGM;

    BGM(String bgm) {
        mBGM = Gdx.audio.newMusic(Gdx.files.internal("bgm/" + bgm + ".ogg"));
    }

    @Override
    public void play() {
        mBGM.play();
    }

    @Override
    public void pause() {
        mBGM.pause();
    }

    @Override
    public void stop() {
        mBGM.stop();
    }

    @Override
    public boolean isPlaying() {
        return mBGM.isPlaying();
    }

    @Override
    public void setLooping(boolean isLooping) {
        mBGM.setLooping(isLooping);
    }

    @Override
    public boolean isLooping() {
        return mBGM.isLooping();
    }

    @Override
    public void setVolume(float volume) {
        mBGM.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return mBGM.getVolume();
    }

    @Override
    public void setPan(float pan, float volume) {
        mBGM.setPan(pan, volume);
    }

    @Override
    public void setPosition(float position) {
        mBGM.setPosition(position);
    }

    @Override
    public float getPosition() {
        return mBGM.getPosition();
    }

    @Override
    public void dispose() {
        mBGM.dispose();
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        mBGM.setOnCompletionListener(listener);
    }
}
