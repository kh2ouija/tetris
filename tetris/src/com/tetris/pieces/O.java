package com.tetris.pieces;

public class O extends PieceType {

	@Override
	public int getBoundingBoxSize() {
		return 2;
	}

	@Override
	public int getIndexInTexture() {
		return 1;
	}

	@Override
	public int[][][] getRenderings() {
		return new int[][][] {
				new int[][] {
						new int[] { 1, 1},
						new int[] { 1, 1}
				},
				new int[][] {
						new int[] { 1, 1},
						new int[] { 1, 1}
				},
				new int[][] {
						new int[] { 1, 1},
						new int[] { 1, 1}
				},
				new int[][] {
						new int[] { 1, 1},
						new int[] { 1, 1}
				}				
		};
	}

}
