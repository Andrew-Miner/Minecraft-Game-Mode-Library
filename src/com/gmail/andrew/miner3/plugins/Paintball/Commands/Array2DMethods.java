package com.gmail.andrew.miner3.plugins.Paintball.Commands;

public class Array2DMethods 
{
	public static void initializeArray(int[][] array, int maxRows, int maxCols, int value)
	{
		for(int i = 0; i < maxRows; i++)
		{
			for(int j = 0; j < maxCols; j++)
			{
				array[i][j] = value;
			}
		}
	}
}
