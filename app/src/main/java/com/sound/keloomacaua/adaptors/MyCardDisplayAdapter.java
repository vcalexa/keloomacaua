package com.sound.keloomacaua.adaptors;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.keloomacaua.activities.ui.game.MainActivity;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.interfaces.ItemClickListener;
import com.sound.keloomacaua.R;

public class MyCardDisplayAdapter extends RecyclerView.Adapter<MyCardDisplayAdapter.ViewHolder> {
    private List<Integer> actualCards;
    private final Context context;
    private ImageView tablePile;
    private static ItemClickListener clickListener;
    MainActivity activity;

    public MyCardDisplayAdapter(Context context, List<Integer> actualCards, ImageView tablePile, MainActivity activity) {
        super();
        this.context = context;
        this.actualCards = actualCards;
        this.tablePile = tablePile;
        this.activity = activity;
    }

    public List<Integer> getActualCards() {
        return actualCards;
    }

    public void setActualCards(List<Integer> actualCards) {
        this.actualCards = actualCards;
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
        int turn = cardMoves.getPlayerTurn();

        int imageId = context.getResources().getIdentifier(imageTitle,
                "drawable", context.getPackageName());
        viewHolder.imgThumbnail.setImageResource(imageId);

        boolean isPlayerOne = activity.isPlayerOne();

        viewHolder.setClickListener((view, position, isLongClick) -> {
            String imageTitleFromHand = cardUtils.getImageViewName(actualCards.get(position));
            if ((isPlayerOne && cardMoves.getPlayerTurn() == 1) || (!isPlayerOne && turn == 2) &&
                    cardMoves.playIfPossible(actualCards.get(position))) {
                Toast.makeText(context, "Played:" + position + " - " + imageTitleFromHand,
                        Toast.LENGTH_SHORT).show();

                int clickedImageId = context.getResources().getIdentifier(imageTitleFromHand, "drawable", context.getPackageName());

                tablePile.setImageResource(clickedImageId);
                if (isPlayerOne) {
                    cardMoves.player1Move(position);
                } else {
                    cardMoves.player2Move(position);
                }

                cardMoves.changeTurn();

                activity.iaCarteButton.setEnabled(false);
                activity.gataTura.setEnabled(false);

                //actualCards.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, actualCards.size());
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