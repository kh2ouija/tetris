package com.tetris.pieces;

public abstract class PieceType {

	public abstract int[][][] getRenderings();
	public abstract int getBoundingBoxSize();
	public abstract int getIndexInTexture();
		
}
