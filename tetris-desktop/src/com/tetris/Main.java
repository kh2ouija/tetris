package com.tetris;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Tetris";
		cfg.useGL20 = true;
		cfg.width = Settings.PIT_WIDTH * Settings.BLOCK_SIZE + 140;
		cfg.height = Settings.PIT_DEPTH * Settings.BLOCK_SIZE;
		
		new LwjglApplication(new TetrisGame(), cfg);
	}
}
