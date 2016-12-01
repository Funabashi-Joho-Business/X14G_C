package to.pns.lib;

import android.content.Context;
import android.content.Intent;

public class LogService 
{
	public static String UPDATE_NAME = "LOG_UPDATE";
	
	public static void output(Context con,String msg)
	{
		LogDB db = new LogDB(con);
		db.output(msg);
		db.close();	
		
		Intent broadcastIntent = new Intent(UPDATE_NAME);
		con.sendBroadcast(broadcastIntent);

	}
	public static void format(Context con,String f,Object... args)
	{
		String msg = String.format(f,args);

		LogDB db = new LogDB(con);
		db.output(msg);
		db.close();

		Intent broadcastIntent = new Intent(UPDATE_NAME);
		con.sendBroadcast(broadcastIntent);

	}

}