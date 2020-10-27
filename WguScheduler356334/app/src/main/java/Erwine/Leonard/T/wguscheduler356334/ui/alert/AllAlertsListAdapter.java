package Erwine.Leonard.T.wguscheduler356334.ui.alert;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

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
        private final TextView dateAndCodeTextView;
        private final TextView messageTextView;
        private AlertListItem alertListItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateAndCodeTextView = view.findViewById(R.id.dateAndCodeTextView);
            messageTextView = view.findViewById(R.id.messageTextView);
            view.setOnClickListener(this::onViewClick);
        }

        private void onViewClick(View view) {
            onItemClicked.accept(alertListItem);
        }

        public void setItem(@NonNull AlertListItem alertListItem) {
            this.alertListItem = alertListItem;
            LocalDate d = alertListItem.getAlertDate();
            SpannableStringBuilder builder;
            Resources resources = mView.getResources();
            if (null == d && null == (d = alertListItem.getEventDate())) {
                builder = new SpannableStringBuilder(resources.getString(R.string.label_none));
                builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                builder = new SpannableStringBuilder(LocalDateConverter.LONG_FORMATTER.format(d));
            }
            int start = builder.append(" ").length();
            String customMessage = alertListItem.getCustomMessage();
            String title = alertListItem.getTitle();
            if (null != customMessage) {
                builder.append(customMessage).setSpan(new StyleSpan(Typeface.BOLD), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                dateAndCodeTextView.setText(builder);
                builder = new SpannableStringBuilder(resources.getString(alertListItem.getTypeDisplayResourceId())).append(" ").append(alertListItem.getCode());
                if (null != title) {
                    builder.append(": ").setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.append(title);
                } else {
                    builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                if (alertListItem.isAssessment()) {
                    start = builder.append("\n").length();
                    builder.append(resources.getString(R.string.label_course)).append(" ").append(alertListItem.getCourseNumber()).append(": ").append(alertListItem.getCourseTitle())
                            .setSpan(new StyleSpan(Typeface.ITALIC), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new RelativeSizeSpan(0.75f), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                messageTextView.setText(builder);
            } else {
                builder.append(resources.getString(alertListItem.getTypeDisplayResourceId())).append(" ").append(alertListItem.getCode())
                        .setSpan(new StyleSpan(Typeface.BOLD), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                dateAndCodeTextView.setText(builder);
                if (null != title) {
                    builder = new SpannableStringBuilder(title);
                    builder.setSpan(new StyleSpan(Typeface.BOLD), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (alertListItem.isAssessment()) {
                        start = builder.append("\n").length();
                        builder.append(resources.getString(R.string.label_course)).append(" ").append(alertListItem.getCourseNumber()).append(": ").append(alertListItem.getCourseTitle())
                                .setSpan(new StyleSpan(Typeface.ITALIC), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                        builder.setSpan(new RelativeSizeSpan(0.75f), start, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    messageTextView.setText(builder);
                } else if (alertListItem.isAssessment()) {
                    builder = new SpannableStringBuilder(resources.getString(R.string.label_course)).append(" ").append(alertListItem.getCourseNumber()).append(": ").append(alertListItem.getCourseTitle());
                    builder.setSpan(new StyleSpan(Typeface.ITALIC), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    builder.setSpan(new RelativeSizeSpan(0.75f), 0, builder.length(), SPAN_EXCLUSIVE_EXCLUSIVE);
                    messageTextView.setText(builder);
                } else {
                    messageTextView.setText("");
                }
            }
        }
    }
}