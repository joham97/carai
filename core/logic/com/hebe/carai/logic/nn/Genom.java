package com.hebe.carai.logic.nn;

import com.hebe.carai.logic.entities.Car;

public class Genom {

	private Car car;
	private NeuralNetwork neuralNetwork;
	
	public Genom(Car car) {
		this.car = car;
		this.neuralNetwork = new NeuralNetwork(new int[] { 5, 4, 3, 1 });
		this.neuralNetwork.setRandomWeights(-30, 30);
	}

	public Genom(Car car, NeuralNetwork neuralNetwork) {
		this.car = car;
		this.neuralNetwork = neuralNetwork;
	}

	public void update(float delta) {
		this.car.update(delta);
		double[] inputs = new double[5];
		for (int i = 0; i < 5; i++) {
			inputs[i] = this.car.getSensorValue(i);
		}

		double[] outputs = this.neuralNetwork.processInputs(inputs);
		this.car.setTurn((float) outputs[0]);
		//this.car.setEngine((float) outputs[0]);		
	}

	public Car getCar() {
		return car;
	}
	
	public NeuralNetwork getNeuralNetwork() {
		return neuralNetwork;
	}
	
}
