package by.vfedorenko.budgetwatcher.content;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class BudgetProvider extends ContentProvider {
	private DatabaseManager databaseManager_;

	private static final String AUTHORITY = "by.vfedorenko.budgetwatcher.content.BudgetProvider";

	public static final int ALL_OPERATIONS = 1;

	private static final String SCHEME_CONTENT = "content://";

	private static final String OPERATION_BASE_PATH = "operation";

	public static final Uri CONTENT_URI_OPERATIONS = Uri.parse(SCHEME_CONTENT + AUTHORITY + "/" + OPERATION_BASE_PATH);

	private static final UriMatcher URIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		URIMatcher.addURI(AUTHORITY, OPERATION_BASE_PATH, ALL_OPERATIONS);
	}

	@Override
	public boolean onCreate() {
		databaseManager_ = new DatabaseManager(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		int uriType = URIMatcher.match(uri);
		String groupByString = "";
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		switch (uriType) {
			case ALL_OPERATIONS:
				queryBuilder.setTables(OperationsTable.TABLE_NAME);
				break;
		}

		Cursor cursor = queryBuilder.query(databaseManager_.getReadableDatabase(), projection, selection, selectionArgs,
				groupByString, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = URIMatcher.match(uri);
		Uri resultUri = null;
		long rowID;
		switch (uriType) {
			case ALL_OPERATIONS:
				rowID = databaseManager_.getWritableDatabase().insert(OperationsTable.TABLE_NAME, null, values);
				resultUri = ContentUris.withAppendedId(CONTENT_URI_OPERATIONS, rowID);
				break;
		}

		getContext().getContentResolver().notifyChange(resultUri, null);
		return resultUri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = URIMatcher.match(uri);
		int count = 0;

		switch (uriType) {
			case ALL_OPERATIONS:
				count = databaseManager_.getWritableDatabase().delete(OperationsTable.TABLE_NAME, selection, selectionArgs);
				getContext().getContentResolver().notifyChange(uri, null);
				break;
		}

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}
}
