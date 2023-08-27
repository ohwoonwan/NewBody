package com.example.newbody;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MemberAdaptor extends RecyclerView.Adapter<MemberAdaptor.MemberViewHolder>{
    private List<ManagerMember.MemberItem> memberList;

    public MemberAdaptor(List<ManagerMember.MemberItem> memberList) {
        this.memberList = memberList;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.member_item, parent, false);
        return new MemberViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        ManagerMember.MemberItem currentItem = memberList.get(position);
        holder.nameTextView.setText(currentItem.name);
        holder.birthTextView.setText(currentItem.birth);
        holder.genderTextView.setText(currentItem.gender);
        holder.gradeTextView.setText(currentItem.grade);
        if(currentItem.grade.equals("프리미엄")){
            holder.gradeTextView.setTextColor(Color.parseColor("#ffcc00"));
        }
    }

    @Override
    public int getItemCount() {
        return memberList.size();
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView birthTextView;
        TextView genderTextView;
        TextView gradeTextView;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            birthTextView = itemView.findViewById(R.id.birthTextView);
            genderTextView = itemView.findViewById(R.id.genderTextView);
            gradeTextView = itemView.findViewById(R.id.gradeTextView);
        }
    }
}
