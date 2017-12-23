package com.hebe.carai.logic.nn;

public class NeuralNetwork {

	    public NeuralLayer[] layers;

	    /// <summary>
	    /// An array of unsigned integers representing the node count 
	    /// of each layer of the network from input to output layer.
	    /// </summary>
	    public int[] topology;

	    /// <summary>
	    /// The amount of overall weights of the connections of this network.
	    /// </summary>
	    public int weightCount;
	    /// <summary>
	    /// Initialises a new fully connected feedforward neural network with given topology.
	    /// </summary>
	    /// <param name="topology">An array of unsigned integers representing the node count of each layer from input to output layer.</param>
	    public NeuralNetwork(int[] topology)
	    {
	        this.topology = topology;

	        //Calculate overall weight count
	        this.weightCount = 0;
	        for (int i = 0; i < topology.length - 1; i++)
	            this.weightCount += (topology[i] + 1) * topology[i + 1]; // + 1 for bias node

	        //Initialise layers
	        this.layers = new NeuralLayer[topology.length - 1];
	        for (int i = 0; i<this.layers.length; i++){
	            this.layers[i] = new NeuralLayer(topology[i], topology[i + 1]);
	        }
	    }

	    /// <summary>
	    /// Processes the given inputs using the current network's weights.
	    /// </summary>
	    /// <param name="inputs">The inputs to be processed.</param>
	    /// <returns>The calculated outputs.</returns>
	    public double[] processInputs(double[] inputs)
	    {
	        //Check arguments
	        if (inputs.length != this.layers[0].neuronCount)
	        	System.err.println("Given inputs do not match network input amount.");

	        //Process inputs by propagating values through all layers
	        double[] outputs = inputs;
	        for (NeuralLayer layer : this.layers){
	            outputs = layer.processInputs(outputs);
	        }

	        return outputs;
	        
	    }

	    /// <summary>
	    /// Sets the weights of this network to random values in given range.
	    /// </summary>
	    /// <param name="minValue">The minimum value a weight may be set to.</param>
	    /// <param name="maxValue">The maximum value a weight may be set to.</param>
	    public void setRandomWeights(double minValue, double maxValue)
	    {
	        if (this.layers != null)
	        {
		        for (NeuralLayer layer : this.layers){
	                layer.setRandomWeights(minValue, maxValue);
		        }
	        }
	    }

	    /// <summary>
	    /// Returns a new NeuralNetwork instance with the same topology and 
	    /// activation functions, but the weights set to their default value.
	    /// </summary>
	    public NeuralNetwork getTopologyCopy()
	    {
	        return new NeuralNetwork(this.topology);
	    }

	    /// <summary>
	    /// Copies this NeuralNetwork including its topology and weights.
	    /// </summary>
	    /// <returns>A deep copy of this NeuralNetwork</returns>
	    public NeuralNetwork DeepCopy()
	    {
	        NeuralNetwork newNet = new NeuralNetwork(this.topology);
	        for (int i = 0; i < this.layers.length; i++)
	            newNet.layers[i] = this.layers[i].deepCopy();

	        return newNet;
	    }

	    /// <summary>
	    /// Returns a string representing this network in layer order.
	    /// </summary>
	    @Override
	    public String toString()
	    {
	        String output = "";

	        for (int i = 0; i<this.layers.length; i++)
	            output += "Layer " + i + ":\n" + this.layers[i].toString();

	        return output;
	    }
	
	
}
