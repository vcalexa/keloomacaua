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
    private final List<Integer> ownCards;
    private final Context context;
    private static ItemClickListener clickListener;
    private final DatabaseReference gameReference;

    public MyCardDisplayAdapter(Context context, DatabaseReference firebaseReference) {
        super();
        this.context = context;
        this.ownCards = new ArrayList<>();
        this.gameReference = firebaseReference;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOwnCards(List<Integer> ownCards) {
        this.ownCards.clear();
        this.ownCards.addAll(ownCards);
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
        String imageTitle = CardUtils.getImageViewName(ownCards.get(i));

        int imageId = context.getResources().getIdentifier(imageTitle,
                "drawable", context.getPackageName());
        viewHolder.imgThumbnail.setImageResource(imageId);

        viewHolder.setClickListener((view, position, isLongClick) -> {
            String imageTitleFromHand = CardUtils.getImageViewName(ownCards.get(position));
            if (cardMoves.hasMoved(position)) {
                Toast.makeText(context, "Played:" + position + " - " + imageTitleFromHand,
                        Toast.LENGTH_SHORT).show();
                gameReference.setValue(cardMoves.getGame());
            } else {
                Toast.makeText(context, "Cannot play:" + position + " - " + imageTitleFromHand,
                        Toast.LENGTH_SHORT).show();
            }

            if (cardMoves.isGameOver()) {
                Toast.makeText(context, "GAME OVER!!", Toast.LENGTH_LONG).show();
            }

        });
    }


    @Override
    public int getItemCount() {
        return ownCards.size();
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