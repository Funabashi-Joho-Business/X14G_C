package to.pns.lib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;


public abstract class SQLite extends SQLiteOpenHelper
{
	private SQLiteDatabase mDataBase;
	public SQLite(Context context,String dbName,int version)
	{
		super(context, dbName, null, version);
	}
	@Override
	public synchronized void close() {
		if(mDataBase != null)
			mDataBase.close();
		super.close();
	}
	public long insert(String tableName, ContentValues v){
		return mDataBase.insert(tableName,null,v);
	}
	public long replace(String tableName, ContentValues v){
		if(mDataBase == null || mDataBase.isReadOnly())
		{
			if(mDataBase != null)
				mDataBase.close();
			mDataBase = getWritableDatabase();
		}
		return mDataBase.replace(tableName,null,v);
	}
	public boolean isTable(String name)
	{
		String sql = String.format("select name from sqlite_master where name='%s';",name);
		Cursor c = query(sql);
		boolean flag =  c.moveToFirst();
		c.close();
		return flag;
	}
	public boolean exec(String sql)
	{
		for(int i= 0;i<20;i++)
		{
			try {
				if(mDataBase == null || mDataBase.isReadOnly())
				{
					if(mDataBase != null)
						mDataBase.close();
					mDataBase = getWritableDatabase();
				}
				
				mDataBase.execSQL(sql);
				return true;
			}catch(SQLiteDatabaseLockedException e)
			{
				System.out.println(e);
				try{Thread.sleep(100);} catch (InterruptedException e1) {}
			} catch (SQLException e) {

				System.out.println(e);
				break;
			}
		}

		return false;
	}
	public Cursor query(String sql)
	{
		for(int i= 0;i<20;i++)
		{
			try {
				if(mDataBase == null)
					mDataBase = getReadableDatabase();
				return mDataBase.rawQuery(sql,null);
			}catch(SQLiteDatabaseLockedException e)
			{
				System.out.println(e);
				try{Thread.sleep(100);} catch (InterruptedException e1) {}
			} catch (SQLException e) {

				System.out.println(e);
				break;
			}
		}
		return null;
	}
	//SQLインジェクション対策
	public static String STR(String str)
	{
		//シングルクオートをシングルクオート二つにエスケーブ
		return str.replaceAll("'", "''");
	}

	public void begin()
	{
		exec("begin;");
	}
	public void commit()
	{
		exec("commit;");
	}
}
