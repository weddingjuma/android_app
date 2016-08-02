package com.acs.mainmenu;
// ******************************************************************* //
// ** Chapter 7 Code Listings                                       ** //
// ** Professional Android 2 Application Development                ** //
// ** Reto Meier                                                    ** //
// ** (c)2010 Wrox                                                  ** //
// ******************************************************************* //

// ** SQLite Databases ******************************************** //

// *******************************************************************
// ** Listing 7-1: Skeleton code for a standard database adapter implementation
// used as the template for this implementation 

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

public class AcsDBAdapter {
  private static final String DATABASE_NAME = "acsDatabase.db";
  private static final String DATABASE_TABLE = "mainTable";
  private static final int DATABASE_VERSION = 6;
  public static final int RECORDSZ = 19;
 
  // The index (key) column name for use in where clauses.
  public static final String KEY_ID="_id";
  public static final int ID_COLUMN = 0;
  // The name and column index of each column in your database.
  public static final String KEY_STORENAME="storename"; 
  public static final int STORENAME_COLUMN = 1;
  public static final String KEY_DRIVER="driver"; 
  public static final int DRIVER_COLUMN = 2;
  public static final String KEY_INVOICE="invoice"; 
  public static final int INVOICE_COLUMN = 3;
  public static final String KEY_CUST="customer"; 
  public static final int CUST_COLUMN = 4;
  public static final String KEY_TYPE="type"; 
  public static final int TYPE_COLUMN = 5;
  public static final String KEY_AMT="amount"; 
  public static final int AMT_COLUMN = 6;
  public static final String KEY_START="started"; 
  public static final int START_COLUMN = 7;
  public static final String KEY_PRIORITY="priority"; 
  public static final int PRIORITY_COLUMN = 8;
  public static final String KEY_STORENO="location";
  public static final int STORENO_COLUMN = 9;
  public static final String KEY_SIGFILE="signature"; 
  public static final int SIGFILE_COLUMN = 10;
  public static final String KEY_SIGDATE ="sigdate";
  public static final int SIGDATE_COLUMN = 11;
  public static final String KEY_CUSTNUM ="custnum";
  public static final int CUSTNUM_COLUMN = 12;
  public static final String KEY_ADDRESS="address";
  public static final int ADDRESS_COLUMN = 13;
  public static final String KEY_LAT="latitude";
  public static final int LAT_COLUMN = 14;
  public static final String KEY_LONG="longitude";
  public static final int LONG_COLUMN = 15;
  public static final String KEY_ACTION="actionitem";
  public static final int ACTION_COLUMN = 16;
  public static final String KEY_DISPATCH="dispatch";
  public static final int DISPATCH_COLUMN = 17;
  public static final String KEY_QTY="quantity";
  public static final int QTY_COLUMN = 18;

  private static final String[] cols = new String[] {KEY_ID, KEY_STORENAME, KEY_DRIVER, KEY_INVOICE, KEY_CUST, KEY_TYPE, 
	  KEY_AMT, KEY_START, KEY_PRIORITY, KEY_STORENO, KEY_SIGFILE, KEY_SIGDATE, KEY_CUSTNUM, KEY_ADDRESS, 
	  KEY_LAT, KEY_LONG, KEY_ACTION, KEY_DISPATCH, KEY_QTY};

  // SQL Statement to create a new database.
  private static final String DATABASE_CREATE = "create table " + 
    DATABASE_TABLE + " (" + KEY_ID + 
    " integer primary key autoincrement, " +
    KEY_STORENAME + " text not null, " +
    KEY_DRIVER + " text not null, " +
    KEY_INVOICE + " text not null, " +
    KEY_CUST + " text, " +
    KEY_TYPE + " text, " +
    KEY_AMT + " text not null, " +
    KEY_START + " text, " +
    KEY_PRIORITY + " text, " +
    KEY_STORENO + " text, " +
    KEY_SIGFILE + " text, " +
    KEY_SIGDATE + " text, " +
    KEY_CUSTNUM + " text, " +
    KEY_ADDRESS + " text, " +
    KEY_LAT + " text, " + 
    KEY_LONG + " text, " + 
    KEY_ACTION + " text," +
    KEY_DISPATCH + " text," +
  	KEY_QTY + " text );";

  // Variable to hold the database instance
  private SQLiteDatabase db;
  // Context of the application using the database.
  private final Context context;
  // Database open/upgrade helper
  private acsDbHelper dbHelper;

