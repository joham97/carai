package com.hebe.carai.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hebe.carai.hud.HUD;
import com.hebe.carai.hud.components.HUDText;
import com.hebe.carai.logic.entities.Car;
import com.hebe.carai.logic.entities.Wall;
import com.hebe.carai.logic.nn.Genom;
import com.hebe.carai.logic.nn.NNComponent;
import com.hebe.carai.logic.nn.NeuralNetwork;

public class World {

	private float camX, camY;

	private List<Genom> genoms;
	private List<Wall> walls;

	private HUDText turn;
	private HUDText engine;
	private HUDText fitness;
	private HUDText generation;
	private NNComponent nnComponent;

	private int count = 512;

	private boolean out = false;

	private int generationCount = 1;

	public World(HUD hud) {
		this.turn = new HUDText(0, 1080, "Turn: ");
		this.engine = new HUDText(0, 1060, "Engine: ");
		this.fitness = new HUDText(0, 1040, "Fitness: ");
		this.generation = new HUDText(0, 1020, "Generation: ");
		
		this.nnComponent = new NNComponent(0, 0);

		hud.add(this.turn);
		hud.add(this.engine);
		hud.add(this.fitness);
		hud.add(this.generation);
		hud.add(this.nnComponent);

		this.camX = 0;
		this.camY = 0;

		reset();
	}
	
	public void reset(){
		this.walls = new ArrayList<Wall>();

		int y = 0;
		int variation = 250;
		Random rand = new Random();
		for (int i = 0; i < this.count; i++) {
			int nY = (int) (y + rand.nextDouble() * (2*variation) - variation);
			this.walls.add(new Wall(250f * i, y - 250f, 250f * (i + 1), nY - 250f));
			this.walls.add(new Wall(250f * i, y + 250f, 250f * (i + 1), nY + 250f));
			y = nY;
		}

		this.walls.add(new Wall(-250f, 250f, 0f, 250f));
		this.walls.add(new Wall(-250f, -250f, 0f, -250f));
		this.walls.add(new Wall(-250f, 250f, -250f, -250f));
		this.walls.add(new Wall(250f * this.count, y+250f, 250f * this.count, y-250f));

		this.genoms = new ArrayList<Genom>();
		for (int i = 0; i < 20; i++) {
			Genom genom = new Genom(new Car(0, 0, 120, 80, this.walls));
			genom.setInitalGeneration(this.generationCount);
			this.genoms.add(genom);
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
		if (allDead && !this.out) {
			this.generationCount++;
			nextGen(maxGenom);
		}

		if (maxGenom2 != null) {
			setCam(maxGenom2.getCar().getX(), maxGenom2.getCar().getY());

			this.turn.setText("Turn: " + maxGenom2.getCar().getTurn());
			this.engine.setText("Engine: " + maxGenom2.getCar().getEngine());
			this.fitness.setText("Fitness: " + maxGenom2.getCar().getFitness());
			if(this.generationCount == maxGenom2.getInitalGeneration()) {
				this.generation.setText("Generation: " + this.generationCount);
			}else {
				this.generation.setText("Generation: " + this.generationCount + " from " + maxGenom2.getInitalGeneration());
			}
			
			this.nnComponent.setNn(maxGenom.getNeuralNetwork());
			
			if(Gdx.input.isKeyJustPressed(Keys.P)) {
				exportNN(maxGenom2);
			}
		}

		if(Gdx.input.isKeyJustPressed(Keys.I)) {
			importNN();
		}
		if(Gdx.input.isKeyJustPressed(Keys.R)) {
			reset();
		}
	}

	private void nextGen(Genom genom) {
		this.genoms.clear();
		Genom bestNeuralNetwork = new Genom(new Car(0, 0, 120, 80, this.walls), genom.getNeuralNetwork());
		bestNeuralNetwork.setInitalGeneration(genom.getInitalGeneration());
		this.genoms.add(bestNeuralNetwork);
		for (int i = 0; i < 9; i++) {
			NeuralNetwork bestNetwork = genom.getNeuralNetwork().deepCopy();
			bestNetwork.mutate(1 - genom.getCar().getFitness());
			Genom newGenom = new Genom(new Car(0, 0, 120, 80, this.walls), bestNetwork);
			newGenom.setInitalGeneration(this.generationCount);
			this.genoms.add(newGenom);
		}
		for (int i = 9; i < 20; i++) {
			Genom newGenom = new Genom(new Car(0, 0, 120, 80, this.walls));
			newGenom.setInitalGeneration(this.generationCount);
			this.genoms.add(newGenom);
		}
	}

	private void calcFitness(Car car) {
		car.setFitness(car.getX() / (this.count * 250));
	}

	public void render(SpriteBatch batch, ShapeRenderer shape) {
		for (Wall wall : this.walls) {
			wall.render(shape);
		}

		for (Genom genom : this.genoms) {
			genom.getCar().render(shape);
		}
	}
	
	private void importNN() {
		try {
			FileHandle file = Gdx.files.local("nn.dat");
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.file()));
			NeuralNetwork imported = (NeuralNetwork) ois.readObject();
			ois.close();
			
			this.generationCount = 1;
			
			Genom genom = new Genom(new Car(0, 0, 120, 80, this.walls), imported);
			genom.setInitalGeneration(1);

			this.genoms.clear();
			this.genoms.add(genom);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private void exportNN(Genom genom) {
		try {
			FileHandle file = Gdx.files.local("nn.dat");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file.file()));
			oos.writeObject(genom.getNeuralNetwork());
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
