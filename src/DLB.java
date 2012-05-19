import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.StringTokenizer;

//TASK'S EXECUTION TIME
class tasks{
	int[] exe_time;
	
	tasks(){		
		exe_time = new int[1000];
	}
}

//TAKE INPUT THROUGH FILE
class inputData {
	int no_Of_Proc;
	int no_Of_Task;
	tasks inputTask;
	
	inputData()	{
		inputTask = new tasks();
	}

//	//TAKES INPUT FROM FILE INPUT.TXT 
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
 	    	
 	    	//STORE NO. OF PROCESSORS
     	    no_Of_Proc = Integer.parseInt(line);
 
     	    //System.out.println("No. of Processor = "+ no_Of_Proc+"\n");
 	    	line = scanner.nextLine(); 
 	    	line = scanner.nextLine(); 
 	    	
 	    	   	    	
 	    	//System.out.print("Task No\t Execution time");
 	    	//System.out.print("\n");
 	    	
 	    	//INPUT EXECUTION TIME OF EACH TASK  
 	    	st=new StringTokenizer(line," ");
 	    	for(i=0;st.hasMoreTokens();i++)
 	    	{
 	    		inputTask.exe_time[i] = Integer.parseInt(st.nextToken());
 	    		//System.out.print("  "+i+"\t\t"+inputTask.exe_time[i]);	        	    	
 	    	}
 	    	
 	    	//TOTAL NO. OF TASK
 	    	no_Of_Task = i;
 	    	System.out.println();
     
     	}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

//TASK STARTING TIME , FINISH TIME AND PROCESSOR AFTER SCHEDULE
class scheduleTask {
	int ST;
	int FT;
	int taskNo;
	int proc;
	scheduleTask()
	{
		
	}
}


//MAIN CLASS
public class DLB {
	
	//INTIAL SCHEDULE
	static int minScheduleTime=100000;    
	//SLIDING WINDOW
	static int sizeSlideWindow= 10;
	//POPULATION SIZE(SHOUD BE EVEN)
	static int pop_size = 1000;
	//SOLUTION SET(INITIAL POPULATION PLUS CROSSOVER AND MUTATION POP)
	static int chromosomes[][][]; 
	//PROCESSOR AVAILABLE TIME OR EACH PROCESSOR
	static int procAvT[];			
	
	//static scheduleTask schedule[] = new scheduleTask[sizeSlideWindow];		//Store schedule task data 
	//static int taskAtProc[][] = new int[1001][20];	//store which task schedule at which processor

	//COUNT OF TASK ON EACH PROCESSOR  
	static int counter[][];
	//COUNT OF TASK ON EACH PROCESSOR OF FITTEST CHROSOME
	 static int fCount[];	
	//COUNT OF TASK ON EACH PROCESSOR IN GENERATED SCHEDULE
	static int finalCount[];
	//STORE FITNESS OF GENERATED POPULATION
	static float fitNess[];
	//STORES VALUE OF ROULETTE WHEEL
	static float roulette[];
	//STORES SCHEDULE OF EVERY SLIDING WINDOW
	static int finalSchedule[][];
	//STORES FINAL SCHEDULE
	static int finalDSchedule[][];
	//STORED THE HETEROGENIOUS FACTOR OF PROCESSORS
 	static float proc_speed[];
 	//STORES MAXIMUM FITNESS OF EACH SLIDING WINDOW
 	static float maxFitNess [];
	//
 	static int scheduleRA[][];
 	//Take input
 	static inputData input= new inputData();  
 		
	
 	//CREATE POPULATION OF POP_SIZE(1000) CHROMOSOMES AND STORE IT IN CHROMOSOMES[][][]
	static void createPopulation(int id)
	{	
		Random rand = new Random();
		int k,tmp,l,z,j;
		//RANDOMLY CREATE SOLUTION SET OF POP_SIZE(1000)
		for(l=0;l<pop_size;l++) 
		{
			for(z=0;z<input.no_Of_Proc;z++)
			{
				counter[l][z]=0;
			}
			for(k=id;k<((sizeSlideWindow+id)>input.no_Of_Task?input.no_Of_Task:(sizeSlideWindow+id));k++)
			{
					tmp = rand.nextInt(input.no_Of_Proc);	
					chromosomes[l][tmp][counter[l][tmp]] = k;
					//System.out.print(chromosomes[l][tmp][counter[l][tmp]]+" ");
					counter[l][tmp]++;
			}
			//System.out.println();
			/*System.out.print(l+"=");
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[l][k];j++)
					System.out.print(chromosomes[l][k][j]+" ");
			}
			System.out.print("\n");*/
		}			
	}
	
