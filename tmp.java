import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

//Task's Execution time
class tasks{
	int[] exe_time;
	
	tasks(){		
		exe_time = new int[1000];
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

//	//takes input from file input.txt 
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
 	    	
 	    	//Store no. of Processors
     	    	no_Of_Proc = Integer.parseInt(line);
 
     	    	//System.out.println("No. of Processor = "+ no_Of_Proc+"\n");
 	    	line = scanner.nextLine(); 
 	    	line = scanner.nextLine(); 
 	    	
 	    	   	    	
 	    	//System.out.print("Task No\t Execution time");
 	    	//System.out.print("\n");
 	    	
 	    	//Input Execution time of each Task  
 	    	st=new StringTokenizer(line," ");
 	    	for(i=0;st.hasMoreTokens();i++)
 	    	{
 	    			//System.out.print("(");
 	    			inputTask.exe_time[i] = Integer.parseInt(st.nextToken());
 	    			//System.out.print("  "+i+"\t\t"+inputTask.exe_time[i]);
        	   		//System.out.print(" )\n");	        	    	
 	    	}
 	    	
 	    	//Total no. of Task
 	    	no_Of_Task = i;
 	    	System.out.println();
     
     	}
		catch (Exception e)
		{
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
	
	//Intial schedule
	static int minScheduleTime=100000;    
	//Sliding window
	static int sizeSlideWindow= 10;
	
	//Solution set (Population of 1000)
	static int chromosomes[][][]= new int[3000][5][sizeSlideWindow]; 
	//static int minScheduleList[][]= new int[5][10];		//Processor - Task list of Min Schedule
	static int procAvT[] = new int[10];				//Processor available time
	static scheduleTask schedule[] = new scheduleTask[sizeSlideWindow];		//Store schedule task data 
//	static int taskAtProc[][] = new int[1001][20];	//store which task schedule at which processor
	static inputData input= new inputData();//store input  
	
	static int counter[][] = new int[3000][5];
	static float fitNess[] = new float[3000];
	static float roulette[] = new float[1000];
	static int finalSchedule[][] = new int [5][sizeSlideWindow];
	static int finalDSchedule[][] = new int [5][500];
 	static int finalCount[] = new int[5];
 	static int fCount[] = new int[5];
 	static float proc_speed[]=new float[5];
 	static float maxFitNess [] = new float [100];
	
	
 //	Create Population of 1000 Chromosomes and store it in chromosomes[][][]
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
			/*
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[l][k];j++)
					System.out.print(chromosomes[l][k][j]+" ");
				System.out.print(" ");
			}
			System.out.print("\t");*/
		}			
		//System.out.print("\n");
	}
	
//	processor Speed initialization
	static void speed_initialization()
	{
		int i=0;
		for(i=0;i<input.no_Of_Proc;i++)
		{
			proc_speed[i]=1;//(float) (1+i*0.25);
//			System.out.println(proc_speed[i]);
		}
		
	}
	
//	Calculate max of two integers
	static int maX(int x,int y)
	{
		return x>y?x:y;
	}
	
//	Calculate max of two floats
	static float maXF(float x,float y)
	{
		return x>y?x:y;
	}
		
//	Calculate Fitness of a given chromosome (include the average utilization of Processors)	
	static float fitNessCalwLoadBal(int id)
	{
		int i=0,j,z,scheduleTime=0;
		float avgUtilization =0 ,sum =0,fitness=0;
		
			
		for(i=0;i<input.no_Of_Proc;i++)
		{
			int maxDAT=procAvT[i];
			for(j=0;j<counter[id][i];j++)
			{				
					maxDAT+= input.inputTask.exe_time[(chromosomes[id][i][j])]/proc_speed[i];
			}
			scheduleTime = maX(scheduleTime,maxDAT);
//				procAvT[i] = maxDAT ;
			sum+=maxDAT;  //for Loadbalancing
//				System.out.print(maxDAT+" ");
		}
		avgUtilization = (sum / (input.no_Of_Proc*scheduleTime)) ; //for Load balancing
//				System.out.print(avgUtilization+" ");
//		avgUtilization = 1; //without load balancing
//				System.out.print(":");
//					scheduleTime=0;
//					for(i=0;i<5;i++)
//					{
//						if(scheduleTime<procAvT[i])
//							scheduleTime=procAvT[i];
//					}
//					for(i=0;i<5;i++)
//					{
//						System.out.print(procAvT[i]+" ");
//					}
					
		fitness = avgUtilization/scheduleTime;				
		return fitness*1000;
	}
	
	

