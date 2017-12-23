package com.hebe.carai.logic.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.hebe.gameutils.collision.MyLine;

public class Wall extends MyLine{

	public Wall(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public void render(ShapeRenderer shape) {
		shape.begin(ShapeType.Filled);
		shape.setColor(Color.WHITE);

		shape.rectLine(this.x1, this.y1, this.x2, this.y2, 5);
		
		shape.end();
	}
	
}
