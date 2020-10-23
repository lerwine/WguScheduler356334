package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter;
import Erwine.Leonard.T.wguscheduler356334.entity.alert.AlertListItem;

public class AllAlertsListAdapter extends RecyclerView.Adapter<AllAlertsListAdapter.ViewHolder> {

    private final List<AlertListItem> mValues;
    private final Consumer<AlertListItem> onItemClicked;

    public AllAlertsListAdapter(@NonNull List<AlertListItem> items, @NonNull Consumer<AlertListItem> onItemClicked) {
        mValues = items;
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_all_alerts_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressWarnings("FieldCanBeLocal")
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
            onItemClicked.accept(alertListItem);
        }

        public void setItem(@NonNull AlertListItem alertListItem) {
            this.alertListItem = alertListItem;
            LocalDate d = alertListItem.getEventDate();
            if (null == d) {
                dateTextView.setText(R.string.label_none);
            } else {
                dateTextView.setText(LocalDateConverter.LONG_FORMATTER.format(d));
            }
            typeTextView.setText(alertListItem.getTypeDisplayResourceId());
            codeTextView.setText(alertListItem.getCode());
            titleTextView.setText(alertListItem.getTitle());
        }
    }
}