	//PROCESSOR SPEED INITIALIZATION
	static void speed_initialization()
	{
		int i=0;
		for(i=0;i<input.no_Of_Proc;i++)
		{
			proc_speed[i]=1+(float) (1+i*0.25);
			//System.out.println(proc_speed[i]);
		}
		
	}
	
	//CALCULATE MAX OF TWO INTEGERS
	static int maX(int x,int y)
	{
		return x>y?x:y;
	}
	
	//CALCULATE MAX OF TWO FLOATS
	static float maXF(float x,float y)
	{
		return x>y?x:y;
	}
		
	//CALCULATE FITNESS OF A GIVEN CHROMOSOME (INCLUDE THE AVERAGE UTILIZATION OF PROCESSORS)	
	static float fitNessCalwLoadBal(int id)
	{
		int i=0,j,scheduleTime=0;
		float avgUtilization =0 ,sum =0,fitness=0;
					
		for(i=0;i<input.no_Of_Proc;i++)
		{
			int maxDAT=procAvT[i];
			for(j=0;j<counter[id][i];j++)
			{				
					maxDAT+= input.inputTask.exe_time[(chromosomes[id][i][j])]/proc_speed[i];
			}
			scheduleTime = maX(scheduleTime,maxDAT);
			//procAvT[i] = maxDAT ;
			sum+=maxDAT; 
		}
		//FOR LOAD BALANCING
		avgUtilization = (sum / (input.no_Of_Proc*scheduleTime)) ; 
		/*System.out.print(avgUtilization+" ");
		avgUtilization = 1; //without load balancing
		System.out.print(":");
		scheduleTime=0;
		for(i=0;i<5;i++)
		{
			if(scheduleTime<procAvT[i])
				scheduleTime=procAvT[i];
		}
		for(i=0;i<5;i++)
		{
			System.out.print(procAvT[i]+" ");
		}*/
					
		fitness = avgUtilization/scheduleTime;				
		//MULTIPLY BY 1000,SO THAT FITNESS IS GREATER THEN 1
		return fitness*1000;
	}
	
	//ROULLETE WHEEL SELECTION TO SELECT FITTEST PARENT FOR CROSSOVER
	static int roulette_Selection()
	{
		Random rand = new Random();
		int s_id=0,i=0;
		float sum_fitNess=0,tmp;	
		
		for(i=0;i<pop_size ;i++)
		{
			sum_fitNess +=fitNess[i] ;
			
			
		}
		//System.out.print(sum_fitNess+" ");
		//INITIALIZE THE ROULETTE
		for(i=0;i<pop_size ;i++)
		{
			roulette[i]=0;
		}
		
		roulette[0] =fitNess[0]/sum_fitNess;
		
		//CALCULATE THE ROULETTE WHEEL 
		for(i=1;i<pop_size ;i++)
		{
			roulette[i] =roulette[i-1]+(fitNess[i]/sum_fitNess);			
		}
		tmp = (rand.nextInt((int)sum_fitNess))/sum_fitNess;
		for(i=1;i<pop_size ;i++)
		{
			//System.out.println(tmp);
			if((tmp>roulette[i-1]) && (tmp<roulette[i]))
			{
				s_id =i; 
				break;
			}
		}
		//RETURN THE ID OF SELECTED PARENT
		return s_id;
	}
	
