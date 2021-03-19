package com.sound.keloomacaua.adaptors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.sound.keloomacaua.R;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.interfaces.ItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MyCardDisplayAdapter extends RecyclerView.Adapter<MyCardDisplayAdapter.ViewHolder> {
    private final List<Integer> actualCards;
    private final Context context;
    private static ItemClickListener clickListener;
    private DatabaseReference gameReference;

    public MyCardDisplayAdapter(Context context, boolean isPlayerOne, DatabaseReference firebaseReference) {
        super();
        this.context = context;
        this.actualCards = new ArrayList<>();
        this.gameReference = firebaseReference;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setActualCards(List<Integer> actualCards) {
        this.actualCards.clear();
        this.actualCards.addAll(actualCards);
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_image, viewGroup, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        CardMoves cardMoves = CardMoves.getInstance();
        CardUtils cardUtils = cardMoves.getCardUtils();
        String imageTitle = cardUtils.getImageViewName(actualCards.get(i));

        int imageId = context.getResources().getIdentifier(imageTitle,
                "drawable", context.getPackageName());
        viewHolder.imgThumbnail.setImageResource(imageId);

        viewHolder.setClickListener((view, position, isLongClick) -> {
            String imageTitleFromHand = cardUtils.getImageViewName(actualCards.get(position));
            if (cardMoves.hasMoved(position)) {
                Toast.makeText(context, "Played:" + position + " - " + imageTitleFromHand,
                        Toast.LENGTH_SHORT).show();
                gameReference.setValue(cardMoves.getGame());
            } else {
                Toast.makeText(context, "Cannot play:" + position + " - " + imageTitleFromHand,
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


        ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void setClickListener(ItemClickListener itemClickListener) {
            clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getLayoutPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getLayoutPosition(), true);
            return true;
        }
    }
}