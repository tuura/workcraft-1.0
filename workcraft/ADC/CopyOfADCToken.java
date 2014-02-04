package workcraft.ADC;

public class CopyOfADCToken
{
	public boolean		valid;
	public int			time;
	
	public CopyOfADCToken()
	{
		valid = false;
		time = 0;
	}

	public CopyOfADCToken(boolean valid, int time)
	{
		this.valid = valid;
		this.time = time;
	}
	
	public CopyOfADCToken(CopyOfADCToken token)
	{
		valid = token.valid;
		time = token.time;
	}
}
