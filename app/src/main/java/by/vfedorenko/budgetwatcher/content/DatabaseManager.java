package by.vfedorenko.budgetwatcher.content;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DatabaseManager extends SQLiteOpenHelper {
	private SQLiteDatabase db;

	public static class OperationsTable implements BaseColumns {
		public static final int TYPE_INCOMING = 1;
		public static final int TYPE_OUTGOING = 0;

		public static final String TABLE_NAME = "budgets";

		public static final String TYPE = "type";
		public static final String AMOUNT = "amount";
		public static final String DATE = "date";
		public static final String BALANCE = "balance";
	}

	private static final String DB_FILE_NAME = "budget.db";
	private static final int DB_VERSION = 1;

	public DatabaseManager(Context context) {
		super(context, DB_FILE_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;

		db.execSQL("CREATE TABLE " + OperationsTable.TABLE_NAME + " ("
				+ OperationsTable._ID + " INTEGER PRIMARY KEY,"
				+ OperationsTable.TYPE + " INTEGER,"
				+ OperationsTable.AMOUNT + " INTEGER,"
				+ OperationsTable.DATE + " INTEGER,"
				+ OperationsTable.BALANCE + " INTEGER"
				+ " );");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		dropDataBase();
		onCreate(db);
	}


	public void dropDataBase(){
		String[] tables = {OperationsTable.TABLE_NAME};

		SQLiteDatabase db = getWritableDatabase();
		for (String table : tables) {
			db.delete(table, null, null);
		}
	}
}

