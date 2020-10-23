package Erwine.Leonard.T.wguscheduler356334.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.course.TermCourseListItem;

public class TermCourseListAdapter extends AbstractCourseListAdapter<TermCourseListItem, TermCourseListAdapter.ViewHolder> {

    public TermCourseListAdapter(List<TermCourseListItem> items) {
        super(items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_course_item, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends AbstractCourseListAdapter<TermCourseListItem, TermCourseListAdapter.ViewHolder>.AbstractViewHolder {
        private final TextView mentorTextView;

        public ViewHolder(View view) {
            super(view);
            mentorTextView = view.findViewById(R.id.mentorTextView);
        }

        @Override
        public void setItem(TermCourseListItem courseEntity) {
            super.setItem(courseEntity);
            String s = courseEntity.getMentorName();
            mentorTextView.setText((s.isEmpty()) ? getView().getResources().getString(R.string.message_no_mentor) : s);
        }
    }
}