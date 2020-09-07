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
import Erwine.Leonard.T.wguscheduler356334.entity.PhoneNumberEntity;

public class PhoneNumberListAdapter extends RecyclerView.Adapter<PhoneNumberListAdapter.ViewHolder> {

    private final List<PhoneNumberEntity> mValues;
    private final Context mContext;

    public PhoneNumberListAdapter(List<PhoneNumberEntity> items, Context context) {
        mValues = items;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_phone_number_item, parent, false);
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
        private final FloatingActionButton mEditPhoneFloatingActionButton;
        private final FloatingActionButton mPhoneUpFloatingActionButton;
        private final FloatingActionButton mPhoneDownFloatingActionButton;
        private final FloatingActionButton mDeletePhoneFloatingActionButton;
        public PhoneNumberEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            contentTextView = (TextView) view.findViewById(R.id.contentTextView);
            mEditPhoneFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.editEmailFloatingActionButton);
            mPhoneUpFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.emailUpFloatingActionButton);
            mPhoneDownFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.emailDownFloatingActionButton);
            mDeletePhoneFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.deleteEmailFloatingActionButton);
            mEditPhoneFloatingActionButton.setOnClickListener(this::onEditPhoneFloatingActionButton);
            mPhoneUpFloatingActionButton.setOnClickListener(this::onPhoneUpFloatingActionButton);
            mPhoneDownFloatingActionButton.setOnClickListener(this::onPhoneDownFloatingActionButton);
            mDeletePhoneFloatingActionButton.setOnClickListener(this::onDeletePhoneFloatingActionButton);
        }

        private void onEditPhoneFloatingActionButton(View view) {

        }

        private void onPhoneUpFloatingActionButton(View view) {

        }

        private void onPhoneDownFloatingActionButton(View view) {

        }

        private void onDeletePhoneFloatingActionButton(View view) {

        }

        public void setItem(PhoneNumberEntity item, boolean isFirst, boolean isLast) {
            mItem = item;
            contentTextView.setText(mItem.getValue());
            if (isFirst) {
                mPhoneUpFloatingActionButton.setEnabled(false);
                mPhoneUpFloatingActionButton.setVisibility(View.INVISIBLE);
                if (isLast) {
                    mDeletePhoneFloatingActionButton.setEnabled(false);
                    mDeletePhoneFloatingActionButton.setVisibility(View.GONE);
                } else {
                    mDeletePhoneFloatingActionButton.setVisibility(View.VISIBLE);
                    mDeletePhoneFloatingActionButton.setEnabled(true);
                }
            } else {
                mDeletePhoneFloatingActionButton.setVisibility(View.VISIBLE);
                mPhoneUpFloatingActionButton.setVisibility(View.VISIBLE);
                mDeletePhoneFloatingActionButton.setEnabled(true);
                mPhoneUpFloatingActionButton.setEnabled(true);
            }
            if (isLast) {
                mPhoneDownFloatingActionButton.setEnabled(false);
                mPhoneDownFloatingActionButton.setVisibility(View.INVISIBLE);
            } else {
                mPhoneDownFloatingActionButton.setVisibility(View.VISIBLE);
                mPhoneDownFloatingActionButton.setEnabled(true);
            }
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + contentTextView.getText() + "'";
        }
    }
}