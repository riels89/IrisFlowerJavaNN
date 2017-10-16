package neuralNetwork;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {
	
	public static final int BIAS = 1;
	public static final int INPUT_NEURONS = 4;
	public static final int HIDDEN_NEURONS = 4;
	public static final int OUTPUT_NEURONS = 3;

	
	private double learningRate = 0.01;
	private int dataIndex = 0;
	private List<Boolean> last200 = new ArrayList<Boolean>();
	private List<Boolean> testingCorrect = new ArrayList<Boolean>();

	private double[][] trainingData;
	private double[][] testingData;
	private int[][] trainingAnswers;
	private int[][] testingAnswers;
	
	private double[] currentInput;
	/**
	 * Weights are used per output NOT per input  
	 */
	private Weight[][] inputWeights = new Weight[HIDDEN_NEURONS][INPUT_NEURONS+1];
	private Weight[][] outputWeights = new Weight[OUTPUT_NEURONS][HIDDEN_NEURONS+1];
	private Neuron[] hiddenNeurons = new Neuron[HIDDEN_NEURONS];
	private Neuron[] outputNeurons = new Neuron[OUTPUT_NEURONS];
	
	/**
	 * creates a Neural Network which uses the given data
	 * @param trainingData The data used to train the network	
	 * @param trainingAnswers The data used to correct the network
	 * @param testingData the data used to predict new data on the network
	 * @param testingAnswers the data used to check the new predictions
	 */
	public NeuralNetwork(double[][] trainingData, int[][] trainingAnswers, double[][] testingData, int[][] testingAnswers)
	{
		this.trainingData = trainingData;
		this.trainingAnswers = trainingAnswers;
		this.testingData = testingData;
		this.testingAnswers = testingAnswers;
		currentInput = trainingData[dataIndex];

		fillArrays();
	}
	/**
	 * Iterates through one training operation
	 */
	public void trainOne()
	{
		currentInput = trainingData[dataIndex];
		forwardPropagate();
		backPropagate();
		
		last200.add(wasRight(trainingAnswers[dataIndex]));
		if (last200.size() > 200)
			last200.remove(0);
		
		dataIndex++;
		if(dataIndex>=trainingData.length)
			dataIndex=0;
	}
	/**
	 * checks if a given answer was correct
	 * @param answers the answers to check with
	 * @return True if it was correct, False otherwise
	 */
	private boolean wasRight(int[] answers)
	{
		Neuron biggest;
		int index = -1;
		for(int i=0;i<answers.length;i++)
		{
			if(answers[i]==1)
				index=i;
		}
		biggest = outputNeurons[index];
		for(Neuron neuron:outputNeurons)
		{
			if(neuron.getOutput()>biggest.getOutput())
				biggest = neuron;
		}
		return biggest == outputNeurons[index];
		
	}
	/**
	 * Iterates through the testing data
	 */
	public void test()
	{
		if (testingCorrect.size() >= testingAnswers.length)
			testingCorrect.clear();
		for (int i = 0; i < testingData.length; i++)
		{
			currentInput = testingData[i];
			forwardPropagate();
			testingCorrect.add(wasRight(testingAnswers[i]));
		}
	}
	/**
	 * Updates the weights to attempt to minimize the error function
	 */
	private void backPropagate()
	{
		outputLayerBProp();
		inputLayerBProp();
		for(Weight[] weights:inputWeights)
		{
			for(Weight weight:weights)
			{
				weight.changeWeight();
			}
		}
		for(Weight[] weights:outputWeights)
		{
			for(Weight weight:weights)
			{
				weight.changeWeight();
			}
		}
	}
	/**
	 * calculates the partial derivative of the total error with respect to the output neuron
	 */
	private void inputLayerBProp()
	{
		double deltaHiddenOutput;
		for(int i=0;i<HIDDEN_NEURONS;i++)
		{
			deltaHiddenOutput = hiddenNeurons[i].getDeltaOutput();
			for(int j=0;j<INPUT_NEURONS;j++)
			{
				inputWeights[i][j].setChange( learningRate * deltaHiddenOutput * deltaTError(i) * currentInput[j]);
			}
			inputWeights[i][INPUT_NEURONS].setChange(learningRate * deltaHiddenOutput * deltaTError(i));
		}
		
	}
	/**
	 * Calculates the error for the input weights to use
	 * @param indexHidden the hidden neuron that the back propagation is on
	 * @return
	 */
	private double deltaTError(int indexHidden)
	{
		double error = 0;
		double deltaOutput;
		double deltaNetError;
		double deltaTError=0;
		
		for(int i=0;i<OUTPUT_NEURONS;i++)
		{
			error = outputNeurons[i].getOutput() - trainingAnswers[dataIndex][i];
			deltaOutput = outputNeurons[i].getDeltaOutput();
			deltaNetError = deltaOutput * error;
			deltaTError += deltaNetError * outputWeights[i][indexHidden].getWeight();
		}
		return deltaTError;
	}
	/**
	 * calculates the partial derivative of the total error with respect to the output neuron
	 * @param dataIndex
	 */
	private void outputLayerBProp()
	{
		double error;
		double deltaOutput;
		double deltaWeight;
		
		for(int i=0;i<OUTPUT_NEURONS;i++)
		{
			error =  outputNeurons[i].getOutput() - trainingAnswers[dataIndex][i];
			deltaOutput = outputNeurons[i].getDeltaOutput();
			
			for(int j=0;j<HIDDEN_NEURONS;j++)
			{				
				deltaWeight = error * deltaOutput * hiddenNeurons[j].getOutput();
				outputWeights[i][j].setChange(learningRate * deltaWeight);
			}
			outputWeights[i][HIDDEN_NEURONS].setChange(learningRate * error * deltaOutput); 
		}
		
	}
	/**
	 * Predicts the output on the current input data
	 */
	private void forwardPropagate()
	{
		double[] hiddenOutputs = new double[hiddenNeurons.length];
		for(int i=0;i<hiddenNeurons.length;i++)
		{
			hiddenOutputs[i] = hiddenNeurons[i].activate(currentInput);
		}
		for(Neuron neuron:outputNeurons)
		{
			//System.out.println("Output: " + neuron.calculate(hiddenOutputs));
			neuron.activate(hiddenOutputs);
		}
	}
	/**
	 * Fills the weight and neuron arrays.
	 * When filling weight arrays it puts in random values between [-1,1]
	 * 
	 */
	private void fillArrays()
	{
		for(int i=0;i<inputWeights.length;i++)
		{
			for(int j=0;j<inputWeights[i].length;j++)
			inputWeights[i][j] = new Weight(0.01 * Math.random());
		}
		for(int i=0;i<outputWeights.length;i++)
		{
			for(int j=0;j<outputWeights[i].length;j++)
			outputWeights[i][j] = new Weight(0.01 * Math.random());
		}
		//fills input neurons
		for(int i=0;i<hiddenNeurons.length;i++)
		{
			hiddenNeurons[i] = new Neuron(inputWeights[i], BIAS);
			hiddenNeurons[i].setActivationFunction(ActivationFunctions.ReLU);
		}
		//fills output neurons
		for(int i=0;i<outputNeurons.length;i++)
		{
			outputNeurons[i] = new Neuron(outputWeights[i], BIAS);
			outputNeurons[i].setActivationFunction(ActivationFunctions.SIGMOID);
		}
	}
	/**
	 * prints the current weights
	 */
	public void printWeights()
	{
		for(Weight[] weights:outputWeights)
		{
			for(Weight weight:weights)
				System.out.println(weight.getWeight());
		}
		for(Weight[] weights:inputWeights)
		{
			for(Weight weight:weights)
				System.out.println(weight.getWeight());
		}
	}
	/**
	 * returns the input weights
	 * @return The input weights
	 */
	public Weight[][] getInputWeights()
	{
		return inputWeights;
	}
	/**
	 * Returns the current output weights
	 * @return The output Weights
	 */
	public Weight[][] getOutputWeights()
	{
		return outputWeights;
	}
	/**
	 * Returns the hidden Neurons
	 * @return the hidden Neurons
	 */
	public Neuron[] getHiddenNeurons()
	{
		return hiddenNeurons;
	}
	/**
	 * Returns the output neurons
	 * @return The output neurons
	 */
	public Neuron[] getOutputNeurons()
	{
		return outputNeurons;
	}
	/**
	 * Returns the current input data
	 * @return The current input data
	 */
	public double[] getCurrentInput()
	{
		return currentInput;
	}
	/**
	 * Returns the percentage of how many of the last 200 training iterations were correct
	 * @return The percentage correct
	 */
	public String percentOf200Correct()
	{
		DecimalFormat fmt = new DecimalFormat("%##.##");
		int amountRight=0;
		for(boolean answer:last200)
		{
			if(answer)
				amountRight++;
		}
		return fmt.format(amountRight/(double)(last200.size()));
	}
	/**
	 * Returns the percentage of the testing data correct
	 * @return The pecentage correct
	 */
	public String percentOfTestCorrect()
	{
		DecimalFormat fmt = new DecimalFormat("%##.##");
		int amountRight=0;
		for(boolean answer:testingCorrect)
		{
			if(answer)
				amountRight++;
		}
		return fmt.format(amountRight/(double)(testingCorrect.size()));
	}
	/**
	 * Trains the network for a certain number of epochs
	 * @param epochs The amount of times the network will go thorugh the data
	 */
	public void train(int epochs)
	{
		for(int e=0;e<epochs;e++)
		{
			for(int i=0;i<trainingData.length;i++)
			{
				trainOne();
			}
		}
	}
}
