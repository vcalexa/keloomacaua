package com.sound.KelooMacaua.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.KelooMacaua.ActualCard;
import com.sound.KelooMacaua.ItemClickListener;
import com.sound.KelooMacaua.MainActivity;
import com.sound.KelooMacaua.R;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.ViewHolder>{
    private List<ActualCard> actualCards;
    private final Context context;

    public MyAdapter(Context context, List<ActualCard> actualCards) {
        super();
        this.context = context;
        this.actualCards = actualCards;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        //viewHolder.textView.setText(numberName.get(i));

        int imageId =  context.getResources().getIdentifier(actualCards.get(i).getCardDescription(),
                "drawable", context.getPackageName());
        viewHolder.imgThumbnail.setImageResource(imageId);
        viewHolder.setClickListener((view, position, isLongClick) -> {
            if (isLongClick) {
                Toast.makeText(context, "#" + position + " - " + actualCards.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                context.startActivity(new Intent(context, MainActivity.class));
            } else {
                Toast.makeText(context, "#" + position + " - " + actualCards.get(position),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public int getItemCount() {
        return actualCards.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        ImageView imgThumbnail;
        //TextView textView;
        private ItemClickListener clickListener;
        ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            //textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }
        void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}