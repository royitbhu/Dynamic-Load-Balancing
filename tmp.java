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
		
