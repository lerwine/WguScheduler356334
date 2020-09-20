package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.annotation.SuppressLint;
import android.content.Context;
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
import Erwine.Leonard.T.wguscheduler356334.entity.TermListItem;

public class TermListAdapter extends RecyclerView.Adapter<TermListAdapter.ViewHolder> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee M/d/YYYY").withZone(ZoneId.systemDefault());
    private final List<TermListItem> entityList;
    private final Context context;

    public TermListAdapter(List<TermListItem> items, Context context) {
        this.entityList = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.fragment_term_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(entityList.get(position));
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView termNameTextView;
        private final TextView termRangeTextView;
        private TermListItem mItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            termNameTextView = itemView.findViewById(R.id.termNameTextView);
            termRangeTextView = itemView.findViewById(R.id.termRangeTextView);
            mView.setOnClickListener(this::onViewClick);
        }

        public void setItem(TermListItem item) {
            mItem = item;
            termNameTextView.setText(item.getName());
            LocalDate start = item.getStart();
            LocalDate end = item.getEnd();
            if (null == start) {
                if (null == end) {
                    termRangeTextView.setText("? to ?");
                } else {
                    termRangeTextView.setText(String.format("? to %s", FORMATTER.format(end)));
                }
            } else if (null == end) {
                termRangeTextView.setText(String.format("%s to ?", FORMATTER.format(start)));
            } else {
                termRangeTextView.setText(String.format("%s to %s", FORMATTER.format(start), FORMATTER.format(end)));
            }
        }

        @SuppressWarnings("ConstantConditions")
        private void onViewClick(View v) {
            EditTermViewModel.startViewTermActivity(v.getContext(), mItem.getId());
        }

    }

}
