import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

//Task Data
class tasks{
	int[] exe_time;
	
	tasks(){		
		exe_time = new int[100];
	}
}

//Take input through File
class inputData {
	int no_Of_Proc;
	tasks inputTask;
	
	inputData()	{
		inputTask = new tasks();
	}
	
	//takes input from file input.txt 
	public void takeinput() 
	{
		File inFile;
		Scanner scanner;
		String line;
		StringTokenizer st;
		int i=0;
		
		try 
     	{
     	    	inFile = new File("input.txt");
     	    	scanner = new Scanner(inFile);
     	    	
     	    	line = scanner.nextLine();
     	    	
     	    	//Input no. of Tasks and Processors
     	    	no_Of_Proc = Integer.parseInt(line);
 
     	    	System.out.println("No. of Processor = "+ no_Of_Proc+"\n");
     	    	
     	    	line = scanner.nextLine(); 
     	    	line = scanner.nextLine(); 
     	    	//Input Execution time of each Task     	    	
     	    	System.out.print("Task No\t Execution time");
     	    	System.out.print("\n");
     	    	
     	    	st=new StringTokenizer(line," ");
     	    	for(i=0;st.hasMoreTokens();i++)
     	    	{
     	    			System.out.print("(");
     	    			inputTask.exe_time[i] = Integer.parseInt(st.nextToken());
     	    			System.out.print("  "+i+"\t\t"+inputTask.exe_time[i]);
	        	    	System.out.print(" )\n");	        	    	
     	    	}
     	    	System.out.println();
     
     	}
		catch (Exception e) {
 	    e.printStackTrace();
 	}
     	  
	}
}

//Task starting time , finish time and processor after schedule
class scheduleTask {
	int ST;
	int FT;
	int taskNo;
	int proc;
	scheduleTask()
	{
		
	}
}


//Main class
public class DLB {
	
	static int minScheduleTime=1000;    //intial schedule
	static int sizeSlideWindow= 10;		//Sliding window
	static int chromosomes[][][]= new int[1000][5][10]; 		//Solution set (Population of 1000) 
	static int minScheduleList[][]= new int[5][10];		//Processor - Task list of Min Schedule
	//static int procAvT[];				//Processor available time
	static scheduleTask schedule[] = new scheduleTask[10];		//Store schedule task data 
//	static int taskAtProc[][] = new int[1001][20];	//store which task schedule at which processor
	static inputData input= new inputData();//store input  
	int slideWindow[] = new int[sizeSlideWindow];					//store current task no in Sliding window
	
	
	
	
	//Create Population of 1000 Chromosomes and store it in chromosomes[][][]
	static void createPopulation()
	{	
		Random rand = new Random();
		int k,tmp,l,z,j,i;
		int counter[][] = new int[1000][5];
		input.takeinput();
		for(l=0;l<1000;l++) // Populatio 1000
		{
			
			for(z=0;z<5;z++)
			{
				counter[l][z]=0;
			}
			for(k=0;k<sizeSlideWindow;k++)
			{
					tmp = rand.nextInt(input.no_Of_Proc);	
					chromosomes[l][tmp][counter[l][tmp]] = k;	
					counter[l][tmp]++;
			}
		/*	System.out.print("\n");
			for(i=0;i<input.no_Of_Proc;i++)
			{
				for(j=0;j<counter[l][i];j++)
					System.out.print(chromosomes[l][i][j]+" ");
				System.out.print(" ");
			}
		*/
		}
		
		
	
	}
	
	
	//Main method
	public static void main(String [] args){

		createPopulation();
	}
}