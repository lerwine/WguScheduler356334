package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.term.TermListItem;

import static Erwine.Leonard.T.wguscheduler356334.db.LocalDateConverter.FULL_FORMATTER;

public class TermListAdapter extends RecyclerView.Adapter<TermListAdapter.ViewHolder> {

    private final List<TermListItem> entityList;
    private final Context context;

    public TermListAdapter(List<TermListItem> entityList, Context context) {
        this.entityList = entityList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_term_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.accept(entityList.get(position));
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @SuppressWarnings("FieldCanBeLocal")
        private final View fragment_term_item;
        private final TextView termNameTextView;
        private final TextView termRangeTextView;
        private TermListItem listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fragment_term_item = itemView;
            termNameTextView = itemView.findViewById(R.id.termNameTextView);
            termRangeTextView = itemView.findViewById(R.id.termRangeTextView);
            fragment_term_item.setOnClickListener(this::onViewClick);
        }

        private void accept(TermListItem listItem) {
            this.listItem = listItem;
            termNameTextView.setText(listItem.getName());
            LocalDate start = listItem.getStart();
            LocalDate end = listItem.getEnd();
            if (null == start) {
                if (null == end) {
                    termRangeTextView.setText(R.string.label_unknown_to_unknown_range);
                } else {
                    termRangeTextView.setText(context.getResources().getString(R.string.format_range_unknown_to_end, FULL_FORMATTER.format(end)));
                }
            } else if (null == end) {
                termRangeTextView.setText(context.getResources().getString(R.string.format_range_start_to_unknown, FULL_FORMATTER.format(start)));
            } else {
                termRangeTextView.setText(context.getResources().getString(R.string.format_range_start_to_end, FULL_FORMATTER.format(start), FULL_FORMATTER.format(end)));
            }
        }

        private void onViewClick(View v) {
            EditTermViewModel.startViewTermActivity(v.getContext(), listItem.getId());
        }

    }

}
