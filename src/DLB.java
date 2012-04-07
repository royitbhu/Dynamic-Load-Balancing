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
	int no_Of_Task;
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
 
     	    	//System.out.println("No. of Processor = "+ no_Of_Proc+"\n");
     	    	
     	    	line = scanner.nextLine(); 
     	    	line = scanner.nextLine(); 
     	    	//Input Execution time of each Task     	    	
     	    //	System.out.print("Task No\t Execution time");
     	    //	System.out.print("\n");
     	    	
     	    	st=new StringTokenizer(line," ");
     	    	for(i=0;st.hasMoreTokens();i++)
     	    	{
     	    	//		System.out.print("(");
     	    			inputTask.exe_time[i] = Integer.parseInt(st.nextToken());
     	    	//		System.out.print("  "+i+"\t\t"+inputTask.exe_time[i]);
	        	//   	System.out.print(" )\n");	        	    	
     	    	}
     	    	no_Of_Task = i;
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
	static int chromosomes[][][]= new int[2001][5][10]; 		//Solution set (Population of 1000) 
	//static int minScheduleList[][]= new int[5][10];		//Processor - Task list of Min Schedule
	static int procAvT[] = new int[10];				//Processor available time
	static scheduleTask schedule[] = new scheduleTask[10];		//Store schedule task data 
