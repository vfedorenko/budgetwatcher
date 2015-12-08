package by.vfedorenko.budgetwatcher.realm;

import io.realm.RealmObject;

public class Tag extends RealmObject {
	public static final String FIELD_NAME = "name";
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
