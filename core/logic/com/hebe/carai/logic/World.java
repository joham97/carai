package com.hebe.carai.logic;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hebe.carai.hud.HUD;
import com.hebe.carai.hud.components.HUDText;
import com.hebe.carai.logic.entities.Car;
import com.hebe.carai.logic.entities.Wall;

public class World {

	private float camX, camY;

	private List<Car> cars;
	private List<Wall> walls;

	private HUDText turn;
	private HUDText engine;
	private HUDText fitness;

	private int count = 128;

	public World(HUD hud) {
		this.turn = new HUDText(0, 1080, "Turn: ");
		this.engine = new HUDText(0, 1060, "Engine: ");
		this.fitness = new HUDText(0, 1040, "Fitness: ");

		hud.add(this.turn);
		hud.add(this.engine);
		hud.add(this.fitness);

		this.camX = 0;
		this.camY = 0;

		this.walls = new ArrayList<Wall>();

		for (float i = 0; i < this.count + 1; i++) {
			this.walls.add(new Wall(i * 500f, (float) Math.sin(Math.PI * (2 * this.count) / i) * 500f + 350f, (i + 1) * 500f, (float) Math.sin(Math.PI * (2 * this.count) / (i + 1)) * 500f + 350f));
			this.walls.add(new Wall(i * 500f, (float) Math.sin(Math.PI * (2 * this.count) / i) * 500f - 350f, (i + 1) * 500f, (float) Math.sin(Math.PI * (2 * this.count) / (i + 1)) * 500f - 350f));
		}

		this.walls.add(new Wall(-350f, 350f, 500f, 350f));
		this.walls.add(new Wall(-350f, -350f, 500f, -350f));
		this.walls.add(new Wall(-350f, 350f, -350f, -350f));
		
		
		this.cars = new ArrayList<Car>();
		this.cars.add(new Car(0, 0, 120, 80, this.walls));
	}

	public void update(float delta) {
		for (Car car : this.cars) {
			if(Gdx.input.isKeyPressed(Keys.A)){
				car.setTurn(1);
			}else if(Gdx.input.isKeyPressed(Keys.D)){
				car.setTurn(-1);
			} else {
				car.setTurn(0);
			}
			car.update(delta);
			calcFitness(car);
		}

		Car maxCar = null;
		for (Car car : this.cars) {
			if (maxCar == null || car.getFitness() > maxCar.getFitness()) {
				maxCar = car;
			}
		}

		setCam(maxCar.getX(), maxCar.getY());

		this.turn.setText("Turn: " + maxCar.getTurn());
		this.engine.setText("Engine: " + maxCar.getEngine());
		this.fitness.setText("Fitness: " + maxCar.getFitness());

	}

	private void calcFitness(Car car) {
		car.setFitness(car.getX() / (this.count * 500));
	}

	public void render(SpriteBatch batch, ShapeRenderer shape) {
		for (Wall wall : this.walls) {
			wall.render(shape);
		}
		
		for (Car car : this.cars) {
			car.render(shape);
		}
	}

	public void setCam(float x, float y) {
		this.camX = x;
		this.camY = y;
	}

	public float getCamX() {
		return this.camX;
	}

	public float getCamY() {
		return this.camY;
	}
}
