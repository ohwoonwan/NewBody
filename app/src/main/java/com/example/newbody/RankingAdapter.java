package com.example.newbody;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<Ranking.RankingItem> rankingList;

    public RankingAdapter(List<Ranking.RankingItem> rankingList) {
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ranking_item, parent, false);
        return new RankingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Ranking.RankingItem currentItem = rankingList.get(position);
        holder.rankTextView.setText(String.valueOf(currentItem.rank));
        holder.nameTextView.setText(currentItem.name);
        holder.scoreTextView.setText(String.valueOf(currentItem.score));
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    static class RankingViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        TextView nameTextView;
        TextView scoreTextView;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            scoreTextView = itemView.findViewById(R.id.scoreTextView);
        }
    }
}
