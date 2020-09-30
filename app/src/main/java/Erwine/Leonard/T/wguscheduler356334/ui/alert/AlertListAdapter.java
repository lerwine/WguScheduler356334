package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;

public class AlertListAdapter extends RecyclerView.Adapter<AlertListAdapter.ViewHolder> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee M/d/YYYY").withZone(ZoneId.systemDefault());
    private final List<AlertListItem> mValues;

    public AlertListAdapter(List<AlertListItem> items) {
        mValues = items;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_alert_item, parent, false);
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
        private final TextView typeTextView;
        private final TextView codeTextView;
        private final TextView titleTextView;
        private AlertListItem alertListItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateTextView = view.findViewById(R.id.dateTextView);
            typeTextView = view.findViewById(R.id.typeTextView);
            codeTextView = view.findViewById(R.id.codeTextView);
            titleTextView = view.findViewById(R.id.titleTextView);
            view.setOnClickListener(this::onViewClick);
        }

        private void onViewClick(View view) {
            // TODO: Display popup to view/manage alert
        }

        public void setItem(AlertListItem alertListItem) {
            this.alertListItem = alertListItem;
            LocalDate d = alertListItem.getEventDate();
            if (null == d) {
                dateTextView.setText(R.string.label_none);
            } else {
                dateTextView.setText(FORMATTER.format(d));
            }
            typeTextView.setText(alertListItem.getTypeDisplayResourceId());
            codeTextView.setText(alertListItem.getCode());
            titleTextView.setText(alertListItem.getTitle());
        }
    }
}