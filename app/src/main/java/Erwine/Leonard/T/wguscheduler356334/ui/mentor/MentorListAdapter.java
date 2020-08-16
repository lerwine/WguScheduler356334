package Erwine.Leonard.T.wguscheduler356334.ui.mentor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import Erwine.Leonard.T.wguscheduler356334.R;
import Erwine.Leonard.T.wguscheduler356334.entity.MentorEntity;

public class MentorListAdapter extends RecyclerView.Adapter<MentorListAdapter.ViewHolder> {

    private final List<MentorEntity> entityList;
    private final Context context;

    public MentorListAdapter(List<MentorEntity> items, Context context) {
        this.entityList = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.mentor_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MentorEntity item = entityList.get(position);
        holder.mentorNameTextView.setText(item.getName());
        holder.editMentorButton.setOnClickListener((view) -> {
            Intent intent = new Intent(context, EditMentorActivity.class);
            intent.putExtra(EditMentorActivity.EXTRAS_KEY_MENTOR_ID, item.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return entityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mentorNameTextView;
        private FloatingActionButton editMentorButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mentorNameTextView = itemView.findViewById(R.id.mentorNameTextView);
            editMentorButton = itemView.findViewById(R.id.editMentorButton);
        }

    }

}
