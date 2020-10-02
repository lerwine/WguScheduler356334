package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.AbstractCourseEntity;

public abstract class AbstractCourseListAdapter<T extends AbstractCourseEntity<T>, U extends AbstractCourseListAdapter<T, U>.AbstractViewHolder> extends RecyclerView.Adapter<U> {

    private static final String LOG_TAG = AbstractCourseListAdapter.class.getName();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    private final List<T> items;

    protected AbstractCourseListAdapter(List<T> items) {
        this.items = items;
    }

    protected List<T> getItems() {
        return items;
    }

    @Override
    public void onBindViewHolder(@NonNull U holder, int position) {
        ((AbstractCourseListAdapter<T, U>.AbstractViewHolder) holder).setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public abstract class AbstractViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView courseNumberTextView;
        public final TextView titleTextView;
        public final TextView statusTextView;
        public final TextView rangeTextView;
        public T item;

        protected AbstractViewHolder(View view) {
            super(view);
            this.view = view;
            courseNumberTextView = (TextView) view.findViewById(R.id.typeTextView);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            statusTextView = (TextView) view.findViewById(R.id.statusTextView);
            rangeTextView = (TextView) view.findViewById(R.id.rangeTextView);
            view.setOnClickListener(this::onViewClick);
        }

        protected T getItem() {
            return item;
        }

        protected View getView() {
            return view;
        }

        private void onViewClick(View view) {
            Log.d(LOG_TAG, "Viewing assessment " + item);
            EditCourseViewModel.startViewCourseActivity(view.getContext(), item.getId());
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText() + "'";
        }

        public void setItem(T courseEntity) {
            item = courseEntity;
            courseNumberTextView.setText(courseEntity.getNumber());
            titleTextView.setText(courseEntity.getTitle());
            statusTextView.setText(courseEntity.getStatus().displayResourceId());
            LocalDate start = courseEntity.getActualStart();
            LocalDate end;
            if (null == start) {
                start = courseEntity.getExpectedStart();
                if (null == (end = courseEntity.getExpectedEnd())) {
                    end = courseEntity.getActualEnd();
                }
            } else if (null == (end = courseEntity.getActualEnd())) {
                end = courseEntity.getExpectedEnd();
            }

            if (null == start) {
                if (null == end) {
                    rangeTextView.setText(R.string.label_unknown_to_unknown_range);
                } else {
                    rangeTextView.setText(view.getContext().getResources().getString(R.string.format_range_unknown_to_end, FORMATTER.format(end)));
                }
            } else if (null == end) {
                rangeTextView.setText(view.getContext().getResources().getString(R.string.format_range_start_to_unknown, FORMATTER.format(start)));
            } else {
                rangeTextView.setText(view.getContext().getResources().getString(R.string.format_range_start_to_end, FORMATTER.format(start), FORMATTER.format(end)));
            }
        }
    }
}