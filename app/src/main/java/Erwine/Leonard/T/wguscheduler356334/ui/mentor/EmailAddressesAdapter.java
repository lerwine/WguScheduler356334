package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class EmailAddressesAdapter extends RecyclerView.Adapter<EmailAddressesAdapter.ViewHolder> {
    private final IndexedStringList mValues;

    public EmailAddressesAdapter(IndexedStringList items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.email_address_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
        holder.deleteEmailButton.setOnClickListener(v -> {
            IndexedStringList.Item m = holder.item;
            if (null != m) {
                Integer i = m.getNumber().getValue();
                if (null != i) {
                    mValues.remove(i);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final LinearLayout mView;
        public final TextView itemNumberTextView;
        public final EditText contentEditText;
        public final FloatingActionButton deleteEmailButton;
        private final TextWatcher textChangedListener;
        private final Observer<Integer> numberChangedObserver;
        public IndexedStringList.Item item;

        public ViewHolder(LinearLayout view) {
            super(view);
            mView = view;
            itemNumberTextView = view.findViewById(R.id.item_number);
            contentEditText = view.findViewById(R.id.content);
            deleteEmailButton = view.findViewById(R.id.deleteEmailButton);
            textChangedListener = Values.textWatcherForTextChanged(this::onContentChanged);
            numberChangedObserver = (n) -> {
                itemNumberTextView.setText((null == n) ? "" : n.toString());
            };
        }

        private void onContentChanged(String s) {
            IndexedStringList.Item i = item;
            if (null != i) {
                i.getContent().postValue(s);
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + contentEditText.getText() + "'";
        }

        private synchronized void setItem(IndexedStringList.Item item) {
            if (null != this.item) {
                if (this.item == item) {
                    return;
                }
                this.item.getNumber().removeObserver(numberChangedObserver);
                this.item = item;
                if (null == item) {
                    contentEditText.removeTextChangedListener(textChangedListener);
                    itemNumberTextView.setText("");
                } else {
                    onContentChanged(contentEditText.getText().toString());
                    item.getNumber().observeForever(numberChangedObserver);
                    numberChangedObserver.onChanged(item.getNumber().getValue());
                }
            } else if (null != item) {
                this.item = item;
                item.getNumber().observeForever(numberChangedObserver);
                numberChangedObserver.onChanged(item.getNumber().getValue());
                String c = item.getContent().getValue();
                contentEditText.setText(c);
                contentEditText.addTextChangedListener(textChangedListener);
            }
        }

    }
}
