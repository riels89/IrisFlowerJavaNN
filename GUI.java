package neuralNetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JComponent;

@SuppressWarnings("serial")
public class GUI extends JComponent implements Runnable
{
	public static final int WIDTH  = 700;
	public static final int HEIGHT = 700; 
	private NeuralNetwork nn;
	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private Graphics2D g = image.createGraphics();
	
	private static final int DIAMETER =100;
	private static final int STARTING_Y =60;
	/**
	 * creates a GUI that is runnable on a thread
	 * @param nn
	 */
	public GUI(NeuralNetwork nn)
	{
		this.nn = nn;	
	}
	/**
	 * draws the neurons to the global graphics object
	 */
	private void drawNeurons()
	{
		Neuron[] hidden = nn.getHiddenNeurons();
		Neuron[] output = nn.getOutputNeurons();
		for(int i=0;i<hidden.length;i++)
		{
			hidden[i].draw(g, DIAMETER, 300, STARTING_Y + i*(DIAMETER + DIAMETER/10));
		}
		for(int i=0;i<output.length;i++)
		{
			output[i].draw(g, DIAMETER, 500, STARTING_Y + i*(DIAMETER + DIAMETER/10));
		}
		
		//draw inputs
		int x=100;
		int y=STARTING_Y;
		DecimalFormat fmt = new DecimalFormat("#.#####");

		for(int i=0;i<NeuralNetwork.INPUT_NEURONS;i++)
		{ 
			y = STARTING_Y + i*(DIAMETER + DIAMETER/10);
			g.setStroke(new BasicStroke(3));
			g.setColor(Color.BLACK);
			g.drawOval(x, y, DIAMETER, DIAMETER);
			g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
			g.drawString(fmt.format(nn.getCurrentInput()[i]),
					x+DIAMETER/4-10, y+DIAMETER/2+5);
		}
	}
	/**
	 * draws the weights to the global graphics object
	 */
	private void drawWeights()
	{
		int distance = 200 - DIAMETER;
		int[][][] lines;
		double[][] weights= normalize(nn.getInputWeights());
		
		int firstLayerX = 100;
		int secondLayerX = 300;
		
		for(int i=0;i<NeuralNetwork.HIDDEN_NEURONS;i++)
		{
			for(int j=0;j<NeuralNetwork.INPUT_NEURONS;j++)
			{
				g.setStroke(new BasicStroke(Math.abs((float) weights[i][j]) * 5));
				lines = computeWeightCor(distance, firstLayerX + DIAMETER, STARTING_Y, NeuralNetwork.INPUT_NEURONS, NeuralNetwork.HIDDEN_NEURONS);
				g.drawLine(lines[i][j][0], lines[i][j][1], lines[i][j][2], lines[i][j][3]);
			}
		}
		weights= normalize(nn.getOutputWeights());
		for(int i=0;i<NeuralNetwork.OUTPUT_NEURONS;i++)
		{
			for(int j=0;j<NeuralNetwork.HIDDEN_NEURONS;j++)
			{
				g.setStroke(new BasicStroke(Math.abs((float) weights[i][j]) * 5));
				lines = computeWeightCor(distance, secondLayerX + DIAMETER, STARTING_Y, NeuralNetwork.HIDDEN_NEURONS, NeuralNetwork.OUTPUT_NEURONS);
				g.drawLine(lines[i][j][0], lines[i][j][1], lines[i][j][2], lines[i][j][3]);;
			}
		}
	}
	/**
	 * normalizes a given data set
	 * @param data 
	 * @return The normalized data
	 */
	private double[][] normalize(Weight[][] data)
	{
		double max = getMax(data);
		double min = getMin(data);
		double[][] newWeights = new double[data.length][data[0].length];
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<data[i].length;j++)
			{
				newWeights[i][j] = (data[i][j].getWeight() - min)/ (max - min);
			}
		}
		return newWeights;
	}
	/**
	 * Computes the weight coordinates
	 * @param distance
	 * @param startX
	 * @param baseY
	 * @param layer1Length
	 * @param layer2Length
	 * @return
	 */
	private int[][][] computeWeightCor(int distance, int startX, int baseY, int layer1Length, int layer2Length)
	{
		int startY;
		int endX=startX + distance;
		int endY = 0;
		int[][][] lines = new int[layer1Length][layer2Length][4];
		
		for(int i=0;i<layer1Length;i++)
		{ 
			startY = baseY + DIAMETER/2 + i*(DIAMETER + DIAMETER/10);
			for(int j=0;j<layer2Length;j++)
			{
				endY = STARTING_Y + DIAMETER/2 + j*(DIAMETER + DIAMETER/10);
				lines[i][j] = new int[] {startX, startY, endX, endY};
			}
		}
		//doing this because I messed up and am too lazy to change it
		int[][][] temp = new int[layer2Length][layer1Length][4];
		for(int i=0;i<layer2Length;i++)
		{
			for(int j=0;j<layer1Length;j++)
			{
				temp[i][j] = lines[j][i];
			}
		}
		return temp;
	}
	/**
	 * draws the special case separately
	 */
	private void drawBias()
	{		
		int firstY = STARTING_Y + NeuralNetwork.INPUT_NEURONS * (DIAMETER + DIAMETER/10);
		int secondY = STARTING_Y + NeuralNetwork.HIDDEN_NEURONS * (DIAMETER + DIAMETER/10);
		
		int firstX = 150;
		int secondX = 350;
		g.setStroke(new BasicStroke(3));
		
		g.drawOval(firstX, firstY, DIAMETER, DIAMETER);
		g.drawString(Integer.toString(1), 150+DIAMETER/2-4, firstY+DIAMETER/2+5);
		
		g.drawOval(secondX, secondY, DIAMETER, DIAMETER);
		g.drawString(Integer.toString(1), 350+DIAMETER/2-4, secondY+DIAMETER/2+5);

		int[][][] lines;
		
		double[][] weights = normalize(nn.getInputWeights());
		for(int i=0;i<NeuralNetwork.HIDDEN_NEURONS;i++)
		{
			g.setStroke(new BasicStroke(Math.abs((float) weights[i][NeuralNetwork.INPUT_NEURONS] * 5)));
			lines = computeWeightCor(50, firstX + DIAMETER, firstY, 1, NeuralNetwork.INPUT_NEURONS);
			g.drawLine(lines[i][0][0], lines[i][0][1], lines[i][0][2], lines[i][0][3]);
		}
		
		weights = normalize(nn.getOutputWeights());
		for(int i=0;i<NeuralNetwork.OUTPUT_NEURONS;i++)
		{
			g.setStroke(new BasicStroke(Math.abs((float) weights[i][NeuralNetwork.HIDDEN_NEURONS] * 5)));
			lines = computeWeightCor(50, secondX + DIAMETER, firstY, 1, NeuralNetwork.OUTPUT_NEURONS);
			g.drawLine(lines[i][0][0], lines[i][0][1], lines[i][0][2], lines[i][0][3]);
		}
		
	}
	/**
	 * paints everything to the sceen
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		this.g.setRenderingHint(
				  RenderingHints.KEY_TEXT_ANTIALIASING,
			        RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		
		this.g.setColor(Color.WHITE);
		this.g.fillRect(0, 0, WIDTH, HEIGHT);	
		
		drawNeurons();
		drawWeights();
		drawBias();
		
		this.g.drawString("Percent of last 200 correct: "+nn.percentOf200Correct(), 50, 50);
		this.g.drawString("Percent of test correct: "+nn.percentOfTestCorrect(), 400, 50);
		
		g.drawImage(image, 0, 0, getHeight(),getWidth(), null);
		
		image =  new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		this.g = image.createGraphics();
	}
	/**
	 * starts the thread and will update every 5 milliseconds
	 */
	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				Thread.sleep(5);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * gets the max value of a data set
	 * @param data The data set
	 * @return The max value
	 */
	private double getMax(Weight[][] data)
	{
		double max=data[0][0].getWeight();
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<data[i].length;j++)
			{
				max = Math.max(max, data[i][j].getWeight());
			}
		}
		return max;
	}
	/**
	 * Gets the minimum value of a data set
	 * @param data The data
	 * @return The min value
	 */
	private double getMin(Weight[][] data)
	{
		double min=data[0][0].getWeight();
		for(int i=0;i<data.length;i++)
		{
			for(int j=0;j<data[i].length;j++)
			{
				min = Math.min(min, data[i][j].getWeight());
			}
		}
		return min;
	}
}

