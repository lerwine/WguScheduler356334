package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentAlert;

public class AssessmentAlertListAdapter extends RecyclerView.Adapter<AssessmentAlertListAdapter.ViewHolder> {

    private final List<AssessmentAlert> mValues;
    private final Consumer<AssessmentAlert> onItemClick;

    public AssessmentAlertListAdapter(List<AssessmentAlert> items, @NonNull Consumer<AssessmentAlert> onItemClick) {
        mValues = items;
        this.onItemClick = onItemClick;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_assessment_alert_item, parent, false);
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
        private final View mView;
        private final TextView dateTextView;
        private final TextView messageTextView;
        private AssessmentAlert alertListItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateTextView = view.findViewById(R.id.dateTextView);
            messageTextView = view.findViewById(R.id.messageTextView);
            view.setOnClickListener(this::onViewClick);
        }

        private void onViewClick(View view) {
            onItemClick.accept(alertListItem);
        }

        public void setItem(@NonNull AssessmentAlert alertListItem) {
            this.alertListItem = alertListItem;
            LocalDate d = alertListItem.getAlertDate();
            if (null != d) {
                dateTextView.setText(LocalDateConverter.LONG_FORMATTER.format(d));
            } else {
                AlertEntity alert = alertListItem.getAlert();
                long timeSpec = alert.getTimeSpec();
                if (Objects.requireNonNull(alert.isSubsequent())) {
                    if (timeSpec == 0) {
                        dateTextView.setText(mView.getResources().getString(R.string.message_on_completion_date));
                    } else {
                        dateTextView.setText(mView.getResources().getString(R.string.format_days_after_completion_date, Math.abs(timeSpec)));
                    }
                } else if (timeSpec == 0) {
                    dateTextView.setText(mView.getResources().getString(R.string.message_on_goal_date));
                } else if (timeSpec < 0) {
                    dateTextView.setText(mView.getResources().getString(R.string.format_days_before_goal_date, Math.abs(timeSpec)));
                } else {
                    dateTextView.setText(mView.getResources().getString(R.string.format_days_after_goal_date, Math.abs(timeSpec)));
                }
            }
            if (alertListItem.isMessagePresent()) {
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(alertListItem.getMessage());
            } else {
                messageTextView.setText("");
                messageTextView.setVisibility(View.GONE);
            }
        }
    }
}