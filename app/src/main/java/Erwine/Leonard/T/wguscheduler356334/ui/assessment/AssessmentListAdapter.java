package Erwine.Leonard.T.wguscheduler356334.ui.assessment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.assessment.AssessmentEntity;

public class AssessmentListAdapter extends RecyclerView.Adapter<AssessmentListAdapter.ViewHolder> {

    private static final String LOG_TAG = AssessmentListAdapter.class.getName();
    private final List<AssessmentEntity> items;
    private final Context context;

    public AssessmentListAdapter(@NonNull Context context, @NonNull List<AssessmentEntity> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_assessment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressWarnings("FieldCanBeLocal")
        private final View view;
        private final TextView codeTextView;
        private final TextView statusTextView;
        private final TextView adjTextView;
        private final TextView dateTextView;
        private AssessmentEntity item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            codeTextView = view.findViewById(R.id.typeTextView);
            statusTextView = view.findViewById(R.id.statusTextView);
            adjTextView = view.findViewById(R.id.adjTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            view.setOnClickListener(this::onViewClick);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + statusTextView.getText() + "'";
        }

        private void onViewClick(View view) {
            Log.d(LOG_TAG, "Viewing assessment " + item);
            EditAssessmentViewModel.startViewAssessmentActivity(view.getContext(), item.getId());
        }

        public void setItem(AssessmentEntity item) {
            this.item = item;
            codeTextView.setText(item.getCode());
            statusTextView.setText(context.getString(item.getStatus().displayResourceId()));
            LocalDate date;
            switch (item.getStatus()) {
                case NOT_STARTED:
                case IN_PROGRESS:
                    adjTextView.setText(R.string.label_goal);
                    date = item.getGoalDate();
                    break;
                default:
                    adjTextView.setText(R.string.label_completed);
                    date = item.getCompletionDate();
                    break;
            }
            if (null == date) {
                dateTextView.setText(R.string.label_none);
            } else {
                dateTextView.setText(AssessmentListViewModel.FORMATTER.format(date));
            }
        }
    }
}