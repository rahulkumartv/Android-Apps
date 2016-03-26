package com.striquaint.edumaster;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1; 
    // Database Name
    private static final String DATABASE_NAME = "EduMaster_database.db";
    private static String DB_PATH = "/data/data/com.striquaint.edumaster/databases/";
    private SQLiteDatabase m_Database; 
    private final Context m_DBContext;
	public DBHelper(Context dbContext ) {
		super( dbContext, DATABASE_NAME, null, DATABASE_VERSION );
		// TODO Auto-generated constructor stub
		this.m_DBContext = dbContext;
	}
	/**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DATABASE_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = m_DBContext.getAssets().open( DATABASE_NAME );
 
    	// Path to the just created empty DB
    	String outFileName = DB_PATH + DATABASE_NAME;
 
    	//Open the empty DB as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the input file to the output file
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
    public void openDataBase() throws SQLException{    	 
    	//Open the database 
    	try{ 
    		String myPath = DB_PATH + DATABASE_NAME;
    		//m_Database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    		m_Database = m_DBContext.openOrCreateDatabase(myPath, SQLiteDatabase.OPEN_READONLY|SQLiteDatabase.NO_LOCALIZED_COLLATORS,null);
    		    }
    		catch (Exception e) {
    			   e.printStackTrace();
    		}
    }
 
    @Override
	public synchronized void close() {
    	if( m_Database != null )
    		m_Database.close();
    	    super.close();
    	    }
	@Override	
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	//used this function for sql query to database
	public Cursor QueryRawSQL( String sqlQuery)
	{
		return m_Database.rawQuery( sqlQuery, null );
	}
}
