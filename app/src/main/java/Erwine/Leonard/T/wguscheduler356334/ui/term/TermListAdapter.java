package Erwine.Leonard.T.wguscheduler356334.ui.term;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.EditTermActivity;
import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.TermEntity;

public class TermListAdapter extends RecyclerView.Adapter<TermListAdapter.ViewHolder> {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("eee M/d/YYYY").withZone(ZoneId.systemDefault());
    private final List<TermEntity> entityList;
    private final Context context;

    public TermListAdapter(List<TermEntity> items, Context context) {
        this.entityList = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.term_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final TermEntity item = entityList.get(position);
        holder.nameTextView.setText(item.getName());
        LocalDate start = item.getStart();
        LocalDate end = item.getEnd();
        if (null == start) {
            if (null == end) {
                holder.rangeTextView.setText("? to ?");
            } else {
                holder.rangeTextView.setText(String.format("? to %s", FORMATTER.format(end)));
            }
        } else if (null == end) {
            holder.rangeTextView.setText(String.format("%s to ?", FORMATTER.format(start)));
        } else {
            holder.rangeTextView.setText(String.format("%s to %s", FORMATTER.format(start), FORMATTER.format(end)));
        }
        holder.editTermButton.setOnClickListener((view) -> {
            Intent intent = new Intent(context, EditTermActivity.class);
            intent.putExtra(EditTermActivity.EXTRAS_KEY_TERM_ID, item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView rangeTextView;
        private FloatingActionButton editTermButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            rangeTextView = itemView.findViewById(R.id.rangeTextView);
            editTermButton = itemView.findViewById(R.id.editTermButton);
        }

    }
}
