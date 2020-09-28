package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.AbstractCourseEntity;

public abstract class AbstractCourseListAdapter<T extends AbstractCourseEntity<T>, U extends AbstractCourseListAdapter<T, U>.AbstractViewHolder> extends RecyclerView.Adapter<U> {

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
            courseNumberTextView = (TextView) view.findViewById(R.id.codeTextView);
            titleTextView = (TextView) view.findViewById(R.id.titleTextView);
            statusTextView = (TextView) view.findViewById(R.id.typeTextView);
            rangeTextView = (TextView) view.findViewById(R.id.rangeTextView);
            view.setOnClickListener(this::onViewClick);
        }

        protected T getItem() {
            return item;
        }

        protected View getView() {
            return view;
        }

        @SuppressWarnings("ConstantConditions")
        private void onViewClick(View view) {
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
            LocalDate date = courseEntity.getActualStart();
            StringBuilder sb = new StringBuilder();
            boolean expected = null == date;
            if (expected) {
                date = courseEntity.getExpectedStart();
                if (null == date) {
                    sb.append("?");
                } else {
                    sb.append(FORMATTER.format(date));
                }
            } else {
                sb.append(FORMATTER.format(date));
            }
            sb.append(" - ");
            date = courseEntity.getActualEnd();
            if (null == date) {
                date = courseEntity.getExpectedEnd();
                if (null == date) {
                    if (expected) {
                        sb.append(" (expected) - ?");
                    } else {
                        sb.append(" - ?");
                    }
                } else {
                    sb.append(" - ").append(FORMATTER.format(date)).append(" (expected)");
                }
            } else {
                if (expected) {
                    sb.append(" (expected) - ");
                }
                sb.append(FORMATTER.format(date));
            }
            rangeTextView.setText(sb.toString());
        }
    }
}