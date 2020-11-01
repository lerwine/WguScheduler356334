package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertEntity;
import Erwine.Leonard.T.wguscheduler356334.entity.course.CourseAlert;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;

public class CourseAlertListAdapter extends RecyclerView.Adapter<CourseAlertListAdapter.ViewHolder> {

    private final List<CourseAlert> mValues;
    private final Consumer<CourseAlert> onItemClick;

    public <T extends ViewModel> CourseAlertListAdapter(@NonNull List<CourseAlert> items, @NonNull Consumer<CourseAlert> onItemClick) {
        mValues = items;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_course_alert_item, parent, false);
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
        private CourseAlert alertListItem;

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

        public void setItem(@NonNull CourseAlert alertListItem) {
            this.alertListItem = alertListItem;
            AlertEntity alert = alertListItem.getAlert();
            LocalDate d = alertListItem.getAlertDate();
            if (null != d) {
                dateTextView.setText(FULL_FORMATTER.format(d));
            } else {
                long timeSpec = alert.getTimeSpec();
                if (Objects.requireNonNull(alert.isSubsequent())) {
                    if (timeSpec == 0) {
                        dateTextView.setText(mView.getResources().getString(R.string.message_on_course_end));
                    } else if (timeSpec > 0) {
                        dateTextView.setText(mView.getResources().getString(R.string.format_days_after_course_end, timeSpec));
                    } else {
                        dateTextView.setText(mView.getResources().getString(R.string.format_days_before_course_end, Math.abs(timeSpec)));
                    }
                } else if (timeSpec == 0) {
                    dateTextView.setText(mView.getResources().getString(R.string.message_on_course_start));
                } else if (timeSpec < 0) {
                    dateTextView.setText(mView.getResources().getString(R.string.format_days_before_course_start, Math.abs(timeSpec)));
                } else {
                    dateTextView.setText(mView.getResources().getString(R.string.format_days_after_course_start, timeSpec));
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