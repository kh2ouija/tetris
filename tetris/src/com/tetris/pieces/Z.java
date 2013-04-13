package com.tetris.pieces;

public class Z extends PieceType {

	@Override
	public int getBoundingBoxSize() {
		return 3;
	}

	@Override
	public int getIndexInTexture() {
		return 4;
	}

	@Override
	public int[][][] getRenderings() {
		return new int[][][] {
				new int[][] {
						new int[] { 1, 1, 0},
						new int[] { 0, 1, 1},
						new int[] { 0, 0, 0}
				},
				new int[][] {
						new int[] { 0, 0, 1},
						new int[] { 0, 1, 1},
						new int[] { 0, 1, 0}
				},
				new int[][] {
						new int[] { 0, 0, 0},
						new int[] { 1, 1, 0},
						new int[] { 0, 1, 1}
				},
				new int[][] {
						new int[] { 0, 1, 0},
						new int[] { 1, 1, 0},
						new int[] { 1, 0, 0}
				}				
		};
	}

}
