package com.hebe.carai.logic.entities;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.hebe.gameutils.collision.MyLine;
import com.hebe.gameutils.collision.MyVector;

public class Sensor {
	
	private float x, y, endX, endY, dist;
	
	public void set(float x, float y, float cX, float cY, List<Wall> walls) {
		this.x = x;
		this.y = y;
		MyVector dir = new MyVector(x - cX, y - cY).unit();
		MyLine line = new MyLine(x, y, x + dir.x * 2000, y + dir.y * 2000);
		
		this.dist = 2000;
		for(Wall wall : walls){
			MyVector vec = wall.collides(line);
			if(vec != null){
				float dst = vec.dst(new MyVector(x, y));
				if(dst < this.dist){
					this.dist = dst;
				}
			}
		}
		
		MyVector end = new MyVector(x, y).add(dir.scl(this.dist));
		this.endX = end.x;
		this.endY = end.y;
	}
	
	public void render(ShapeRenderer shape) {
		shape.begin(ShapeType.Line);
		
		shape.setColor(Color.RED);
		shape.line(this.x, this.y, this.endX, this.endY);
		
		shape.end();
		

		shape.begin(ShapeType.Filled);
		
		shape.circle(this.endX, this.endY, 5);
		
		shape.end();
	}
	
	public float getDist() {
		return dist;
	}
}
