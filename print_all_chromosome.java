for(z=0;z<1000;z++)
{	
	for(k=0;k<input.no_Of_Proc;k++)
	{
		for(j=0;j<counter[z][k];j++)
			System.out.print(chromosomes[z][k][j]+" ");
		System.out.print(" ");
	}
	System.out.print("\t");
}
System.out.print("\n");
