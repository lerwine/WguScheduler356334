package Erwine.Leonard.T.wguscheduler356334.ui.course;

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
import Erwine.Leonard.T.wguscheduler356334.entity.CourseEntity;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee, MMM d, YYYY").withZone(ZoneId.systemDefault());
    private final List<CourseEntity> mValues;

    public CourseListAdapter(List<CourseEntity> items) {
        mValues = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_course_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        CourseEntity courseEntity = mValues.get(position);
        holder.mItem = courseEntity;
        holder.courseNumberTextView.setText(courseEntity.getNumber());
        holder.courseNameTextView.setText(courseEntity.getTitle());
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
        holder.dateRangeTextView.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView courseNumberTextView;
        public final TextView courseNameTextView;
        public final TextView dateRangeTextView;
        public CourseEntity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            courseNumberTextView = (TextView) view.findViewById(R.id.courseNumberTextView);
            courseNameTextView = (TextView) view.findViewById(R.id.courseNameTextView);
            dateRangeTextView = (TextView) view.findViewById(R.id.dateRangeTextView);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + courseNameTextView.getText() + "'";
        }
    }
}