	//CYCLE CROSSOVER
	static void cycleCrosssover()
	{
		int i=0,P1,P2,k,j,x=0,randVar,l;
		int tmp[][] = new int[2*pop_size][sizeSlideWindow];

		int switchNum[] = new int[sizeSlideWindow];
		Random rand = new Random();
		for(i=0;i<pop_size ;i+=2)
		{
			P1 = roulette_Selection();
			P2 = roulette_Selection();
			//System.out.println(P1+"  "+P2);
			
			//CHECK IF BOTH SELECTED PARENTS ARE NOT SAME
			while(P1==P2)
			{
				P2 = roulette_Selection();				
			}
			/*System.out.print(P1+"=");
			for(k=0;k<input.no_Of_Proc;k++)
			{				
				for(j=0;j<counter[P1][k];j++)
					System.out.print(chromosomes[P1][k][j]+" ");
			}
			System.out.print("\t");
			System.out.print(P2+"=");
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P2][k];j++)
					System.out.print(chromosomes[P2][k][j]+" ");
			}
			System.out.print("\n");*/
			
			//MAKE A TEMPRARY COPY OF PARENT1
			x=0;			
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P1][k];j++)
				{				
					tmp[i][x] = chromosomes[P1][k][j];
					//System.out.print(chromosomes[P1][k][j]+" ");
					x++;
				}
				
			}
			//System.out.println();
			
			//MAKE A TEMPRARY COPY OF PARENT2
			x=0;
			for(k=0;k<input.no_Of_Proc;k++)
			{
				for(j=0;j<counter[P2][k];j++)
				{				
					tmp[i+1][x] = chromosomes[P2][k][j];
					//System.out.print(chromosomes[P2][k][j]+" ");
					x++;
				}
			}
			//System.out.println();
			
			for(l=0;l<sizeSlideWindow;l++)
			{
				switchNum[l] = 0;
			}
			
			//APPLY THE CYCLE CROSSOVER OPERATOR 
			randVar = rand.nextInt(sizeSlideWindow);
			int temp = tmp[i][randVar];
			//System.out.println(randVar);
			tmp[pop_size +i][randVar] = tmp[i][randVar];
			tmp[pop_size +1+i][randVar] = tmp[i+1][randVar];
			switchNum[randVar]= 1;
			
			//COPYING THE TASK IN CYCLE CROSSOVER			
			while(tmp[i+1][randVar]!=temp)
			{
				//System.out.print(tmp[i+1][randVar]+" ");
				l=0;
				while(tmp[i][l]!=tmp[i+1][randVar] )
				{
					l++;
				}
				randVar = l;
				tmp[pop_size +i][randVar] = tmp[i][randVar];
				tmp[pop_size +1+i][randVar] = tmp[i+1][randVar];
				switchNum[randVar]= 1;				
			}
			
			//SWITCHING THE TASK IN CYCLE CROSSOVER			
			for(l=0;l<sizeSlideWindow;l++)
			{
				if(switchNum[l]==0)
				{
					tmp[pop_size +i][l] = tmp[i+1][l];
					tmp[pop_size +1+i][l] = tmp[i][l];
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
			
			//COPYING THE NEWLY GENERATED CHILD2 INTO  CHROMOSOME ARRAY(1000+i)
			for(k=0;k<input.no_Of_Proc;k++)
			{
				counter[pop_size +i][k] = counter[P1][k];
				for(j=0;j<counter[pop_size +i][k];j++)
				{				
					chromosomes[pop_size +i][k][j]= tmp[pop_size +i][x];

					x++;
				}
			}
			x=0;
			
			//COPYING THE NEWLY GENERATED CHILD2 INTO  CHROMOSOME ARRAY(1001+i)
			for(k=0;k<input.no_Of_Proc;k++)
			{
				counter[pop_size +1+i][k] = counter[P2][k];
				for(j=0;j<counter[pop_size+1+i][k];j++)
				{				
					chromosomes[pop_size +1+i][k][j] = tmp[pop_size +1+i][x];
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
	
	
	//MUTATION (RANDOMLY SELECT TWO TAST AND INTERCHANGE THEM)
	static void Mutation(int num,int inc1)
	{
		int j,k,randT1,randT2;
		Random rand = new Random();
		
		//RANDOMLY SELECT TWO TASK TO INTERCHANGE
		randT1 = rand.nextInt(sizeSlideWindow);
		randT2 = rand.nextInt(sizeSlideWindow);
		
		//CHECK IF THE TWO TASK ARE NOT SAME
		while(randT1==randT2)
		{
			randT2 = rand.nextInt(sizeSlideWindow);
		}
		for(k=0;k<input.no_Of_Proc;k++)
		{
			counter[2*pop_size +num][k] =counter[pop_size +num][k];
			for(j=0;j<counter[2*pop_size +num][k];j++)
			{				
				if(chromosomes[pop_size +num][k][j] == randT1+inc1)
					chromosomes[2*pop_size +num][k][j] = randT2+inc1;
				else
				{
					if(chromosomes[pop_size +num][k][j] == randT2+inc1)
						chromosomes[2*pop_size +num][k][j] = randT1+inc1;
					else
						chromosomes[2*pop_size +num][k][j] = chromosomes[pop_size +num][k][j];
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
	
	//RANDOM ALLOCATION ALGORITHM(FOR COMPARISON )
	static void RA()
	{
		Random rand = new Random();
		int k,tmp,l,z,j,i,scheduleTime=0;
		scheduleRA = new int [input.no_Of_Proc][input.no_Of_Task];
		int counterRA[]= new int[input.no_Of_Proc];
		int RAchromosomes[][] =new int[input.no_Of_Proc][sizeSlideWindow]; 
		int RAcounter[]= new int[input.no_Of_Proc];
		int RAprocAvT[]=new int[input.no_Of_Proc];
		for(z=0;z<input.no_Of_Proc;z++)
		{
			RAcounter[z]=0;
			RAprocAvT[z]=0;
			counterRA[z]=0;
		}
		for(i=0;i<input.no_Of_Task;i+=sizeSlideWindow)
		{
			//System.out.print(i+"=");
			for(z=0;z<input.no_Of_Proc;z++)
			{
				RAcounter[z]=0;
			}
			for(k=i;k<(i+sizeSlideWindow);k++)
			{
					tmp = rand.nextInt(input.no_Of_Proc);	
					RAchromosomes[tmp][RAcounter[tmp]] = k;
					//System.out.print(RAchromosomes[tmp][RAcounter[tmp]]+" ");
					RAcounter[tmp]++;
			}
			System.out.println();
			
			float avgUtilization =0 ,sum =0,fitness=0;
						
			for(l=0;l<input.no_Of_Proc;l++)
			{
				int maxDAT=RAprocAvT[l];
				for(j=0;j<RAcounter[l];j++)
				{				
						maxDAT+= input.inputTask.exe_time[(RAchromosomes[l][j])]/proc_speed[l];
						scheduleRA[l][counterRA[l]+j]=RAchromosomes[l][j];
				}
				counterRA[l]+=RAcounter[l];
				RAprocAvT[l]=maxDAT;
				scheduleTime = maX(scheduleTime,maxDAT);
				//procAvT[i] = maxDAT ;
				//sum+=maxDAT; 
			}
			//FOR LOAD BALANCING
			//avgUtilization = (sum / (input.no_Of_Proc*scheduleTime)) ; 						
			//fitness = (avgUtilization/scheduleTime)*1000;			
			//System.out.println(avgUtilization+" "+fitness);
			System.out.print("No. of Task ="+(i+sizeSlideWindow)+" , ");
			//System.out.print("Population Size ="+pop_size+" , ");
			//System.out.print("No of Processors="+input.no_Of_Proc+" , ");
			//System.out.print("Sliding Window="+sizeSlideWindow+" , ");
			int max=0,y;
			sum=0;
			for(y=0;y<input.no_Of_Proc;y++)
			{
				if(max<RAprocAvT[y])
					max= RAprocAvT[y];
				sum+=RAprocAvT[y];
			}
			System.out.print("Makespan ="+max+" , ");
			System.out.print("Utilization= "+sum/(input.no_Of_Proc*max)+"\n");
		}
		System.out.println();
		for(l=0;l<input.no_Of_Proc;l++)
		{
			System.out.print("Processor #"+l+"=");
			for(j=0;j<counterRA[l];j++)
			{				
					System.out.print(scheduleRA[l][j]+" ");
			}
			System.out.print("="+RAprocAvT[l]);
			System.out.println();
		}
	}
	
	
	//MAIN METHOD
	public static void main(String [] args)
	{
		//TEMPRARY VARIABLES
		int i=0,maxFitId=0,j=0,k,inc=0,gen,l=0,flip=0,z=0;;
		float maxF = 0,sum=0;
		float maxFit=0;

		//TAKE THE INPUT FROM FILE
		input.takeinput();
		
		//ALLOCATE MEMORY TO VARIOUS ARRAY
		chromosomes= new int[3*pop_size][input.no_Of_Proc][sizeSlideWindow];
		procAvT = new int[input.no_Of_Proc];	
		counter = new int[3*pop_size][input.no_Of_Proc]; 
		finalCount = new int[input.no_Of_Proc];
	 	fCount = new int[input.no_Of_Proc];
	 	fitNess = new float[3*pop_size];
	 	roulette = new float[pop_size];
		finalSchedule = new int [input.no_Of_Proc][sizeSlideWindow];
		finalDSchedule = new int [input.no_Of_Proc][input.no_Of_Task];
	 	proc_speed=new float[input.no_Of_Proc];
	 	maxFitNess  = new float [3*pop_size];
	 	
	 	//INITIALIZE THE PROCESSORS SPEED
		speed_initialization();
		for(inc=0;inc<input.no_Of_Task;inc+=sizeSlideWindow)
		{
			maxFit=0;
			maxFitId=0;
			
			//CREATE POPULATION OF POP_SIZE
			createPopulation(inc);
			
			//HOW MANY GENERATIONS YOU WANT TO COMPUTE
			gen=100;
			//System.out.print("Maximum Generations ="+gen+" , ");
			while(gen>0)
			{
				//STORE THE FITNESS OF CHROMOSOME
				for(i=0;i<pop_size ;i++)
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
				
				//APPLY CYCLE CROSSOVER OPERATOR ON CURRENT SOLUTION SET AND STORE CHILDS IN CHROMOSOME(1000-2000)
				cycleCrosssover();
				
				//MUTATE THE OFFSPRING STORES IN CHROMOSOME(2000-3000)
				for(i=0;i<pop_size ;i++)
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
				}*/
				
				//FIND THE FITTEST SCHEDULE
				flip=0;
				for(i=0;i<3*pop_size ;i++)
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
					//maxFitNess[inc] = maxFit;
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
				
				
				//CREATE NEW POPULATION WHICH CONSIST OF FITTEST OFFSPRING
				for(i=0;i<pop_size ;i++)
				{
					if(fitNess[i]>=fitNess[pop_size +i]&&fitNess[i]>=fitNess[2*pop_size +i])
						l=i;
					else
					{
						if(fitNess[i]<fitNess[pop_size +i]&&fitNess[pop_size +i]>fitNess[2*pop_size +i])
							l=pop_size +i;
						else
							l=2*pop_size +i;
					}
					
					//System.out.println(fitNess[i]+" "+fitNess[1000+i]+" "+fitNess[2000+i]+"="+l);
					if(l>=pop_size )
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
				
				
			
				//SET ALL TO NULL
				for(i=pop_size ;i<3*pop_size ;i++)
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

			
			/*for(k=0;k<input.no_Of_Proc;k++)
			{
				//System.out.print(" roy ");
				for(j=0;j<fCount[k];j++)
				{				
					System.out.print(finalSchedule[k][j]+" ");
				}
				System.out.println();
			}*/
			//System.out.println(maxFitNess[inc]);
		
			//==============
			//SAVE THE BEST SCHEDULE OF CURRENT SLIDINNG WINDOW INTO FINAL SCHEDULE
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
				//System.out.print(procAvT[k]+" ");
				finalCount[k]+=fCount[k];	
				maxF=maXF(maxF,procAvT[k]);
				sum+=procAvT[k];
				//System.out.println(" "+procAvT[i]);
			}
			//System.out.print("No. of Task ="+(inc+sizeSlideWindow)+" , ");
			//System.out.print("Population Size ="+pop_size+" , ");
			//System.out.print("No of Processors="+input.no_Of_Proc+" , ");
			System.out.print("Sliding Window="+sizeSlideWindow+" , ");
			System.out.print("Makespan ="+maxF+" , ");
			System.out.print("Utilization= "+sum/(input.no_Of_Proc*maxF)+"\n");
		
			
			
			//SET ALL TO 0			
			for(z=0;z<3*pop_size ;z++)
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
		
		//RANDOM ALLOCATION ALGORITHM
		//RA();
	}
}