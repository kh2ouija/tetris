package com.tetris;

import com.tetris.pieces.PieceType;

public class FallingPiece {
		
	private PieceType type;
	private int rotation;
	private int column;
	private int line;
	
	public FallingPiece(PieceType type, int rotation, int line, int column) {
		this.type = type;
		this.rotation = rotation;
		this.line = line;
		this.column = column;		
	}
	
	public void rotateCW() {
		if (rotation == 3) {
			rotation = 0;
		}
		else {
			rotation++;
		}
	}
	
	public void rotateCCW() {
		if (rotation == 0) {
			rotation = 3;
		}
		else {
			rotation--;
		}
	}
	
	public int[][] getRendering() {
		return type.getRenderings()[rotation];
	}
	
	public int getBottomRelativeLine() {
		for (int line = getType().getBoundingBoxSize() - 1; line >=0 ; line--) {
			for (int column = 0; column < getType().getBoundingBoxSize(); column++) {
				if (getRendering()[line][column] != 0) {
					return line;
				}
			}
		}
		return -1;
	}

	public PieceType getType() {
		return type;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public FallingPiece makeCandidate() {
		return new FallingPiece(type, rotation, line, column);
	}
	
	@Override
	public String toString() {
		return type.getClass().toString() + "@" + line + "," + column;
	}

}
