package com.tetris;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TetrisInputProcessor implements InputProcessor {

	private TetrisGame game;

	public TetrisInputProcessor(TetrisGame game) {
		this.game = game;
	}

	static Map<Integer, PlayerInput> mapping = new HashMap<Integer, PlayerInput>();
	static {
		mapping.put(Input.Keys.LEFT, PlayerInput.MOVE_LEFT);
		mapping.put(Input.Keys.RIGHT, PlayerInput.MOVE_RIGHT);
		mapping.put(Input.Keys.UP, PlayerInput.ROTATE_CW);
		mapping.put(Input.Keys.DOWN, PlayerInput.MOVE_DOWN);
		mapping.put(Input.Keys.SPACE, PlayerInput.DROP);
	}

	private Set<Integer> keysDown = new HashSet<Integer>();
	private PlayerInput inputDown = PlayerInput.NONE;

	public PlayerInput getInputDown() {
		if ((keysDown.size() == 0) || (keysDown.size() >= 2)) {
			return PlayerInput.NONE;
		}
		else {
			return mapping.get(keysDown.iterator().next());
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT :
			case Input.Keys.RIGHT :
			case Input.Keys.UP :
			case Input.Keys.DOWN :
			case Input.Keys.SPACE : {
				inputDown = mapping.get(keycode);
				keysDown.add(keycode);
				break;
			}
			default: break;
		}
		if (!(((keycode == Input.Keys.LEFT) && keysDown.contains(Input.Keys.RIGHT)) || ((keycode == Input.Keys.RIGHT) && keysDown.contains(Input.Keys.LEFT)))) {
			game.movement(inputDown);
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		inputDown = PlayerInput.NONE;
		keysDown.remove(keycode);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}


}
