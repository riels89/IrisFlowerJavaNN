package neuralNetwork;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.Timer;

public class NNDriver implements KeyListener
{
	private static double[][] demensions = new double[150][4];
	private static int[][] answers = new int[150][3];
	private static double[][]demensionsTest = new double[(int) (demensions.length * .2)][4];
	private static int[][] answersTest = new int[(int) (demensions.length * .2)][4];
	private static GUI gui;
	private static NeuralNetwork nn;
		
	/**
	 * makes a time to train the data slowly
	 */
	 private ActionListener taskPerformer = new ActionListener() {
		 int counter=0;
		  	@Override
			public void actionPerformed(ActionEvent e)
			{
		  		for(int i=0;i<130;i++)
		  		{
			  		nn.trainOne();
			  		counter++;
		  		}
		  		if(counter==10000 * 130)
		  			timer.removeActionListener(taskPerformer);
		  		//System.out.println(counter);
			}
	  };
	  private Timer timer = new Timer(50, taskPerformer);
	
	/**
	 * makes the frame
	 */
	private void makeFrame()
	{
		JFrame frame = new JFrame("Neural Network");
		frame.setSize(GUI.WIDTH, GUI.HEIGHT);
		frame.setLocation(200, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(gui = new GUI(nn));
		frame.setVisible(true);	
		frame.addKeyListener(this);
		new Thread(gui).start();
	}
	/**
	 * normalizes the data AKA puts the data between 0 and 1 but keeps the same frequency of data
	 */
	private void normalize()
	{
		for(int i=0;i<demensions.length;i++)
		{
			for(int j=0;j<demensions[i].length;j++)
			{
				demensions[i][j] = (demensions[i][j] - getMin()) / (getMax() - getMin());
			}
		}
	}
	/**
	 * gets the max value for the data
	 * @return
	 */
	private static double getMax()
	{
		double max=demensions[0][0];
		for(int i=0;i<demensions.length;i++)
		{
			for(int j=0;j<demensions[i].length;j++)
			{
				max = Math.max(demensions[i][j], max);
			}
		}
		return max;
	}
	/**
	 * gets the min value of the data
	 * @return
	 */
	private static double getMin()
	{
		double min=demensions[0][0];
		for(int i=0;i<demensions.length;i++)
		{
			for(int j=0;j<demensions[i].length;j++)
			{
				min = Math.min(demensions[i][j], min);
			}
		}
		return min;
	}
	/**
	 * seperates the data into the training and testing sets
	 */
	private void seperateData()
	{
		for(int i=0;i<(int)(demensions.length*.2);i++)
		{
			demensionsTest[i] = demensions[i+120];
			answersTest[i] = answers[i+120];
		}
		double[][] temp = new double[(int) (demensions.length*.8)][demensions[0].length];
		int[][] aTemp = new int[(int) (answers.length * .8)][answers[0].length];
		for(int i=0;i<(int) (demensions.length * .8);i++)
		{
			temp[i] = demensions[i];
			aTemp[i] = answers[i];
		}
		demensions = temp;
		answers = aTemp;
	}
	/**
	 * randomizes the order of the data
	 */
	private void randomizeData()
	{
		double[] temp;
		int[] tempInt;
		int randNum;
		for(int i=0;i<200;i++)
		{
			for(int j=0;j<150;j++)
			{
				temp = demensions[j];
				demensions[j] = demensions[(randNum = (int) (Math.random() * 150))];
				demensions[randNum] = temp;
				
				tempInt = answers[j];
				answers[j] = answers[randNum];
				answers[randNum] = tempInt;
			}
		}
	}
	/**
	 * gets the data from the csv file
	 */
	private void getData()
	{
		String input;
		Scanner scan;
		String[] words;
		try
		{
			scan = new Scanner(new FileReader("src/Iris.csv"));
			scan.useDelimiter(",");
			scan.nextLine();
			for(int i=0;i<150;i++)
			{
				words = scan.nextLine().split(","); 
				for(int j=0;j<demensions[i].length;j++)
				{
					demensions[i][j] = Double.parseDouble(words[j+1]);
				}		
				input = words[words.length-1];
				if(input.contains("Iris-setosa"))
				{
					answers[i][0] = 1;
					answers[i][1] = 0;
					answers[i][2] = 0;
				}
				else if(input.contains("Iris-versicolor"))
				{
					answers[i][0] = 0;
					answers[i][1] = 1;
					answers[i][2] = 0;
				}
				else if(input.contains("Iris-virginica"))
				{
					answers[i][0] = 0;
					answers[i][1] = 0;
					answers[i][2] = 1;
				}			
			}
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * checks to see if either 1,2,3 or t was pressed
	 * 1 = train one
	 * 2 = train slowly
	 * 3 =  train fast
	 * t = test
	 */
	@Override
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		if(timer.isRunning()&&key != KeyEvent.VK_2)
			timer.stop();
		
		if(key == KeyEvent.VK_1)
			nn.trainOne();
		else if(key == KeyEvent.VK_2)
		{
			if(timer.isRunning())
				timer.stop();
			else
				timer.start();
		}
		else if(key == KeyEvent.VK_3)
			nn.train(10000);
		else if(key==KeyEvent.VK_T)
			nn.test();
	}
	@Override
	public void keyReleased(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	@Override
	public void keyTyped(KeyEvent e)
	{
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args)
	{
		NNDriver driver = new NNDriver();
		driver.getData();
		driver.normalize();
		driver.randomizeData();
		driver.seperateData();
		
		nn = new NeuralNetwork(demensions, answers, demensionsTest, answersTest);
		driver.makeFrame();
		//nn.test();
		//nn.printWeights();
	}
}
