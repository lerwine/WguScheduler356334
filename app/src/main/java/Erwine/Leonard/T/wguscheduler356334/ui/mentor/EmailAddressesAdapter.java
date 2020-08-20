package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.StringLineIterator;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class EmailAddressesAdapter extends RecyclerView.Adapter<EmailAddressesAdapter.ViewHolder> {

    private final IndexedStringList mValues;

    public EmailAddressesAdapter(String text) {
        mValues = new IndexedStringList();
        if (null == text) {
            mValues.addValue("");
        } else {
            StringLineIterator iterator = StringLineIterator.create(text, true, true);
            while (iterator.hasNext()) {
                String line = iterator.next();
                if (!line.isEmpty()) {
                    mValues.addValue(line);
                }
            }
            if (mValues.isEmpty()) {
                mValues.addValue("");
            }
        }
    }

    public LiveData<Boolean> isAnyElementNonEmpty() {
        return mValues.anyElementNonEmpty();
    }

    public String getText() {
        return mValues.getText();
    }

    public synchronized void setText(String text) {
        if (mValues.setText(text)) {
            notifyDataSetChanged();
        }
    }

    @Override
    @NonNull
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
                Integer i = m.getLineNumber();
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

    public synchronized void addBlank() {
        if (!mValues.isEmpty() && !mValues.get(mValues.size()).getNormalizedValue().isEmpty()) {
            mValues.addValue("");
            notifyDataSetChanged();
        }
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
                i.rawValue().postValue(s);
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
                this.item.lineNumber().removeObserver(numberChangedObserver);
                this.item = item;
                if (null == item) {
                    contentEditText.removeTextChangedListener(textChangedListener);
                    itemNumberTextView.setText("");
                } else {
                    onContentChanged(contentEditText.getText().toString());
                    item.lineNumber().observeForever(numberChangedObserver);
                    numberChangedObserver.onChanged(item.getLineNumber());
                }
            } else if (null != item) {
                this.item = item;
                item.lineNumber().observeForever(numberChangedObserver);
                numberChangedObserver.onChanged(item.getLineNumber());
                String c = item.getRawValue();
                contentEditText.setText(c);
                contentEditText.addTextChangedListener(textChangedListener);
            }
        }

    }
}
