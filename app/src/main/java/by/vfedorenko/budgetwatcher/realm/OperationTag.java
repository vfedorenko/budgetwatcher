package by.vfedorenko.budgetwatcher.realm;

import io.realm.RealmObject;

public class OperationTag extends RealmObject {
	private float percent;
	private Tag tag;

	public float getPercent() {
		return percent;
	}

	public void setPercent(float percent) {
		this.percent = percent;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
