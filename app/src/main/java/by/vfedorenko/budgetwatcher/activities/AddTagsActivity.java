package by.vfedorenko.budgetwatcher.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.databinding.ActivityAddTagsBinding;
import by.vfedorenko.budgetwatcher.viewmodels.AddTagsViewModel;

public class AddTagsActivity extends AppCompatActivity {
    public interface OnTagsUpdatedListener {
        void onTagsUpdated(List<String> tags);
    }

    private static final String KEY_OPERATION_DATE = "KEY_OPERATION_DATE";

    private ActivityAddTagsBinding mBinding;
    private ArrayAdapter<String> mTagsAdapter;

    public static Intent createIntent(Context context, long date) {
        Intent intent = new Intent(context, AddTagsActivity.class);
        intent.putExtra(KEY_OPERATION_DATE, date);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long date = getIntent().getLongExtra(KEY_OPERATION_DATE, -1);

        mTagsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());

        OnTagsUpdatedListener onTagsUpdatedListener = new OnTagsUpdatedListener() {
            @Override
            public void onTagsUpdated(List<String> tags) {
                mTagsAdapter.clear();
                mTagsAdapter.addAll(tags);
            }
        };

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_tags);
        AddTagsViewModel viewModel = new AddTagsViewModel(onTagsUpdatedListener, date);
        mBinding.setTags(viewModel);

        mBinding.tagsListView.setAdapter(mTagsAdapter);
        setSupportActionBar(mBinding.toolbar);
        registerForContextMenu(mBinding.tagsListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tag_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (!mBinding.getTags().deleteTag(mTagsAdapter.getItem(info.position))) {
                    Toast.makeText(this, "Tag in use", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
