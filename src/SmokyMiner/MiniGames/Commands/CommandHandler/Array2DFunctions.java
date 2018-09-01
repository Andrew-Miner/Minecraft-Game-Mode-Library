package SmokyMiner.MiniGames.Commands.CommandHandler;

public class Array2DFunctions
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