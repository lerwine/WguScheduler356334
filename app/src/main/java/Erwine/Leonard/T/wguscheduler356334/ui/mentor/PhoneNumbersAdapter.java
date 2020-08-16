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

public class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneNumbersAdapter.ViewHolder> {

    private final IndexedStringList mValues;

    public PhoneNumbersAdapter(IndexedStringList items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.phone_number_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
        holder.deletePhoneButton.setOnClickListener(v -> {
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
        public final FloatingActionButton deletePhoneButton;
        private final TextWatcher textChangedListener;
        private final Observer<Integer> numberChangedObserver;
        public IndexedStringList.Item item;

        public ViewHolder(LinearLayout view) {
            super(view);
            mView = view;
            itemNumberTextView = view.findViewById(R.id.item_number);
            contentEditText = view.findViewById(R.id.content);
            deletePhoneButton = view.findViewById(R.id.deletePhoneButton);
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

        @Override
        public String toString() {
            return super.toString() + " '" + contentEditText.getText() + "'";
        }
    }
}