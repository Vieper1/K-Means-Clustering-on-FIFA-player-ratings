import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Scanner;

public class KMeans
{
	public static void main(String args[])
		throws FileNotFoundException, IOException
	{
		Scanner sc = new Scanner(System.in);
		String filePath = "";
		System.out.print("Enter the name of the CSV file: ");
		String fileName = sc.nextLine();

		//////////Open the file just to count the number of records//////////
		int records = getRecords(filePath, fileName);

        //////////Open file again to read the records//////////
		double[][] points = new double[records][2];
        readRecords(filePath, fileName, points);

        //////////Sort the points based on X-coordinate values//////////
        sortPointsByX(points);

        //////////Input number of clusters//////////
        System.out.print("Enter the number of clusters to form: ");
        int clusters = sc.nextInt();

        //////////Calculate initial means//////////
        double[][] means = new double[clusters][2];
        for(int i=0; i<means.length; i++)
        {
        	means[i][0] = points[(int) (Math.floor((records*1.0/clusters)/2) + i*records/clusters)][0];
        	means[i][1] = points[(int) (Math.floor((records*1.0/clusters)/2) + i*records/clusters)][1];
        }

        //////////Create skeletons for clusters//////////
        ArrayList<Integer>[] oldClusters = new ArrayList[clusters];
        ArrayList<Integer>[] newClusters = new ArrayList[clusters];

        for(int i=0; i<clusters; i++)
        {
        	oldClusters[i] = new ArrayList<Integer>();
        	newClusters[i] = new ArrayList<Integer>();
        }

        //////////Make the initial clusters//////////
        formClusters(oldClusters, means, points);
        int iterations = 0;
	
		System.out.print("\033[H\033[2J");
		System.out.flush();
		
		System.out.println("Enter the name of the CSV file: " + fileName);
		System.out.println("Enter the number of clusters to form: " + clusters + "\n");
        /////////Showtime//////////
        while(true)
        {
        	updateMeans(oldClusters, means, points);
        	formClusters(newClusters, means, points);

        	iterations++;
        	
        	switch((iterations/1000))
        	{
		    	case 0:	System.out.print("\r[          ]"); break;
		    	case 1:	System.out.print("\r[=         ]"); break;
		    	case 2:	System.out.print("\r[==        ]"); break;
		    	case 3:	System.out.print("\r[===       ]"); break;
		    	case 4:	System.out.print("\r[====      ]"); break;
		    	case 5:	System.out.print("\r[=====     ]"); break;
		    	case 6:	System.out.print("\r[======    ]"); break;
		    	case 7:	System.out.print("\r[=======   ]"); break;
		    	case 8:	System.out.print("\r[========  ]"); break;
		    	case 9:	System.out.print("\r[========= ]"); break;
		    	case 10:	System.out.print("\r[==========]"); break;
        	}
        	System.out.print((iterations/100) + "%");
        	
        	if(iterations >= 10000 || checkEquality(oldClusters, newClusters))
        		break;
        	else
        		resetClusters(oldClusters, newClusters);
        }

        //////////Display the output//////////
        System.out.println("\nThe final clusters are:");
        displayOutput(oldClusters, points);
        System.out.println("\nIterations taken = " + iterations);

        sc.close();
	}

	static int getRecords(String filePath, String fileName)
		throws IOException
	{
		int records = 0;
		BufferedReader br = new BufferedReader(new FileReader(filePath + fileName + ".csv"));
        while (br.readLine() != null)
        	records++;

        br.close();
        return records;
	}

