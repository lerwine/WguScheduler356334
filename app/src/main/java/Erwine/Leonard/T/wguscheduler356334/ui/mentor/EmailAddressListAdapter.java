package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.dummy.DummyContent.DummyItem;
import Erwine.Leonard.T.wguscheduler356334.util.IndexedStringList;
import Erwine.Leonard.T.wguscheduler356334.util.Values;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class EmailAddressListAdapter extends RecyclerView.Adapter<EmailAddressListAdapter.ViewHolder> {

    private final IndexedStringList mValues;

    public EmailAddressListAdapter(List<String> items) {
        mValues = new IndexedStringList(items);
    }

    public void setEmailAddresses(ArrayList<String> emailAddresses) {
        mValues.clear();
        mValues.addValue(emailAddresses);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_email_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView item_number;
        public final EditText content;
        public final FloatingActionButton button_email_delete;
        private final TextWatcher textChangedListener;
        private final Observer<Integer> numberChangedObserver;
        public IndexedStringList.Item item;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            item_number = view.findViewById(R.id.item_number);
            content = view.findViewById(R.id.content);
            button_email_delete = view.findViewById(R.id.button_email_delete);
            textChangedListener = Values.textWatcherForTextChanged(this::onContentChanged);
            numberChangedObserver = (n) -> {
                item_number.setText((null == n) ? "" : n.toString());
            };
            button_email_delete.setOnClickListener(v -> {
                IndexedStringList.Item m = item;
                if (null != m) {
                    Integer i = m.getNumber().getValue();
                    if (null != i) {
                        mValues.remove(i);
                    }
                }
            });
        }

        private void onContentChanged(String s) {
            IndexedStringList.Item i = item;
            if (null != i) {
                i.getContent().postValue(s);
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + content.getText() + "'";
        }

        private synchronized void setItem(IndexedStringList.Item item) {
            if (null != this.item) {
                if (this.item == item) {
                    return;
                }
                this.item.getNumber().removeObserver(numberChangedObserver);
                this.item = item;
                if (null == item) {
                    content.removeTextChangedListener(textChangedListener);
                    item_number.setText("");
                } else {
                    onContentChanged(content.getText().toString());
                    item.getNumber().observeForever(numberChangedObserver);
                    numberChangedObserver.onChanged(item.getNumber().getValue());
                }
            } else if (null != item) {
                this.item = item;
                item.getNumber().observeForever(numberChangedObserver);
                numberChangedObserver.onChanged(item.getNumber().getValue());
                String c = item.getContent().getValue();
                content.setText(c);
                content.addTextChangedListener(textChangedListener);
            }
        }

    }
}