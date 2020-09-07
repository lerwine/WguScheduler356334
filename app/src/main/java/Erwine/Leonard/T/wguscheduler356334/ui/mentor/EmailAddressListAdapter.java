package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.EmailAddressEntity;

public class EmailAddressListAdapter extends RecyclerView.Adapter<EmailAddressListAdapter.ViewHolder> {

    private final List<EmailAddressEntity> mValues;
    private final Context mContext;

    public EmailAddressListAdapter(List<EmailAddressEntity> items, Context context) {
        mValues = items;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_email_address_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position), position == 0, position == mValues.size() - 1);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView contentTextView;
        public final FloatingActionButton editEmailFloatingActionButton;
        private final FloatingActionButton emailUpFloatingActionButton;
        private final FloatingActionButton emailDownFloatingActionButton;
        private final FloatingActionButton deleteEmailFloatingActionButton;
        public EmailAddressEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            contentTextView = (TextView) view.findViewById(R.id.contentTextView);
            editEmailFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.editEmailFloatingActionButton);
            emailUpFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.emailUpFloatingActionButton);
            emailDownFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.emailDownFloatingActionButton);
            deleteEmailFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.deleteEmailFloatingActionButton);
            editEmailFloatingActionButton.setOnClickListener(this::onEditEmailFloatingActionButtonClick);
            emailUpFloatingActionButton.setOnClickListener(this::onEmailUpFloatingActionButtonClick);
            emailDownFloatingActionButton.setOnClickListener(this::onEmailDownFloatingActionButtonClick);
            deleteEmailFloatingActionButton.setOnClickListener(this::onDeleteEmailFloatingActionButtonClick);
        }

        private void onEditEmailFloatingActionButtonClick(View view) {

        }

        private void onEmailUpFloatingActionButtonClick(View view) {

        }

        private void onEmailDownFloatingActionButtonClick(View view) {

        }

        private void onDeleteEmailFloatingActionButtonClick(View view) {

        }

        public void setItem(EmailAddressEntity item, boolean isFirst, boolean isLast) {
            mItem = item;
            contentTextView.setText(mItem.getValue());
            if (isFirst) {
                emailUpFloatingActionButton.setEnabled(false);
                emailUpFloatingActionButton.setVisibility(View.INVISIBLE);
                if (isLast) {
                    deleteEmailFloatingActionButton.setEnabled(false);
                    deleteEmailFloatingActionButton.setVisibility(View.GONE);
                } else {
                    deleteEmailFloatingActionButton.setVisibility(View.VISIBLE);
                    deleteEmailFloatingActionButton.setEnabled(true);
                }
            } else {
                deleteEmailFloatingActionButton.setVisibility(View.VISIBLE);
                emailUpFloatingActionButton.setVisibility(View.VISIBLE);
                deleteEmailFloatingActionButton.setEnabled(true);
                emailUpFloatingActionButton.setEnabled(true);
            }
            if (isLast) {
                emailDownFloatingActionButton.setEnabled(false);
                emailDownFloatingActionButton.setVisibility(View.INVISIBLE);
            } else {
                emailDownFloatingActionButton.setVisibility(View.VISIBLE);
                emailDownFloatingActionButton.setEnabled(true);
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + contentTextView.getText() + "'";
        }
    }
}