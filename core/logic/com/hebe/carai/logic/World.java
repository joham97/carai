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
import com.hebe.carai.logic.nn.Genom;
import com.hebe.carai.logic.nn.NeuralNetwork;

public class World {

	private float camX, camY;

	private List<Genom> genoms;
	private List<Wall> walls;

	private HUDText turn;
	private HUDText engine;
	private HUDText fitness;
	private HUDText generation;

	private int count = 128;

	private boolean out = false;

	private int generationCount = 1;

	public World(HUD hud) {
		this.turn = new HUDText(0, 1080, "Turn: ");
		this.engine = new HUDText(0, 1060, "Engine: ");
		this.fitness = new HUDText(0, 1040, "Fitness: ");
		this.generation = new HUDText(0, 1020, "Generation: ");

		hud.add(this.turn);
		hud.add(this.engine);
		hud.add(this.fitness);
		hud.add(this.generation);

		this.camX = 0;
		this.camY = 0;

		this.walls = new ArrayList<Wall>();

		for (int i = 0; i < 512; i++) {
			this.walls.add(new Wall(250f * i, 500f * (float) Math.sin((i / 4f) * Math.PI) - 350f,
									250f * (i + 1), 500f * (float) Math.sin(((i + 1) / 4f) * Math.PI) - 350f));
			this.walls.add(new Wall(250f * i, 500f * (float) Math.sin((i / 4f) * Math.PI) + 350f,
					250f * (i + 1), 500f * (float) Math.sin(((i + 1) / 4f) * Math.PI) + 350f));
		}

		this.walls.add(new Wall(-350f, 350f, 0f, 350f));
		this.walls.add(new Wall(-350f, -350f, 0f, -350f));
		this.walls.add(new Wall(-350f, 350f, -350f, -350f));

		this.genoms = new ArrayList<Genom>();
		for (int i = 0; i < 20; i++) {
			this.genoms.add(new Genom(new Car(0, 0, 120, 80, this.walls)));
		}
	}

	public void update(float delta) {
		for (Genom genom : this.genoms) {
			if (Gdx.input.isKeyPressed(Keys.A)) {
				genom.getCar().setTurn(1);
			} else if (Gdx.input.isKeyPressed(Keys.D)) {
				genom.getCar().setTurn(-1);
			}
			genom.update(delta);
			calcFitness(genom.getCar());
		}

		Genom maxGenom = null;
		Genom maxGenom2 = null;
		boolean allDead = true;
		for (Genom genom : this.genoms) {
			if (maxGenom == null || genom.getCar().getFitness() > maxGenom.getCar().getFitness()) {
				maxGenom = genom;
			}
			if (maxGenom2 == null && !genom.getCar().isDead()) {
				maxGenom2 = genom;
			}
			if (!genom.getCar().isDead()) {
				allDead = false;
				if (genom.getCar().getFitness() > maxGenom2.getCar().getFitness()) {
					maxGenom2 = genom;
				}
			}
		}
		if (allDead && !out) {
			nextGen(maxGenom);
			this.generationCount++;
		}

		if (maxGenom2 != null) {
			setCam(maxGenom2.getCar().getX(), maxGenom2.getCar().getY());

			this.turn.setText("Turn: " + maxGenom2.getCar().getTurn());
			this.engine.setText("Engine: " + maxGenom2.getCar().getEngine());
			this.fitness.setText("Fitness: " + maxGenom2.getCar().getFitness());
			this.generation.setText("Generation: " + generationCount);
		}

	}

	private void nextGen(Genom genom) {
		this.genoms.clear();
		this.genoms.add(new Genom(new Car(0, 0, 120, 80, this.walls), genom.getNeuralNetwork()));
		for (int i = 0; i < 9; i++) {
			NeuralNetwork bestNetwork = genom.getNeuralNetwork().deepCopy();
			bestNetwork.mutate(1 - genom.getCar().getFitness());
			this.genoms.add(new Genom(new Car(0, 0, 120, 80, this.walls), bestNetwork));
		}
		for (int i = 9; i < 20; i++) {
			this.genoms.add(new Genom(new Car(0, 0, 120, 80, this.walls)));
		}
	}

	private void calcFitness(Car car) {
		car.setFitness(car.getX() / (this.count * 500));
	}

	public void render(SpriteBatch batch, ShapeRenderer shape) {
		for (Wall wall : this.walls) {
			wall.render(shape);
		}

		for (Genom genom : this.genoms) {
			genom.getCar().render(shape);
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
