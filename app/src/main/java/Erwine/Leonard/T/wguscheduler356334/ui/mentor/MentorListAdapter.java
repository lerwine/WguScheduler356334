package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.EditMentorActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class MentorListAdapter extends RecyclerView.Adapter<MentorListAdapter.ViewHolder> {

    private final List<MentorEntity> mValues;
    private final Context mContext;
    private final boolean mPreferEmailAddress;

    public MentorListAdapter(List<MentorEntity> items, Context context) {
        mValues = items;
        mContext = context;
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPreferEmailAddress = preferences.getBoolean("prefer_email", false);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_mentor_item, parent, false);
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
        public final TextView mentorNameTextView;
        public final TextView phoneOrEmailTextView;
        public MentorEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mentorNameTextView = view.findViewById(R.id.mentorNameTextView);
            phoneOrEmailTextView = view.findViewById(R.id.phoneOrEmailTextView);
            mView.setOnClickListener(this::onViewClick);
        }

        public void setItem(MentorEntity item) {
            mItem = item;
            mentorNameTextView.setText(mItem.getName());
            if (mPreferEmailAddress) {
                String s = mItem.getEmailAddress();
                phoneOrEmailTextView.setText((s.isEmpty()) ? mItem.getPhoneNumber() : s);
            } else {
                String s = mItem.getPhoneNumber();
                phoneOrEmailTextView.setText((s.isEmpty()) ? mItem.getEmailAddress() : s);
            }
        }

        private void onViewClick(View v) {
            Intent intent = new Intent(mContext, EditMentorActivity.class);
            long id = mItem.getId();
            Log.i(getClass().getName(), String.format("Starting EditMentorActivity with %d", id));
            intent.putExtra(EditMentorActivity.EXTRAS_KEY_MENTOR_ID, id);
            mContext.startActivity(intent);
        }

    }
}