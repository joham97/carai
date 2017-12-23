package com.hebe.carai.logic.nn;

import java.util.Random;

public class NeuralLayer {

	private static Random randomizer = new Random();

	/// <summary>
	/// The amount of neurons in this layer.
	/// </summary>
	public int neuronCount;

	/// <summary>
	/// The amount of neurons this layer is connected to, i.e., the amount of neurons of the next layer.
	/// </summary>
	public int outputCount;

	/// <summary>
	/// The weights of the connections of this layer to the next layer.
	/// E.g., weight [i, j] is the weight of the connection from the i-th weight
	/// of this layer to the j-th weight of the next layer.
	/// </summary>
	public double[][] weights;

	/// <summary>
	/// Initialises a new neural layer for a fully connected feedforward neural network with given
	/// amount of node and with connections to the given amount of nodes of the next layer.
	/// </summary>
	/// <param name="nodeCount">The amount of nodes in this layer.</param>
	/// <param name="outputCount">The amount of nodes in the next layer.</param>
	/// <remarks>All weights of the connections from this layer to the next are initialised with the default double value.</remarks>
	public NeuralLayer(int nodeCount, int outputCount) {
		this.neuronCount = nodeCount;
		this.outputCount = outputCount;

		this.weights = new double[nodeCount + 1][outputCount]; // + 1 for bias node
	}

	/// <summary>
	/// Sets the weights of this layer to the given values.
	/// </summary>
	/// <param name="weights">
	/// The values to set the weights of the connections from this layer to the next to.
	/// </param>
	/// <remarks>
	/// The values are ordered in neuron order. E.g., in a layer with two neurons with a next layer of three neurons
	/// the values [0-2] are the weights from neuron 0 of this layer to neurons 0-2 of the next layer respectively and
	/// the values [3-5] are the weights from neuron 1 of this layer to neurons 0-2 of the next layer respectively.
	/// </remarks>
	public void setWeights(double[] weights) {
		// Check arguments
		if (weights.length != this.weights.length)
			System.err.println("Input weights do not match layer weight count.");

		// Copy weights from given value array
		int k = 0;
		for (int i = 0; i < this.weights.length; i++)
			for (int j = 0; j < this.weights[i].length; j++)
				this.weights[i][j] = weights[k++];
	}

	/// <summary>
	/// Processes the given inputs using the current weights to the next layer.
	/// </summary>
	/// <param name="inputs">The inputs to be processed.</param>
	/// <returns>The calculated outputs.</returns>
	public double[] processInputs(double[] inputs) {
		// Check arguments
		if (inputs.length != this.neuronCount)
			System.err.println("Given xValues do not match layer input count.");

		// Calculate sum for each neuron from weighted inputs and bias
		double[] sums = new double[this.outputCount];
		// Add bias (always on) neuron to inputs
		double[] biasedInputs = new double[this.neuronCount + 1];
		for (int i = 0; i < inputs.length; i++) {
			biasedInputs[i] = inputs[i];
		}
		biasedInputs[inputs.length] = 1.0;

		for (int j = 0; j < this.weights.length; j++)
			for (int i = 0; i < this.weights[j].length; i++)
				sums[j] += biasedInputs[i] * this.weights[i][j];

		for (int i = 0; i < sums.length; i++)
			sums[i] = sigmoid(sums[i]);

		return sums;
	}

	/// <summary>
	/// Copies this NeuralLayer including its weights.
	/// </summary>
	/// <returns>A deep copy of this NeuralLayer</returns>
	public NeuralLayer deepCopy() {
		// Copy weights
		double[][] copiedWeights = new double[this.weights.length][];
		for (int i = 0; i < this.weights.length; i++) {
			copiedWeights[i] = new double[this.weights[i].length];
			for (int j = 0; j < this.weights[i].length; j++)
				copiedWeights[i][j] = this.weights[i][j];
		}

		// Create copy
		NeuralLayer newLayer = new NeuralLayer(this.neuronCount, this.outputCount);
		newLayer.weights = copiedWeights;

		return newLayer;
	}

	/// <summary>
	/// Sets the weights of the connection from this layer to the next to random values in given range.
	/// </summary>
	/// <param name="minValue">The minimum value a weight may be set to.</param>
	/// <param name="maxValue">The maximum value a weight may be set to.</param>
	public void setRandomWeights(double minValue, double maxValue) {
		double range = Math.abs(minValue - maxValue);
		for (int i = 0; i < this.weights.length; i++)
			for (int j = 0; j < this.weights[i].length; j++)
				this.weights[i][j] = minValue + (randomizer.nextDouble() * range); // random double between minValue and maxValue
	}

	/// <summary>
	/// Returns a string representing this layer's connection weights.
	/// </summary>
	@Override
	public String toString() {
		String output = "";

		for (int i = 0; i < this.weights.length; i++) {
			for (int j = 0; j < this.weights[i].length; j++) {
				output += "[" + i + "," + j + "]: " + this.weights[i][j];
			}
			output += "\n";
		}

		return output;
	}

	public double sigmoid(double value) {
		return (1 / (1 + Math.pow(Math.E, (-1 * value))));
	}

}
