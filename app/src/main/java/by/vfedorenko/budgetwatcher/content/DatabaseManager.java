package by.vfedorenko.budgetwatcher.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
	private static final String DB_FILE_NAME = "budget.db";
	private static final int DB_VERSION = 1;

	private static final String CREATE_OPERATIONS = ""
			+ "CREATE TABLE " + OperationsTable.TABLE_NAME + " ("
			+ OperationsTable.ID + " INTEGER PRIMARY KEY,"
			+ OperationsTable.TYPE + " INTEGER,"
			+ OperationsTable.AMOUNT + " INTEGER,"
			+ OperationsTable.DATE + " INTEGER"
			+ " );";

	private static final String CREATE_TAGS = ""
			+ "CREATE TABLE " + TagsTable.TABLE_NAME + " ("
			+ TagsTable.ID + " INTEGER PRIMARY KEY,"
			+ TagsTable.NAME + " TEXT,"
			+ TagsTable.VALUE + " INTEGER"
			+ " );";

	private static final String CREATE_OPERATION_TAG = ""
			+ "CREATE TABLE " + OperationTagTable.TABLE_NAME + " ("
			+ OperationTagTable.ID + " INTEGER PRIMARY KEY,"
			+ OperationTagTable.OPERATION_ID + " INTEGER,"
			+ OperationTagTable.TAG_ID + " INTEGER"
			+ " );";

	public DatabaseManager(Context context) {
		super(context, DB_FILE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_OPERATIONS);
		db.execSQL(CREATE_TAGS);
		db.execSQL(CREATE_OPERATION_TAG);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropDataBase();
		onCreate(db);
	}

	public void dropDataBase(){
		String[] tables = {OperationsTable.TABLE_NAME, TagsTable.TABLE_NAME, OperationTagTable.TABLE_NAME};

		SQLiteDatabase db = getWritableDatabase();
		for (String table : tables) {
			db.delete(table, null, null);
		}
	}
}

