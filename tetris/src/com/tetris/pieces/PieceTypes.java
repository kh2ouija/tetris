package com.tetris.pieces;

import java.util.Random;

public class PieceTypes {
	
	private final static Random r = new Random();
	
	private final static PieceType[] types = new PieceType[] {
		new I(), new J(), new L(), new O(), new S(), new T(), new Z()
	};
	
	public final static PieceType get(int type) {
		return types[type];
	}
	
	public final static PieceType getRandom() {
		return get(r.nextInt(7));
	}

}
