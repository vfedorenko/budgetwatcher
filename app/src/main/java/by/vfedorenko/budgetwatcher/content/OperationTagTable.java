package by.vfedorenko.budgetwatcher.content;

import android.content.ContentValues;

public class OperationTagTable {
	public static final String TABLE_NAME = "operation_tag";

	public static final String ID = "_id";
	public static final String OPERATION_ID = "operation_id";
	public static final String TAG_ID = "tag_id";

	public static final class Builder {
		private final ContentValues values = new ContentValues();

		public Builder id(long id) {
			values.put(ID, id);
			return this;
		}

		public Builder operationId(long id) {
			values.put(OPERATION_ID, id);
			return this;
		}

		public Builder tagId(long id) {
			values.put(TAG_ID, id);
			return this;
		}

		public ContentValues build() {
			return values; // TODO defensive copy?
		}
	}
}