  public AcsDBAdapter(Context _context) {
    context = _context;
    dbHelper = new acsDbHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  public AcsDBAdapter open() throws SQLException {
    db = dbHelper.getWritableDatabase();
    return this;
  }

  public void close() {
      db.close();
  }
  
  public int getVersion()
  {
	  return DATABASE_VERSION;
  }

  public int insertEntry(Object _Object) {
	int index = -1;
	ContentValues newvals = new ContentValues();
	String[] pvals = (String [])_Object;

	newvals.put(KEY_STORENAME, pvals[STORENAME_COLUMN]);
	newvals.put(KEY_DRIVER,    pvals[DRIVER_COLUMN]);
	newvals.put(KEY_INVOICE,   pvals[INVOICE_COLUMN]);
	newvals.put(KEY_CUST,      pvals[CUST_COLUMN]);
	newvals.put(KEY_TYPE,      pvals[TYPE_COLUMN]);
	newvals.put(KEY_AMT,       pvals[AMT_COLUMN]);
	newvals.put(KEY_START,     pvals[START_COLUMN]);
	newvals.put(KEY_PRIORITY,  pvals[PRIORITY_COLUMN]);	  
	newvals.put(KEY_STORENO,   pvals[STORENO_COLUMN]);
	newvals.put(KEY_SIGFILE,   pvals[SIGFILE_COLUMN]);
	newvals.put(KEY_SIGDATE,   pvals[SIGDATE_COLUMN]);
	newvals.put(KEY_CUSTNUM,   pvals[CUSTNUM_COLUMN]);
	newvals.put(KEY_ADDRESS,   pvals[ADDRESS_COLUMN]);
	newvals.put(KEY_LAT,       pvals[LAT_COLUMN]);
	newvals.put(KEY_LONG,      pvals[LONG_COLUMN]);
	newvals.put(KEY_ACTION,    pvals[ACTION_COLUMN]);
	newvals.put(KEY_DISPATCH,  pvals[DISPATCH_COLUMN]);
	newvals.put(KEY_QTY,  	   pvals[QTY_COLUMN]);

	try {
		index = (int) this.db.insert(DATABASE_TABLE, null, newvals);		
	} 
	catch(Exception e) {
		String msg = e.getMessage();
		e.printStackTrace();
	}
    return index;
  }
  
  public int checkSignatures(){
	  int rv = 0;
	  try {
		  Cursor cur = db.rawQuery("SELECT COUNT(*) FROM mainTable where signature is NOT NULL",null);
		  if(cur != null) {
			  cur.moveToFirst();
		  		rv = cur.getInt(0);
	  }	
	} catch (Exception e) {
		String errMsg;
		e.printStackTrace();
		errMsg = e.getMessage();
	}

	return rv;
  }

  public boolean removeEntry(long _rowIndex) {
    return db.delete(DATABASE_TABLE, KEY_ID + "=" + _rowIndex, null) > 0;
  }

  public Cursor getAllEntries () {
	  String ordby = KEY_PRIORITY + " ASC";
    return db.query(DATABASE_TABLE, cols, null, null, null, null, ordby, null);
  }
  
  public Cursor getCustNum(String custnum) {
	  custnum = custnum.replaceAll("\\s", "");
	  String where = KEY_CUSTNUM + "=" + "\"" + custnum + "\"";
	  return db.query(DATABASE_TABLE, cols, where, null, null, null, null, null);
	  
  }

  public Cursor getCustByName(String cust) {
	  String where = KEY_CUST + "=" + "\"" + cust + "\"";
	  return db.query(DATABASE_TABLE, cols, where, null, null, null, null, null);
	  
  }

  public Object getEntry(long _rowIndex) {
	  String[] res = new String[RECORDSZ];
	  for (int i=0; i < RECORDSZ; i++)
		  res[i] = null;
	  String where = KEY_ID + "=" + _rowIndex;
	  Cursor row = db.query(DATABASE_TABLE, cols, where, null, null, null, null);

	  if(row.moveToFirst()) {
		  res[ID_COLUMN] = String.valueOf(row.getInt(ID_COLUMN));
		  res[STORENAME_COLUMN] = row.getString(STORENAME_COLUMN);
		  res[DRIVER_COLUMN] = row.getString(DRIVER_COLUMN);
		  res[INVOICE_COLUMN] = row.getString(INVOICE_COLUMN);
		  res[CUST_COLUMN] = row.getString(CUST_COLUMN);
		  res[TYPE_COLUMN] = row.getString(TYPE_COLUMN);
		  res[AMT_COLUMN] = row.getString(AMT_COLUMN);
		  res[START_COLUMN] = row.getString(START_COLUMN);
		  res[PRIORITY_COLUMN] = row.getString(PRIORITY_COLUMN);
		  res[STORENO_COLUMN] = row.getString(STORENO_COLUMN);
		  res[SIGFILE_COLUMN] = row.getString(SIGFILE_COLUMN);
		  res[SIGDATE_COLUMN] = row.getString(SIGDATE_COLUMN);
		  res[CUSTNUM_COLUMN] = row.getString(CUSTNUM_COLUMN);
		  res[ADDRESS_COLUMN] = row.getString(ADDRESS_COLUMN);
		  res[LAT_COLUMN] = row.getString(LAT_COLUMN);
		  res[LONG_COLUMN] = row.getString(LONG_COLUMN);
		  res[ACTION_COLUMN] = row.getString(ACTION_COLUMN);		  
		  res[DISPATCH_COLUMN] = row.getString(DISPATCH_COLUMN);		  
		  res[QTY_COLUMN] = row.getString(QTY_COLUMN);		  
	  }
	  row.close();
	  return res;
  }

  public Object getAction(String _action) {
	  String[] res = new String[RECORDSZ];
	  for (int i=0; i < RECORDSZ; i++)
		  res[i] = null;
	  _action = _action.replace("\\s", "");
	  String where = KEY_ACTION + "=" +  "\"" + _action + "\"";
	  Cursor row = db.query(DATABASE_TABLE, cols, where, null, null, null, null);

	  if(row.moveToFirst()) {
		  res[ID_COLUMN] = String.valueOf(row.getInt(ID_COLUMN));
		  res[STORENAME_COLUMN] = row.getString(STORENAME_COLUMN);
		  res[DRIVER_COLUMN] = row.getString(DRIVER_COLUMN);
		  res[INVOICE_COLUMN] = row.getString(INVOICE_COLUMN);
		  res[CUST_COLUMN] = row.getString(CUST_COLUMN);
		  res[TYPE_COLUMN] = row.getString(TYPE_COLUMN);
		  res[AMT_COLUMN] = row.getString(AMT_COLUMN);
		  res[START_COLUMN] = row.getString(START_COLUMN);
		  res[PRIORITY_COLUMN] = row.getString(PRIORITY_COLUMN);
		  res[STORENO_COLUMN] = row.getString(STORENO_COLUMN);
		  res[SIGFILE_COLUMN] = row.getString(SIGFILE_COLUMN);
		  res[SIGDATE_COLUMN] = row.getString(SIGDATE_COLUMN);
		  res[CUSTNUM_COLUMN] = row.getString(CUSTNUM_COLUMN);
		  res[ADDRESS_COLUMN] = row.getString(ADDRESS_COLUMN);
		  res[LAT_COLUMN] = row.getString(LAT_COLUMN);
		  res[LONG_COLUMN] = row.getString(LONG_COLUMN);
		  res[ACTION_COLUMN] = row.getString(ACTION_COLUMN);		  
		  res[DISPATCH_COLUMN] = row.getString(DISPATCH_COLUMN);		  
		  res[QTY_COLUMN] = row.getString(QTY_COLUMN);		  
	  }
	  row.close();
	  return res;
  }
  
  public int getRowIndex(String invno){
	  int ndx = -1;
	  // clean up string
	  invno = invno.replaceAll("\\s", "");
	  String[] cols = new String[] {KEY_ID, KEY_INVOICE };
	  String where = KEY_INVOICE + "=" + "\"" + invno + "\"";
	  Cursor row = db.query(DATABASE_TABLE, cols, where, null, null, null, null);
	  if(row.moveToFirst()){
		  ndx = row.getInt(ID_COLUMN);
	  }
	  row.close();
	  return ndx;
  }

  public int getActionIndex(String action){
	  int ndx = -1;
	  // clean up string
	  action = action.replaceAll("\\s", "");
	  String[] cols = new String[] {KEY_ID, KEY_ACTION };
	  String where = KEY_ACTION + "=" + "\"" + action + "\"";
	  Cursor row = db.query(DATABASE_TABLE, cols, where, null, null, null, null);
	  if(row.moveToFirst()){
		  ndx = row.getInt(ID_COLUMN);
	  }
	  row.close();
	  return ndx;
  }
  
  public Object getInvoice(String invno) {
	  invno = invno.replaceAll("\\s", "");
	  String[] res = new String[RECORDSZ];
	  for (int i=0; i < RECORDSZ; i++)
		  res[i] = null;
	  String where = KEY_INVOICE + "=" + "\"" + invno + "\"";
	  Cursor row = db.query(DATABASE_TABLE, cols, where, null, null, null, null);

	  if(row.moveToFirst()) {
		  res[ID_COLUMN] = String.valueOf(row.getInt(ID_COLUMN));
		  res[STORENAME_COLUMN] = row.getString(STORENAME_COLUMN);
		  res[DRIVER_COLUMN] = row.getString(DRIVER_COLUMN);
		  res[INVOICE_COLUMN] = row.getString(INVOICE_COLUMN);
		  res[CUST_COLUMN] = row.getString(CUST_COLUMN);
		  res[TYPE_COLUMN] = row.getString(TYPE_COLUMN);
		  res[AMT_COLUMN] = row.getString(AMT_COLUMN);
		  res[START_COLUMN] = row.getString(START_COLUMN);
		  res[PRIORITY_COLUMN] = row.getString(PRIORITY_COLUMN);
		  res[STORENO_COLUMN] = row.getString(STORENO_COLUMN);
		  res[SIGFILE_COLUMN] = row.getString(SIGFILE_COLUMN);
		  res[SIGDATE_COLUMN] = row.getString(SIGDATE_COLUMN);
		  res[CUSTNUM_COLUMN] = row.getString(CUSTNUM_COLUMN);
		  res[ADDRESS_COLUMN] = row.getString(ADDRESS_COLUMN);
		  res[LAT_COLUMN] = row.getString(LAT_COLUMN);
		  res[LONG_COLUMN] = row.getString(LONG_COLUMN);
		  res[ACTION_COLUMN] = row.getString(ACTION_COLUMN);		  
		  res[DISPATCH_COLUMN] = row.getString(DISPATCH_COLUMN);		  
		  res[QTY_COLUMN] = row.getString(QTY_COLUMN);		  
	  }
	  row.close();
	  return res;
  }
  
  public boolean updateEntry(long _rowIndex, Object _Object) {
	ContentValues newvals = new ContentValues();
	String[] pvals = (String [])_Object;

	newvals.put(KEY_STORENAME, pvals[STORENAME_COLUMN]);
	newvals.put(KEY_DRIVER,    pvals[DRIVER_COLUMN]);
	newvals.put(KEY_INVOICE,   pvals[INVOICE_COLUMN]);
	newvals.put(KEY_CUST,      pvals[CUST_COLUMN]);
	newvals.put(KEY_TYPE,      pvals[TYPE_COLUMN]);
	newvals.put(KEY_AMT,       pvals[AMT_COLUMN]);
	newvals.put(KEY_START,     pvals[START_COLUMN]);
	newvals.put(KEY_PRIORITY,  pvals[PRIORITY_COLUMN]);	  
	newvals.put(KEY_STORENO,   pvals[STORENO_COLUMN]);
	newvals.put(KEY_SIGFILE,   pvals[SIGFILE_COLUMN]);
	newvals.put(KEY_SIGDATE,   pvals[SIGDATE_COLUMN]);
	newvals.put(KEY_CUSTNUM,   pvals[CUSTNUM_COLUMN]);
	newvals.put(KEY_ADDRESS,   pvals[ADDRESS_COLUMN]);
	newvals.put(KEY_LAT,       pvals[LAT_COLUMN]);
	newvals.put(KEY_LONG,      pvals[LONG_COLUMN]);
	newvals.put(KEY_ACTION,    pvals[ACTION_COLUMN]);
	newvals.put(KEY_DISPATCH,  pvals[DISPATCH_COLUMN]);
	newvals.put(KEY_QTY,  	   pvals[QTY_COLUMN]);
	
	String where = KEY_ID + "=" + _rowIndex;
    db.update(DATABASE_TABLE, newvals, where, null);
	
    return true;
  }

  private static class acsDbHelper extends SQLiteOpenHelper {

    public acsDbHelper(Context context, String name, 
                      CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one. 
    @Override
    public void onCreate(SQLiteDatabase _db) {
      _db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " + 
                             _oldVersion + " to " +
                             _newVersion + ", which will destroy all old data");
        
      // Upgrade the existing database to conform to the new version. Multiple 
      // previous versions can be handled by comparing _oldVersion and _newVersion
      // values.

      // The simplest case is to drop the old table and create a new one.
      // also the weakest but do we really need to keep the old data?
      // NOTE: this is primarily a have to do override not necessarily useful 
      _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      // Create a new one.
      onCreate(_db);
    }
  }
}
