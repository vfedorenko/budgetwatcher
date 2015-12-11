package by.vfedorenko.budgetwatcher.viewmodels;

import android.databinding.BaseObservable;
import android.text.Editable;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import by.vfedorenko.budgetwatcher.activities.AddTagsActivity;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.realm.OperationTag;
import by.vfedorenko.budgetwatcher.realm.Tag;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddTagsViewModel extends BaseObservable {
	private String mTagName;
	private long mOperationDate;

	private AddTagsActivity.OnTagsUpdatedListener mOnTagsUpdatedListener;

	public AddTagsViewModel(AddTagsActivity.OnTagsUpdatedListener onTagsUpdatedListener, long operationDate) {
		mOnTagsUpdatedListener = onTagsUpdatedListener;
		mOperationDate = operationDate;
		refreshTags("");
	}

	public void onTagChanged(Editable str) {
		mTagName = str.toString();
		refreshTags(mTagName);
	}

	public void onAddClick(View v) {
		createTag(mTagName);
	}

	public void onTagClicked(AdapterView<?> parent, View view, int position, long id) {
		String tagName = (String) parent.getAdapter().getItem(position);

		Realm realm = Realm.getDefaultInstance();

		if (realm.where(Operation.class).equalTo(Operation.FIELD_DATE, mOperationDate).equalTo("tags.tag.name", tagName).findAll().size() == 0) {
			Operation operation = realm.where(Operation.class).equalTo(Operation.FIELD_DATE, mOperationDate).findFirst();
			Tag tag = realm.where(Tag.class).equalTo(Tag.FIELD_NAME, tagName).findFirst();

			realm.beginTransaction();
			OperationTag operationTag = realm.createObject(OperationTag.class);
			operationTag.setPercent(100);
			operationTag.setTag(tag);

			operation.getTags().add(operationTag);
			realm.commitTransaction();
		}
	}

	public boolean deleteTag(String tagName) {
		Realm realm = Realm.getDefaultInstance();

		long count = realm.where(Operation.class).equalTo("tags.tag.name", tagName).count();
		if (count > 0) {
			// Cannot delete used tag
			return false;
		}

		realm.beginTransaction();
		realm.where(Tag.class).equalTo(Tag.FIELD_NAME, tagName).findAll().clear();
		realm.commitTransaction();

		refreshTags(mTagName);
		return true;
	}

	private void createTag(String tagName) {
		Realm realm = Realm.getDefaultInstance();

		if (realm.where(Tag.class).equalTo(Tag.FIELD_NAME, tagName).findAll().size() == 0) {
			realm.beginTransaction();
			Tag tag = realm.createObject(Tag.class);
			tag.setName(tagName);
			realm.commitTransaction();

			refreshTags(mTagName);
		}
	}

	private void refreshTags(String query) {
		Realm realm = Realm.getDefaultInstance();
		RealmResults<Tag> results = realm.where(Tag.class).contains(Tag.FIELD_NAME, query).findAll();

		List<String> tags = new ArrayList<>();
		for(Tag tag : results) {
			tags.add(tag.getName());
		}
		mOnTagsUpdatedListener.onTagsUpdated(tags);
	}
}
