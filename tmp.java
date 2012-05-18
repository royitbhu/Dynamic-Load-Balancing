maxFit=0;				
			
			while(gen>0)
			{
				
				
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
				
	//			System.out.println(roulette_Selection());
	//			System.out.println(roulette_Selection());
	
	//			Apply the cycle Crossover operator			
				flip=0;
				//System.out.print(" roy ");
				cycleCrosssover();
				//System.out.print(" roy ");
	//			Mutate the currently generated childs			
				for(i=0;i<1000;i++)
				{
					Mutation(i);
				}
			//	System.out.print(" roy ");
/*				for(k=0;k<input.no_Of_Proc;k++)
				{
//					System.out.print(" roy ");
					for(j=0;j<counter[1004][k];j++)
					{				
						System.out.print(chromosomes[1004][k][j]+" ");
//						
					}
				}
				for(k=0;k<input.no_Of_Proc;k++)
				{
//					System.out.print(" roy ");
					for(j=0;j<counter[2004][k];j++)
					{				
						System.out.print(chromosomes[2004][k][j]+" ");
//						
					}
				}*/
				for(i=1000;i<3000;i++)
				{
	//				System.out.print(fitNessCal(i)+" ");
					fitNess[i] = fitNessCalwLoadBal(i);
	//				fitNess[i] = fitNessCalwoLoadBal(i);
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
		//					System.out.print(finalSchedule[k][j]);
						}
						fCount[j]=counter[maxFitId][k];
					}
				}
	//			System.out.println(" "+maxFit);
				
	/*			Mutation();
				
				for(i=1000;i<2000;i++)
				{
	//				System.out.print(fitNessCal(i)+" ");
					fitNess[i] = fitNessCalwLoadBal(i);
	//				fitNess[i] = fitNessCalwoLoadBal(i);
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
						for(j=0;j<sizeSlideWindow;j++)
						{				
							finalSchedule[k][j] = 0;
						}
					}			
					for(k=0;k<input.no_Of_Proc;k++)
					{
	//					System.out.print(" roy ");
						for(j=0;j<counter[maxFitId][k];j++)
						{				
							finalSchedule[k][j] = chromosomes[maxFitId][k][j];
	//						System.out.print(finalSchedule[k][j]);
						}
					}
				}
	*/	//		System.out.println(" "+maxFit);

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
					if(l>=1000)
					{
						for(k=0;k<input.no_Of_Proc;k++)
						{
							for(j=0;j<counter[i][k];j++)
							{				
								chromosomes[i][k][j] =0;
			//					System.out.print(finalSchedule[k][j]);
							}
							counter[i][k]=0;
						}
						for(k=0;k<input.no_Of_Proc;k++)
						{
							for(j=0;j<counter[l][k];j++)
							{				
								chromosomes[i][k][j] =chromosomes[l][k][j];
			//					System.out.print(finalSchedule[k][j]);
							}
							counter[i][k]=counter[l][k];
						}
					}
/*					for(k=0;k<input.no_Of_Proc;k++)
					{
//						System.out.print(" roy ");
						for(j=0;j<counter[i][k];j++)
						{				
							System.out.print(chromosomes[i][k][j]+" ");
//							
						}
					}
					System.out.println();*/
				}
				
				//				Set all to 0
				for(i=1000;i<3000;i++)
				{
		//			System.out.print(fitNessCal(i)+" ");
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

				gen--;
		}
maxF=0;sum=0;
			

		
			
			
			
			//	for(i=0;i<input.no_Of_Proc;i++)
	//	{
	//		System.out.print(procAvT[i]+" ");
	//	}
		/*maxF = 0;sum=0;
		for(i=0;i<input.no_Of_Proc;i++)
		{
			System.out.print("Processor# "+i+": ");
			for(j=0;j<finalCount[i];j++)
			{				
				System.out.print(finalDSchedule[i][j]+" ");
			}
	//		maxF=maXF(maxF,procAvT[i]);
	//		sum+=procAvT[i];
	//		System.out.print(" = "+procAvT[i]);
			System.out.println();
		}*/
	//	System.out.println("Final avg utilization = "+sum/(input.no_Of_Proc*maxF));
