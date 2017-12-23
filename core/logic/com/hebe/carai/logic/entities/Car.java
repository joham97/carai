package com.hebe.carai.logic.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.hebe.gameutils.collision.MyLine;
import com.hebe.gameutils.collision.MyVector;

public class Car {

	private float x;
	private float y;

	private int width;
	private int height;

	private float rotation = 0;
	private float engine = 1;
	private float turn = 0;
	private float fitness = 0;

	private float maxSpeed = 500;

	private int sensorWidth = 10;

	private List<Sensor> sensors;

	private List<Wall> walls;

	private boolean dead = false;

	public Car(float x, float y, int width, int height, List<Wall> walls) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.walls = walls;

		this.sensors = new ArrayList<Sensor>();
		for (int i = 0; i < 5; i++) {
			this.sensors.add(new Sensor());
		}
	}

	public void update(float delta) {
		if (!this.dead) {
			this.rotation += this.turn * delta;

			this.x += delta * this.engine * this.maxSpeed * Math.cos(this.rotation);
			this.y += delta * this.engine * this.maxSpeed * Math.sin(this.rotation);
						
			List<MyVector> vecs = getSensorPos();
			for (int i = 0; i < 5; i++) {
				this.sensors.get(i).set(vecs.get(i).x, vecs.get(i).y, this.x, this.y, this.walls);
			}
			
			//Is it now dead?
			List<MyVector> cs = getCorners();
			for(Wall wall : this.walls){
				for (int i = 0; i < cs.size() - 1; i++) {
					if(checkCollision(cs.get(i).x, cs.get(i).y, cs.get(i + 1).x, cs.get(i + 1).y, wall)){
						this.dead = true;
					}
				}
				if(checkCollision(cs.get(cs.size() - 1).x, cs.get(cs.size() - 1).y, cs.get(0).x, cs.get(0).y, wall)){
					this.dead = true;
				}
			}
		}
	}
	
	private boolean checkCollision(float x1, float y1, float x2, float y2, Wall wall){
		return new MyLine(x1, y1, x2, y2).collides(wall) != null;
	}

	public void render(ShapeRenderer shape) {
		for (Sensor sensor : this.sensors) {
			sensor.render(shape);
		}

		shape.begin(ShapeType.Filled);
		shape.setColor(Color.WHITE);

		List<MyVector> cs = getCorners();
		for (int i = 0; i < cs.size() - 1; i++) {
			shape.rectLine(cs.get(i).x, cs.get(i).y, cs.get(i + 1).x, cs.get(i + 1).y, 5);
		}
		shape.rectLine(cs.get(cs.size() - 1).x, cs.get(cs.size() - 1).y, cs.get(0).x, cs.get(0).y, 5);

		cs = getSensorPos();
		shape.setColor(Color.RED);
		for (int i = 0; i < cs.size(); i++) {
			shape.circle(cs.get(i).x, cs.get(i).y, this.sensorWidth);
		}
		shape.end();
	}

	public List<MyVector> getCorners() {
		List<MyVector> c = new ArrayList<MyVector>();

		MyVector pos = new MyVector(this.x, this.y);
		MyVector dir = new MyVector((float) Math.cos(this.rotation), (float) Math.sin(this.rotation)).unit();

		// Front
		c.add(pos.add(dir.scl(this.width / 2)).add(dir.orth().scl(this.height / 2)));
		c.add(pos.add(dir.scl(this.width / 2)).sub(dir.orth().scl(this.height / 2)));

		// Hinten
		c.add(pos.sub(dir.scl(this.width / 2)).sub(dir.orth().scl(this.height / 2)));
		c.add(pos.sub(dir.scl(this.width / 2)).add(dir.orth().scl(this.height / 2)));

		return c;
	}

	public List<MyVector> getSensorPos() {
		List<MyVector> c = new ArrayList<MyVector>();

		MyVector pos = new MyVector(this.x, this.y);
		MyVector dir = new MyVector((float) Math.cos(this.rotation), (float) Math.sin(this.rotation)).unit();

		// Front
		c.add(pos.add(dir.scl(this.width * 3f / 8f)));
		c.add(pos.add(dir.scl(this.width * 3f / 8f)).add(dir.orth().scl(this.height / 3f)));
		c.add(pos.add(dir.scl(this.width * 3f / 8f)).sub(dir.orth().scl(this.height / 3f)));
		c.add(pos.add(dir.scl(this.width * 3f / 8f - this.height / 3f)).add(dir.orth().scl(this.height / 3f)));
		c.add(pos.add(dir.scl(this.width * 3f / 8f - this.height / 3f)).sub(dir.orth().scl(this.height / 3f)));

		return c;
	}

	public float getSensorValue(int i) {
		return this.sensors.get(i).getDist();
	}
	
	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public void setTurn(float turn) {
		this.turn = 2f*turn-1f;
	}

	public void setEngine(float engine) {
		this.engine = engine;

		if (this.engine > 1) {
			this.engine = 1;
		} else if (this.engine < 0) {
			this.engine = 0;
		}
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public float getFitness() {
		return this.fitness;
	}

	public float getEngine() {
		return this.engine;
	}

	public float getTurn() {
		return this.turn;
	}
	
	public boolean isDead() {
		return dead;
	}
}