//	Roullete wheel Selection
	static int roulette_Selection()
	{
		Random rand = new Random();
		int s_id=0,i=0;
		float sum_fitNess=0,tmp;	
		
		for(i=0;i<1000;i++)
		{
			sum_fitNess +=fitNess[i] ;
			
			
		}
		//System.out.print(sum_fitNess+" ");
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
	
//	Cycle Crossover
	static void cycleCrosssover()
	{
		int i=0,P1,P2,k,j,x=0,randVar,l;
		int tmp[][] = new int[2000][sizeSlideWindow];
		int switchNum[] = new int[sizeSlideWindow];
		Random rand = new Random();
		for(i=0;i<1000;i+=2)
		{
			P1 = roulette_Selection();
			P2 = roulette_Selection();
//			System.out.println(P1+"  "+P2);
//			check if both selected parents are not same
			while(P1==P2)
			{
				P2 = roulette_Selection();				
			}
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
			for(l=0;l<sizeSlideWindow;l++)
			{
				switchNum[l] = 0;
			}
			randVar = rand.nextInt(sizeSlideWindow);
			int temp = tmp[i][randVar];
			//System.out.println(randVar);
			tmp[1000+i][randVar] = tmp[i][randVar];
			tmp[1001+i][randVar] = tmp[i+1][randVar];
			switchNum[randVar]= 1;
//			copying the task in cycle crossover			
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
//			Switching the task in cycle crossover			
			for(l=0;l<sizeSlideWindow;l++)
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
//			copying the newly generated child
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
//			copying the newly generated child
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
	
	
//	Mutation
	static void Mutation(int num,int inc1)
	{
		int i,j,k,randSol,randT1,randT2;
		Random rand = new Random();
		float fitPar,fitMut;
	
		randT1 = rand.nextInt(sizeSlideWindow);
		randT2 = rand.nextInt(sizeSlideWindow);
		while(randT1==randT2)
		{
			randT2 = rand.nextInt(sizeSlideWindow);
		}
		for(k=0;k<input.no_Of_Proc;k++)
		{
			counter[2000+num][k] =counter[1000+num][k];
			for(j=0;j<counter[2000+num][k];j++)
			{				
				if(chromosomes[1000+num][k][j] == randT1+inc1)
					chromosomes[2000+num][k][j] = randT2+inc1;
				else
				{
					if(chromosomes[1000+num][k][j] == randT2+inc1)
						chromosomes[2000+num][k][j] = randT1+inc1;
					else
						chromosomes[2000+num][k][j] = chromosomes[1000+num][k][j];
				}
			}
			/*fitPar = fitNessCalwLoadBal(1000+randSol);
			fitPar = fitNessCalwoLoadBal(1000+randSol);
			fitMut = fitNessCalwLoadBal(2000);
			fitMut = fitNessCalwoLoadBal(2000);
			System.out.println(fitPar+" "+fitMut+" "+randSol);
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
			}*/
		}
	}
	
//	Main method
	public static void main(String [] args)
	{
		int i=0,maxFitId=0,j=0,k,key=0,inc=0,gen,l=0,flip=0,z=0;;
		float maxF = 0,sum=0;
		float maxFit=0;
		input.takeinput();
		speed_initialization();
		
		for(inc=0;inc<input.no_Of_Task;inc+=sizeSlideWindow)
		{
			
			maxFit=0;
			maxFitId=0;
			createPopulation(inc);
			gen=100;
			while(gen>0)
			{
				/*for(z=0;z<1000;z++)
				{	
					for(k=0;k<input.no_Of_Proc;k++)
					{
						for(j=0;j<counter[z][k];j++)
							System.out.print(chromosomes[z][k][j]+" ");
						System.out.print(" ");
					}
					System.out.print("\t");
				}
				System.out.print("\n");*/
				
	
				for(i=0;i<1000;i++)
				{
					fitNess[i] = fitNessCalwLoadBal(i);
					//fitNess[i] = fitNessCalwoLoadBal(i);
					//System.out.print(fitNess[i]+" ");
					/*if(maxFit<fitNess[i])
					{
						maxFit = fitNess[i];
						maxFitId = i;
					}*/
					
				}
				//System.out.println();
				cycleCrosssover();
				
				for(i=0;i<1000;i++)
				{
					Mutation(i,inc);
				}
				
				/*for(z=2000;z<2010;z++)
				{	
					for(k=0;k<input.no_Of_Proc;k++)
					{
						for(j=0;j<counter[z][k];j++)
							System.out.print(chromosomes[z][k][j]+" ");
						System.out.print(" ");
					}
					System.out.print("\t");
				}
				System.out.print("\n");*/
				flip=0;
				//System.out.print("gen="+gen+" ");
				for(i=0;i<3000;i++)
				{
					//System.out.print(fitNessCal(i)+" ");
					fitNess[i] = fitNessCalwLoadBal(i);
					//System.out.print(fitNess[i]+" ");
					//fitNess[i] = fitNessCalwoLoadBal(i);
					if(maxFit<fitNess[i])
					{
						maxFit = fitNess[i];
						maxFitId = i;
						flip =1;
					}
				}
				if(flip==1)
				{
					for(k=0;k<input.no_Of_Proc;k++)
					{
						for(j=0;j<counter[maxFitId][k];j++)
						{				
							finalSchedule[k][j] =chromosomes[maxFitId][k][j];
							//System.out.print(finalSchedule[k][j]);
						}
						fCount[k]=counter[maxFitId][k];
					}
					maxFitNess[inc] = maxFit;
				}
				/*for(k=0;k<input.no_Of_Proc;k++)
				{
					//System.out.print(" roy ");
					for(j=0;j<fCount[k];j++)
					{				
						System.out.print(finalSchedule[k][j]+" ");
						
					}
				}
				System.out.println();*/
				//copy all best to current population
				for(i=0;i<1000;i++)
				{
					if(fitNess[i]>=fitNess[1000+i]&&fitNess[i]>=fitNess[2000+i])
						l=i;
					else
					{
						if(fitNess[i]<fitNess[1000+i]&&fitNess[1000+i]>fitNess[2000+i])
							l=1000+i;
						else
							l=2000+i;
					}
					
					//System.out.println(fitNess[i]+" "+fitNess[1000+i]+" "+fitNess[2000+i]+"="+l);
					if(l>=1000)
					{
						for(k=0;k<input.no_Of_Proc;k++)
						{
							for(j=0;j<counter[i][k];j++)
							{				
								chromosomes[i][k][j] =0;
								//System.out.print(finalSchedule[k][j]);
							}
							counter[i][k]=0;
						}
						for(k=0;k<input.no_Of_Proc;k++)
						{
							for(j=0;j<counter[l][k];j++)
							{				
								chromosomes[i][k][j] =chromosomes[l][k][j];
								//System.out.print(finalSchedule[k][j]);
							}
							counter[i][k]=counter[l][k];
							fitNess[i]=fitNess[l];
						}
					
					}
					/*for(k=0;k<input.no_Of_Proc;k++)
					{
						//System.out.print(" roy ");
						for(j=0;j<counter[i][k];j++)
						{				
							System.out.print(chromosomes[i][k][j]+" ");
							
						}
					}
					System.out.println();*/
				}
				
				
			
				//Set all to 0
				for(i=1000;i<3000;i++)
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
					fitNess[i] = 0;
				}
				
				/*for(k=0;k<input.no_Of_Proc;k++)
				{
					//System.out.print(" roy ");
					for(j=0;j<fCount[k];j++)
					{				
						System.out.print(finalSchedule[k][j]+" ");
					}
					System.out.println();
				}
				System.out.println();*/
				gen--;
			}

			
			for(k=0;k<input.no_Of_Proc;k++)
			{
				//System.out.print(" roy ");
				for(j=0;j<fCount[k];j++)
				{				
					System.out.print(finalSchedule[k][j]+" ");
				}
				System.out.println();
			}
			//System.out.println(maxFitNess[inc]);
		
			//==============
			sum = 0;
			maxF = 0;
			for(k=0;k<input.no_Of_Proc;k++)
			{
				
				for(j=0;j<fCount[k];j++)
				{				
					finalDSchedule[k][finalCount[k]+j] =finalSchedule[k][j];
					procAvT[k]+= input.inputTask.exe_time[finalSchedule[k][j]]/proc_speed[k];
					finalSchedule[k][j]=0;
					//System.out.print(finalSchedule[k][j]);
				}
				System.out.print(procAvT[k]+" ");
				finalCount[k]+=fCount[k];	
				maxF=maXF(maxF,procAvT[k]);
				sum+=procAvT[k];
				//System.out.println(" "+procAvT[i]);
			}
			//System.out.println();
			System.out.println("Utilization "+sum/(input.no_Of_Proc*maxF)+"\n");
		
			
			
			//Set all to 0			
			for(z=0;z<3000;z++)
			{
				fitNess[z] =0;
				for(k=0;k<input.no_Of_Proc;k++)
				{
					
					for(j=0;j<counter[z][k];j++)
					{
						chromosomes[z][k][j]=0;
					}
					counter[z][k]=0;
				}
			}
			for(k=0;k<input.no_Of_Proc;k++)
			{
				//System.out.print(" roy ");
				for(j=0;j<fCount[k];j++)
				{				
					finalSchedule[k][j]=0;
				}
				fCount[k]=0;//System.out.println();
			}
			
		}
		maxF = 0;sum=0;
		for(i=0;i<input.no_Of_Proc;i++)
		{
			System.out.print("Processor# "+i+": ");
			for(j=0;j<finalCount[i];j++)
			{				
				System.out.print(finalDSchedule[i][j]+" ");
			}
			maxF=maXF(maxF,procAvT[i]);
			sum+=procAvT[i];
			System.out.print(" = "+procAvT[i]);
			System.out.println();
		}
		System.out.println("Final avg utilization = "+sum/(input.no_Of_Proc*maxF));
	}
}
