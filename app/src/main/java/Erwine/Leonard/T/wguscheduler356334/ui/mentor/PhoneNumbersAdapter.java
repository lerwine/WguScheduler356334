package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

public class PhoneNumbersAdapter extends RecyclerView.Adapter<PhoneNumbersAdapter.ViewHolder> {

    private final IndexedStringList mValues;

    public PhoneNumbersAdapter(String text) {
        mValues = new IndexedStringList();
        if (null == text) {
            mValues.addValue("");
        } else {
            mValues.addValue(Values.REGEX_LINEBREAKN.split(text));
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
                i.rawValue().postValue(s);
            }
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

        @Override
        public String toString() {
            return super.toString() + " '" + contentEditText.getText() + "'";
        }
    }
}