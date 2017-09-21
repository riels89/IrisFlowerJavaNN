package neuralNetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.text.DecimalFormat;

public class Neuron {
	private double netInput;
	private double output;
	private Weight[] weights;
	private int bias;
	private ActivationFunctions AF;
	private double deltaOutput;
	/**
	 * creates a neuron with the given weights and bias
	 * @param weights The weights
	 * @param bias The bias
	 */
	public Neuron(Weight[] weights, int bias)
	{
		this.weights = weights;
		this.bias = bias;
	}
	/**
	 * applies the activation function to the neuron
	 * @param inputs the inputs
	 * @return the output
	 */
	public double activate(double[] inputs)
	{
		netInput = dot(inputs, weights);
		switch(AF)
		{
			case  SIGMOID:
				output = sigmoid(netInput);
				break;
			case ReLU:
				output = Math.max(0, netInput);
				break;
		}
		deltaOutput = output * (1-output);
		return output;
	}
	/**
	 * Sets the activation function
	 * @param AF The activation function
	 */
	public void setActivationFunction(ActivationFunctions AF)
	{
		this.AF = AF;
	}
	/**
	 * returns the netInput
	 * @return The netInput
	 */
	public double getNetInput()
	{
		return netInput;
	}
	/**
	 * returns the output
	 * @return The output
	 */
	public double getOutput()
	{
		return output;
	}
	/**
	 * sigmoid function
	 * @param x
	 * @return
	 */
	private double sigmoid(double x)
	{
		return 1/ (1 + Math.exp(-x));
	}
	public double getDeltaOutput()
	{
		return deltaOutput;
	}
	/**
	 * dot function
	 * @param inputs
	 * @param weights
	 * @return
	 */
	private double dot(double[] inputs, Weight[] weights)
	{
		double sum = 0;
		for(int i=0;i<inputs.length;i++)
		{
			sum =+ inputs[i] * weights[i].getWeight();
		}
		//adds bias
		sum += bias * weights[weights.length-1].getWeight();
		return sum;
	}
	/**
	 * Draws the neuron to the given graphics object
	 * @param g The graphics object
	 * @param diameter The diameter of the neuron
	 * @param x The x of the neuron
	 * @param y The y of the neuron
	 */
	public void draw(Graphics2D g, int diameter, int x, int y)
	{
		DecimalFormat fmt = new DecimalFormat("#.#####");

		g.setStroke(new BasicStroke(3));
		g.setColor(Color.BLACK);
		g.drawOval(x, y, diameter, diameter);
		g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
		g.drawString(fmt.format(output), x+diameter/4-10, y+diameter/2+5);
	}
}