//	static int taskAtProc[][] = new int[1001][20];	//store which task schedule at which processor
	static inputData input= new inputData();//store input  
	static int slideWindow[] = new int[sizeSlideWindow];					//store current task no in Sliding window
	static int counter[][] = new int[2001][5];
	static float fitNess[] = new float[2001];
	static float roulette[] = new float[1000];
	static int finalSchedule[][] = new int [5][10];
	static int finalDSchedule[][] = new int [5][100];
 	static int finalCount[] = new int[5];
	
	
	//Create Population of 1000 Chromosomes and store it in chromosomes[][][]
	static void createPopulation(int id)
	{	
		Random rand = new Random();
		int k,tmp,l,z,j,i;		
		//System.out.print("roy ");
		for(l=0;l<1000;l++) // Populatio 1000
		{
			
			for(z=0;z<5;z++)
			{
				counter[l][z]=0;
			}
			for(k=id;k<((sizeSlideWindow+id)>input.no_Of_Task?input.no_Of_Task:(sizeSlideWindow+id));k++)
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
	
	//Calculate max of two integers
	static int maX(int x,int y)
	{
		return x>y?x:y;
	}
	
	//Calculate max of two floats
	static float maXF(float x,float y)
	{
		return x>y?x:y;
	}
		
	//Calculate Fitness of a given chromosome (include the average utilization of Processors)	
	static float fitNessCal(int id)
	{
		int i=0,j,z,scheduleTime=0;
		float avgUtilization =0 ,sum =0,fitness=0;
		
			
		for(i=0;i<input.no_Of_Proc;i++)
		{
			int maxDAT=procAvT[i];
			for(j=0;j<counter[id][i];j++)
			{				
					maxDAT+= input.inputTask.exe_time[(chromosomes[id][i][j])];
			}
			scheduleTime = maX(scheduleTime,maxDAT);
			//procAvT[i] = maxDAT ;
			sum+=maxDAT;
			//System.out.print(maxDAT+" ");
		}
		avgUtilization = (sum / (input.no_Of_Proc*scheduleTime)) ; 
		/*System.out.print(":");
		scheduleTime=0;
		for(i=0;i<5;i++)
		{
			if(scheduleTime<procAvT[i])
				scheduleTime=procAvT[i];
		}
		//for(i=0;i<5;i++)
		//{
		//	System.out.print(procAvT[i]+" ");
		//}
		*/
		fitness = avgUtilization/scheduleTime;				
		return fitness*1000;
	}
	
	//Roullete wheel Selection
	static int roulette_Selection()
	{
		Random rand = new Random();
		int s_id=0,i=0;
		float sum_fitNess=0,tmp;	
		
		for(i=0;i<1000;i++)
		{
			sum_fitNess +=fitNess[i] ;
			
		}
		for(i=0;i<1000;i++)
		{
			roulette[i]=0;
		}
		roulette[0] =fitNess[0]/sum_fitNess;
		for(i=1;i<1000;i++)
		{
			roulette[i] =roulette[i-1]+(fitNess[i]/sum_fitNess);			
		}
		tmp = (rand.nextInt((int)sum_fitNess))/sum_fitNess;
		for(i=1;i<1000;i++)
		{
			//System.out.println(tmp);
			if((tmp>roulette[i-1]) && (tmp<roulette[i]))
			{
				s_id =i; 
				break;
			}
		}
		return s_id;
	}
	
	//Cycle Crossover
	static void cycleCrosssover()
	{
		int i=0,P1,P2,k,j,x=0,randVar,l;
		int tmp[][] = new int[2000][10];
		int switchNum[] = new int[10];
		Random rand = new Random();
		for(i=0;i<1000;i+=2)
		{
			P1 = roulette_Selection();
			P2 = roulette_Selection();
			//System.out.println(P1+"  "+P2);
			x=0;
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P1][k];j++)
				{				
					tmp[i][x] = chromosomes[P1][k][j];

					x++;
				}
			}
			x=0;
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P2][k];j++)
				{				
					tmp[i+1][x] = chromosomes[P2][k][j];
					x++;
				}
			}
			for(l=0;l<10;l++)
			{
				switchNum[l] = 0;
			}
			randVar = rand.nextInt(10);
			int temp = tmp[i][randVar];
			//System.out.println(randVar);
			tmp[1000+i][randVar] = tmp[i][randVar];
			tmp[1001+i][randVar] = tmp[i+1][randVar];
			switchNum[randVar]= 1;
			while(tmp[i+1][randVar]!=temp)
			{
				l=0;
				while(tmp[i][l]!=tmp[i+1][randVar])
				{
					l++;
				}
				randVar = l;
				tmp[1000+i][randVar] = tmp[i][randVar];
				tmp[1001+i][randVar] = tmp[i+1][randVar];
				switchNum[randVar]= 1;				
			}
			
			for(l=0;l<10;l++)
			{
				if(switchNum[l]==0)
				{
					tmp[1000+i][l] = tmp[i+1][l];
					tmp[1001+i][l] = tmp[i][l];
				}
			}
			
			/*
			for(l=0;l<10;l++)
			{
				System.out.print(tmp[i][l]+" ");
			}System.out.println();
			for(l=0;l<10;l++)
			{
				System.out.print(tmp[i+1][l]+" ");
			}System.out.println();
			for(l=0;l<10;l++)
			{
				System.out.print(tmp[1000+i][l]+" ");
			}System.out.println();
			for(l=0;l<10;l++)
			{
				System.out.print(tmp[1001+i][l]+" ");
			}System.out.println();
			*/
			x=0;
			for(k=0;k<input.no_Of_Proc;k++)
			{
				counter[1000+i][k] = counter[P1][k];
				for(j=0;j<counter[P1][k];j++)
				{				
					chromosomes[1000+i][k][j]= tmp[1000+i][x];

					x++;
				}
			}
			x=0;
			for(k=0;k<input.no_Of_Proc;k++)
			{
				counter[1001+i][k] = counter[P2][k];
				for(j=0;j<counter[P2][k];j++)
				{				
					chromosomes[1001+i][k][j] = tmp[1001+i][x];
					x++;
				}
			}
			/*
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P1][k];j++)
					System.out.print(chromosomes[1000+i][k][j]+" ");
				System.out.print(" ");
			}
			System.out.println();
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P2][k];j++)
					System.out.print(chromosomes[1001+i][k][j]+" ");
				System.out.print(" ");
			}
			*/
		}		
	}
	
	
	//Mutation
	static void Mutation()
	{
		int i,j,k,randSol,randT1,randT2;
		Random rand = new Random();
		float fitPar,fitMut;
		for(i=0;i<1000;i++)
		{
			randSol = rand.nextInt(1000);
			randT1 = rand.nextInt(10);
			randT2 = rand.nextInt(10);
			for(k=0;k<input.no_Of_Proc;k++)
			{
				counter[2000][k] =counter[1000+randSol][k];
				for(j=0;j<counter[2000][k];j++)
				{				
					if(chromosomes[1000+randSol][k][j] == randT1)
						chromosomes[2000][k][j] = randT2;
					else
					{
						if(chromosomes[1000+randSol][k][j] == randT2)
							chromosomes[2000][k][j] = randT1;
						else
							chromosomes[2000][k][j] = chromosomes[1000+randSol][k][j];
					}
				}
			}
	
			fitPar = fitNessCal(1000+randSol);
			fitMut = fitNessCal(2000);
			//System.out.println(fitPar+" "+fitMut+" "+randSol);
			if(fitPar<fitMut)
			{
				for(k=0;k<input.no_Of_Proc;k++)
				{
					counter[2000][k] =0;
					for(j=0;j<counter[1000+randSol][k];j++)
					{				
						if(chromosomes[1000+randSol][k][j] == randT1)
							chromosomes[1000+randSol][k][j] = randT2;
						else
							if(chromosomes[1000+randSol][k][j] == randT2)
								chromosomes[1000+randSol][k][j] = randT1;
						
						chromosomes[2000][k][j] = 0;
					}
				}
			}
		}
	}
	
	//Main method
	public static void main(String [] args){

		int i=0,maxFitId=0,j=0,k,key=0,inc=0;
		float maxFit=0;
		input.takeinput();
		for(inc=0;inc<input.no_Of_Task;inc+=10)
		{
			createPopulation(inc);
			maxFit=0;
			for(i=0;i<1000;i++)
			{
				//System.out.print(fitNessCal(i)+" ");
				fitNess[i] = fitNessCal(i);
				if(maxFit<fitNess[i])
				{
					maxFit = fitNess[i];
					maxFitId = i;
				}
			}
			
//			System.out.println();
//			
//			System.out.println(maxFit+" "+maxFitId);
//			System.out.print("\n");
//			for(i=0;i<input.no_Of_Proc;i++)
//			{
//				System.out.print(procAvT[i]+" ");
//				for(j=0;j<counter[maxFitId][i];j++)
//					System.out.print(chromosomes[maxFitId][i][j]+" ");
//				System.out.print(" ");
//			}
			
		//	System.out.println(roulette_Selection());
		//	System.out.println(roulette_Selection());
			cycleCrosssover();
			for(i=1000;i<2000;i++)
			{
				//System.out.print(fitNessCal(i)+" ");
				fitNess[i] = fitNessCal(i);
				if(maxFit<fitNess[i])
				{
					maxFit = fitNess[i];
					maxFitId = i;
				}
			}
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[maxFitId][k];j++)
				{				
					finalSchedule[k][j] =chromosomes[maxFitId][k][j];
				//	System.out.print(finalSchedule[k][j]);
				}
			}
			//System.out.println(" "+maxFit);
			
			Mutation();
			
			for(i=1000;i<2000;i++)
			{
				//System.out.print(fitNessCal(i)+" ");
				fitNess[i] = fitNessCal(i);
				if(maxFit<fitNess[i])
				{
					maxFit = fitNess[i];
					maxFitId = i;
					key=1;
				}
			}
			if(key==1)
			{
				for(k=0;k<input.no_Of_Proc;k++)
				{
					for(j=0;j<10;j++)
					{				
						finalSchedule[k][j] = 0;
					}
				}			
				for(k=0;k<input.no_Of_Proc;k++)
				{
					//System.out.print(" roy ");
					for(j=0;j<counter[maxFitId][k];j++)
					{				
						finalSchedule[k][j] = chromosomes[maxFitId][k][j];
			//			System.out.print(finalSchedule[k][j]);
					}
				}
			}
		//System.out.println(" "+maxFit);
		for(i=0;i<input.no_Of_Proc;i++)
		{
			
			for(j=0;j<counter[maxFitId][i];j++)
			{				
				finalDSchedule[i][finalCount[i]+j] =finalSchedule[i][j];
				procAvT[i]+= input.inputTask.exe_time[finalSchedule[i][j]];
				finalSchedule[i][j]=0;
				//System.out.print(finalSchedule[i][j]);
			}
			finalCount[i]+=counter[maxFitId][i];				
			//System.out.println(" "+procAvT[i]);
		}
		//Set all to 0
		for(i=0;i<=2000;i++)
		{
			//System.out.print(fitNessCal(i)+" ");
			fitNess[i] =0;
			for(j=0;j<input.no_Of_Proc;j++)
			{
				
				for(k=0;k<counter[i][j];k++)
				{
					chromosomes[i][j][k]=0;
				}
				counter[i][j]=0;
			}
		}
		for(i=0;i<10;i++)
		{
			slideWindow[i]=0;
		}
	}
//	for(i=0;i<input.no_Of_Proc;i++)
//	{
//		System.out.print(procAvT[i]+" ");
//	}
		
	for(i=0;i<input.no_Of_Proc;i++)
	{
		
		for(j=0;j<finalCount[i];j++)
		{				
			System.out.print(finalDSchedule[i][j]+" ");
		}
		System.out.print(" = "+procAvT[i]);
		System.out.println();
	}
	
  }
}