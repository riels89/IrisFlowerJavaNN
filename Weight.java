package neuralNetwork;

public class Weight {
	private double weight;
	private double change;
	/**
	 * creates a weight with the given starting value
	 * @param weight
	 */
	public Weight(double weight)
	{
		this.weight = weight;
	}
	/**
	 * Sets the delta weight but does not actually change the weight
	 * @param change
	 */
	public void setChange(double change)
	{
		this.change = change;
	}
	/**
	 * returns the weight's value
	 * @return The weight's value
	 */
	public double getWeight()
	{
		return weight;
	}
	/**
	 * subtracts the deltaWeight from the weight
	 * @param deltaWeight
	 */
	public void changeWeight()
	{
		weight -= change;
	}
}