	static void readRecords(String filePath, String fileName, double[][] points)
		throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(filePath + fileName + ".csv"));
		String line;
		int i = 0;
        while ((line = br.readLine()) != null)
        {
        	points[i][0] = Double.parseDouble(line.split(",")[9]);
        	points[i++][1] = Double.parseDouble(line.split(",")[14]);
        }

        br.close();
	}

	static void sortPointsByX(double[][] points)
	{
		double[] temp;
		
		//////////Bubble Sort//////////
		for(int i=0; i<points.length; i++)
            for(int j=1; j<(points.length-i); j++)
            	if(points[j-1][0] > points[j][0])
            	{
                    temp = points[j-1];
                    points[j-1] = points[j];
                    points[j] = temp;
            	}
	}

	static void updateMeans(ArrayList<Integer>[] clusterList, double[][] means, double[][] points)
	{
		double totalX = 0;
		double totalY = 0;
		for(int i=0; i<clusterList.length; i++)
		{
			totalX = 0;
			totalY = 0;
			for(int index: clusterList[i])
			{
				totalX += points[index][0];
				totalY += points[index][1];
			}
			means[i][0] = totalX/clusterList[i].size();
			means[i][1] = totalY/clusterList[i].size();
		}
	}

	static void formClusters(ArrayList<Integer>[] clusterList, double[][] means, double[][] points)
	{
		double distance[] = new double[means.length];
		double minDistance = 999999999;
		int minIndex = 0;

		for(int i=0; i<points.length; i++)
		{
			minDistance = 999999999;
			for(int j=0; j<means.length; j++)
			{
				distance[j] = Math.sqrt(Math.pow((points[i][0] - means[j][0]), 2) + Math.pow((points[i][1] - means[j][1]), 2));
				if(distance[j] < minDistance)
				{
					minDistance = distance[j];
					minIndex = j;
				}
			}
			clusterList[minIndex].add(i);
		}
	}

	static boolean checkEquality(ArrayList<Integer>[] oldClusters, ArrayList<Integer>[] newClusters)
	{
		for(int i=0; i<oldClusters.length; i++)
		{
			//////////Check only lengths first//////////
			if(oldClusters[i].size() != newClusters[i].size())
				return false;

			//////////Check individual values if lengths are equal//////////
			for(int j=0; j<oldClusters[i].size(); j++)
				if(oldClusters[i].get(j) != newClusters[i].get(j))
					return false;
		}

		return true;
	}

	static void resetClusters(ArrayList<Integer>[] oldClusters, ArrayList<Integer>[] newClusters)
	{
		for(int i=0; i<newClusters.length; i++)
		{
			//////////Copy newClusters to oldClusters//////////
			oldClusters[i].clear();
			for(int index: newClusters[i])
				oldClusters[i].add(index);

			//////////Clear newClusters//////////
			newClusters[i].clear();
		}
	}

	static void displayOutput(ArrayList<Integer>[] clusterList, double[][] points)
	{
		StdDraw.setPenRadius(0.007);
		StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.line(0.01, 0, 0.01, 1);
		StdDraw.line(0.01, 0, 1, 0);

		for(int i=0; i<clusterList.length; i++)
        {
        	switch(i)
       		{
       		case 0:
       			StdDraw.setPenColor(StdDraw.BLUE);
       			break;
       		case 1:
       			StdDraw.setPenColor(StdDraw.RED);
				break;
			case 2:	
				StdDraw.setPenColor(StdDraw.GREEN);
				break;
			case 3: 
				StdDraw.setPenColor(StdDraw.GRAY);
				break;
			case 4:	
				StdDraw.setPenColor(StdDraw.CYAN);
				break;
			case 5:	
				StdDraw.setPenColor(StdDraw.PINK);
				break;
			case 6:	
				StdDraw.setPenColor(StdDraw.ORANGE);
				break;
			case 7:	
				StdDraw.setPenColor(StdDraw.MAGENTA);
				break;
			case 8:	
				StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
				break;
			case 9:	
				StdDraw.setPenColor(StdDraw.BLACK);
				break;
			case 10:	
				StdDraw.setPenColor(StdDraw.YELLOW);
				break;
			}
        	
        	String clusterOutput = "\n\n[";
        	for(int index: clusterList[i])
        		//clusterOutput += "(" + points[index][0] + ", " + points[index][1] + "), ";
        		//System.out.println(clusterOutput.substring(0, clusterOutput.length()-2) + "]");
        		StdDraw.point(((points[index][0]-40)/60), (points[index][1]/50));
        }
	}
}
