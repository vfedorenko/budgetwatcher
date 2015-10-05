package by.vfedorenko.budgetwatcher.content;

import android.content.ContentValues;

public class TagsTable {
	public static final String TABLE_NAME = "tags";

	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String VALUE = "value";

	public static final class Builder {
		private final ContentValues values = new ContentValues();

		public Builder id(long id) {
			values.put(ID, id);
			return this;
		}

		public Builder name(String name) {
			values.put(NAME, name);
			return this;
		}

		public Builder value(long value) {
			values.put(VALUE, value);
			return this;
		}

		public ContentValues build() {
			return values; // TODO defensive copy?
		}
	}
}
