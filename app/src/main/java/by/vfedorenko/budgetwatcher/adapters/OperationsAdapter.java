package by.vfedorenko.budgetwatcher.adapters;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import by.vfedorenko.budgetwatcher.BR;
import by.vfedorenko.budgetwatcher.R;
import by.vfedorenko.budgetwatcher.databinding.ItemOperationBinding;
import by.vfedorenko.budgetwatcher.realm.Operation;
import by.vfedorenko.budgetwatcher.viewmodels.OperationViewModel;

public class OperationsAdapter extends RecyclerView.Adapter<OperationsAdapter.BindingHolder> {
	public static class BindingHolder extends RecyclerView.ViewHolder {
		private ItemOperationBinding binding;

		public BindingHolder(View rowView) {
			super(rowView);
			binding = DataBindingUtil.bind(rowView);
		}

		public ViewDataBinding getBinding() {
			return binding;
		}
	}

	private List<Operation> mOperations;

	public OperationsAdapter(List<Operation> data) {
		mOperations = data;
	}

	@Override
	public BindingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_operation, parent, false);
		BindingHolder holder = new BindingHolder(v);
		return holder;
	}

	@Override
	public void onBindViewHolder(BindingHolder holder, int position) {
		Operation operation = mOperations.get(position);
		holder.getBinding().setVariable(BR.operation, new OperationViewModel(operation));
		holder.getBinding().executePendingBindings();
	}

	@Override
	public int getItemCount() {
		return mOperations.size();
	}

	// Clean all elements of the recycler
	public void clear() {
		mOperations.clear();
		notifyDataSetChanged();
	}

	// Add a list of items
	public void addAll(List<Operation> list) {
		mOperations.addAll(list);
		notifyDataSetChanged();
	}
}
