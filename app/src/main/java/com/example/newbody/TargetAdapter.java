package com.example.newbody;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.TargetViewHolder>{
    private List<Target.TargetItem> targetList;

    public TargetAdapter(List<Target.TargetItem> targetList) {
        this.targetList = targetList;
    }

    @NonNull
    @Override
    public TargetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.target_item, parent, false);
        return new TargetViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TargetViewHolder holder, int position) {
        Target.TargetItem currentItem = targetList.get(position);
        holder.targetName.setText(String.valueOf(currentItem.exerciseName));
        holder.progressBar.setProgress(currentItem.myScore);
        Log.d("num", String.valueOf(currentItem.myScore));
    }

    @Override
    public int getItemCount() {
        return targetList.size();
    }

    static class TargetViewHolder extends RecyclerView.ViewHolder {
        TextView targetName;
        ProgressBar progressBar;

        public TargetViewHolder(@NonNull View itemView) {
            super(itemView);
            targetName = itemView.findViewById(R.id.targetName);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
