package com.tetris;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tetris.pieces.O;
import com.tetris.pieces.PieceType;
import com.tetris.pieces.PieceTypes;

import java.util.HashMap;
import java.util.Map;

import static com.tetris.Settings.*;

public class TetrisGame implements ApplicationListener {
	
	private int[][] pit;
	private PieceType nextPieceType;
	private FallingPiece fallingPiece;
	private long lastGravity;
	private Map<PlayerInput, Long> lastMovementMap;
	
	private TetrisInputProcessor tetrisInputProcessor;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Texture blocksTexture;
	private Map<Integer, TextureRegion> blocks;
	private int score;
	private GameState gameState;
	private int speed;

	private enum GameState { RUNNING, GAMEOVER }

	@Override
	public void create() {
		// game stuff
		pit = new int[PIT_DEPTH][PIT_WIDTH];
		for (int i = 0; i < PIT_DEPTH; i++) {
			for (int j = 0; j< PIT_WIDTH; j++) {
				pit[i][j] = -1;
			}
		}
		spawnNewPiece();
		lastGravity = System.currentTimeMillis();
		lastMovementMap = new HashMap<PlayerInput, Long>();
		score = 0;
		gameState = GameState.RUNNING;
		speed = Settings.GRAVITY_MILLIS;
		
		// engine stuff
		Gdx.app.log("Tetris", "create()");
        camera = new OrthographicCamera();
		camera.setToOrtho(false, Settings.PIT_WIDTH * Settings.BLOCK_SIZE, Settings.PIT_DEPTH * Settings.BLOCK_SIZE);
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
		tetrisInputProcessor = new TetrisInputProcessor(this);
		Gdx.input.setInputProcessor(tetrisInputProcessor);
		blocksTexture = new Texture(Gdx.files.internal("data/iotszjl.png"));
		blocks = new HashMap<Integer, TextureRegion>();
		for (int i = 0; i < 7; i++) {
			blocks.put(i, new TextureRegion(blocksTexture, i*BLOCK_SIZE, 0, BLOCK_SIZE, BLOCK_SIZE));
		}
		spriteBatch = new SpriteBatch();
	}

	
	@Override
	public void render() {
		if (gameState == GameState.RUNNING) {
			if (System.currentTimeMillis() - lastGravity >= speed) {
				gravity();
				lastGravity = System.currentTimeMillis();
			}
			else {
				PlayerInput inputDown = tetrisInputProcessor.getInputDown();
				if (inputDown != PlayerInput.NONE) {
					long threshold = Settings.MOVEMENT_MILLIS;
					if (inputDown.equals(PlayerInput.ROTATE_CW) || inputDown.equals(PlayerInput.ROTATE_CCW)) {
						threshold = Settings.ROTATION_MILLIS;
					}
					else if (inputDown.equals(PlayerInput.DROP)) {
						threshold = Settings.DROP_MILLIS;
					}
					if ((lastMovementMap.get(inputDown) == null) || (System.currentTimeMillis() - lastMovementMap.get(inputDown) >= threshold)) {
						movement(inputDown);
						lastMovementMap.put(inputDown, System.currentTimeMillis());
					}
				}
			}

			Gdx.gl.glClearColor(0, 0, 0.2f , 1);
	        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

	        shapeRenderer.begin(ShapeRenderer.ShapeType.FilledRectangle);

	        // draw pit background
	        shapeRenderer.setColor(Color.BLACK);
	        shapeRenderer.filledRect(0, 0, PIT_WIDTH * BLOCK_SIZE, (PIT_DEPTH - PIT_HIDDEN_TOP) * BLOCK_SIZE);

			// ghost
			FallingPiece ghost = fallingPiece.makeCandidate();
			while (true) {
				ghost.setLine(ghost.getLine() + 1);
				if (! isValid(ghost)) {
					ghost.setLine(ghost.getLine() - 1);
					break;
				}
			}
			shapeRenderer.setColor(Color.LIGHT_GRAY);
			for (int pieceLine = 0; pieceLine < ghost.getType().getBoundingBoxSize(); pieceLine++) {
				for (int pieceColumn = 0; pieceColumn < ghost.getType().getBoundingBoxSize(); pieceColumn++) {
					if (ghost.getRendering()[pieceLine][pieceColumn] == 1) {
						int pitLine = pitLine(ghost, pieceLine);
						int pitColumn = pitColumn(ghost, pieceColumn);
						if ((pitLine >= 0) && (pitLine < PIT_DEPTH)) {
							shapeRenderer.filledRect(pitColumn * BLOCK_SIZE, (PIT_DEPTH - pitLine - 1) * BLOCK_SIZE,
									BLOCK_SIZE, BLOCK_SIZE);
						}
					}
				}
			}

			shapeRenderer.end();

	        // draw pit blocks
			spriteBatch.begin();
			for (int pitLine = PIT_HIDDEN_TOP; pitLine < PIT_DEPTH; pitLine++) {
	            for (int pitColumn = 0; pitColumn < PIT_WIDTH; pitColumn++) {
	                if (pit[pitLine][pitColumn] != -1) {
	                    spriteBatch.draw(blocks.get(pit[pitLine][pitColumn]), pitColumn * BLOCK_SIZE, (PIT_DEPTH - pitLine - 1) * BLOCK_SIZE);
	                }
	            }
	        }

	        // draw piece
	        for (int pieceLine = 0; pieceLine < fallingPiece.getType().getBoundingBoxSize(); pieceLine++) {
				for (int pieceColumn = 0; pieceColumn < fallingPiece.getType().getBoundingBoxSize(); pieceColumn++) {
					if (fallingPiece.getRendering()[pieceLine][pieceColumn] == 1) {
						int pitLine = pitLine(fallingPiece, pieceLine);
						int pitColumn = pitColumn(fallingPiece, pieceColumn);
						if ((pitLine >= PIT_HIDDEN_TOP) && (pitLine < PIT_DEPTH)) {
							spriteBatch.draw(blocks.get(fallingPiece.getType().getIndexInTexture()), pitColumn * BLOCK_SIZE, (PIT_DEPTH - pitLine - 1) * BLOCK_SIZE);
						}
					}
				}
			}

			// score
			font.setColor(Color.WHITE);
			font.draw(spriteBatch, "Score: " + score, PIT_WIDTH * BLOCK_SIZE + 10, PIT_DEPTH * BLOCK_SIZE - 10);

			// next piece
			font.draw(spriteBatch, "Next: ", PIT_WIDTH * BLOCK_SIZE + 10, PIT_DEPTH * BLOCK_SIZE - 30);
			int[][] rendering = nextPieceType.getRenderings()[0];
			for (int pieceLine = 0; pieceLine < nextPieceType.getBoundingBoxSize(); pieceLine++) {
				for (int pieceColumn = 0; pieceColumn < nextPieceType.getBoundingBoxSize(); pieceColumn++) {
					if (rendering[pieceLine][pieceColumn] == 1) {
						spriteBatch.draw(blocks.get(nextPieceType.getIndexInTexture()), (PIT_WIDTH + pieceColumn) * BLOCK_SIZE + 10, (PIT_DEPTH - pieceLine) * BLOCK_SIZE - 80);
					}
				}
			}

	        spriteBatch.end();
		}
		else {
			Gdx.gl.glClearColor(0, 0, 0.2f , 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			spriteBatch.begin();
			font.setColor(Color.RED);
			font.draw(spriteBatch, "GAME OVER", PIT_WIDTH * BLOCK_SIZE / 2, PIT_DEPTH * BLOCK_SIZE / 2 );
			font.draw(spriteBatch, "Score: " + score, PIT_WIDTH * BLOCK_SIZE / 2, PIT_DEPTH * BLOCK_SIZE / 2 - 15);
			spriteBatch.end();
		}
	}
	
	
	private void gravity() {
		FallingPiece candidate = fallingPiece.makeCandidate();
		candidate.setLine(candidate.getLine() + 1);
		if (isValid(candidate)) {
			fallingPiece = candidate;
		}
		else {
			// piece becomes part of pit
			for (int pl = 0; pl < fallingPiece.getType().getBoundingBoxSize(); pl++) {
				for (int pc = 0; pc < fallingPiece.getType().getBoundingBoxSize(); pc++) {
					if (fallingPiece.getRendering()[pl][pc] == 1) {
						int pitLine = pitLine(fallingPiece, pl);
						int pitColumn = pitColumn(fallingPiece, pc);
						if ((pitLine >=0 ) && (pitLine < PIT_DEPTH) && (pitColumn >= 0) && (pitColumn < PIT_WIDTH)) {
							pit[pitLine][pitColumn] = fallingPiece.getType().getIndexInTexture();
						}
					}
				}
			}
			// clear full lines
			int checkedLine = PIT_DEPTH - 1;
			checkedLine: while (checkedLine >= 0) {
				for (int c = 0; c < PIT_WIDTH; c++) {
					if (pit[checkedLine][c] == -1) {
						checkedLine--;
						continue checkedLine;
					}
				}
				score++;
				speed = (int) Math.round(Settings.GRAVITY_MILLIS * Math.pow(0.9, (double) (score / 10)));
				for (int l = checkedLine; l > 0; l--) {
					for (int c = 0; c < PIT_WIDTH; c++) {
						pit[l][c] = pit[l-1][c];
					}
				}
				for (int c = 0; c < PIT_WIDTH; c++) {
					pit[0][c] = -1;
				}
			}
			// a new piece spawns
			spawnNewPiece();
		}
	}

	private void spawnNewPiece() {
		if (nextPieceType == null) {
			nextPieceType = PieceTypes.getRandom();
		}
		fallingPiece = new FallingPiece(nextPieceType, 0, 0, PIT_WIDTH/2 - 2);
		if (fallingPiece.getType().getClass().equals(O.class)) {
			fallingPiece.setColumn(fallingPiece.getColumn() + 1);
		}
		nextPieceType = PieceTypes.getRandom();
		if (! isValid(fallingPiece)) {
			gameState = GameState.GAMEOVER;
		}
	}
	
	public void movement(PlayerInput input) {
		if (input == PlayerInput.NONE) {
			return;
		}
		long now = System.currentTimeMillis();
		lastMovementMap.put(input, now);
		FallingPiece candidate = fallingPiece.makeCandidate();
		switch (input) {
			case MOVE_LEFT: {
				candidate.setColumn(candidate.getColumn() - 1);
				break;
			}
			case MOVE_RIGHT: {
				candidate.setColumn(candidate.getColumn() + 1);
				break;
			}
			case ROTATE_CW: {
				candidate.rotateCW();
				break;
			}
			case ROTATE_CCW: {
				candidate.rotateCCW();
				break;
			}
			case MOVE_DOWN: {
				candidate.setLine(candidate.getLine() + 1);
				break;
			}
			case DROP: {
				while (true) {
					candidate.setLine(candidate.getLine() + 1);
					if (! isValid(candidate)) {
						candidate.setLine(candidate.getLine() - 1);
						break;
					}
				}
				break;
			}
			default: {}
		}
		if (isValid(candidate)) {
			fallingPiece = candidate;
		}
	}

	private boolean isValid(FallingPiece candidate) {
	  	for (int gl = 0; gl < candidate.getType().getBoundingBoxSize(); gl++) {
			  for (int gc = 0; gc < candidate.getType().getBoundingBoxSize(); gc++) {
				  if (candidate.getRendering()[gl][gc] == 1) {
					  int pitLine = pitLine(candidate, gl);
					  int pitColumn = pitColumn(candidate, gc);
					  if ((pitLine < 0) || (pitLine >= PIT_DEPTH) || (pitColumn < 0) || (pitColumn >= PIT_WIDTH) || (pit[pitLine][pitColumn] != -1)) {
						  return false;
					  }
				  }
			  }
		}
		return true;
	}

	private int pitLine(FallingPiece piece, int pieceLine) {
		return piece.getLine() + pieceLine;
	}

	private int pitColumn(FallingPiece piece, int pieceColumn) {
		return piece.getColumn() + pieceColumn;
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}
	
	@Override
	public void dispose() {}
}
