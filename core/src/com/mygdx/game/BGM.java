package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class BGM implements Music {

	// purpose of class is just to play the music defined in the .tmx file
	// only thing to really look at is the constructor otherwise who cares

	Music music;

	BGM(String bgm) {
		music = Gdx.audio.newMusic(Gdx.files.internal("bgm/" + bgm + ".ogg"));
	}

	@Override
	public void play() {
		music.play();
	}

	@Override
	public void pause() {
		music.pause();
	}

	@Override
	public void stop() {
		music.stop();
	}

	@Override
	public boolean isPlaying() {
		return music.isPlaying();
	}

	@Override
	public void setLooping(boolean isLooping) {
		music.setLooping(isLooping);
	}

	@Override
	public boolean isLooping() {
		return music.isLooping();
	}

	@Override
	public void setVolume(float volume) {
		music.setVolume(volume);
	}

	@Override
	public float getVolume() {
		return music.getVolume();
	}

	@Override
	public void setPan(float pan, float volume) {
		music.setPan(pan, volume);
	}

	@Override
	public void setPosition(float position) {
		music.setPosition(position);
	}

	@Override
	public float getPosition() {
		return music.getPosition();
	}

	@Override
	public void dispose() {
		music.dispose();
	}

	@Override
	public void setOnCompletionListener(OnCompletionListener listener) {
		music.setOnCompletionListener(listener);
	}
}
