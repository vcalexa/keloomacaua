package com.sound.keloomacaua.adaptors;

import static com.sound.keloomacaua.game.CardUtils.cardToImageId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.keloomacaua.R;
import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.interfaces.CardTapListener;

import java.util.ArrayList;
import java.util.List;

public class MyCardDisplayAdapter extends RecyclerView.Adapter<MyCardDisplayAdapter.ViewHolder> {
    private final List<Integer> ownCards;
    private final CardTapListener clickListener;

    public MyCardDisplayAdapter(CardTapListener cardTapListener) {
        super();
        this.ownCards = new ArrayList<>();
        this.clickListener = cardTapListener;
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
    public void onBindViewHolder(ViewHolder viewHolder, int cardIndex) {
        Context context = viewHolder.container.getContext();
        viewHolder.imgThumbnail.setImageResource(cardToImageId(ownCards.get(cardIndex), context));
        if (CardMoves.getInstance().canPlayCardAt(cardIndex)) {
            viewHolder.container.setElevation(50);
            viewHolder.container.animate().translationY(-16).start();
        } else {
            viewHolder.container.animate().translationY(32).start();
            viewHolder.container.setElevation(0);
        }
        viewHolder.container.setOnClickListener(view -> clickListener.onCardTapped(cardIndex));
    }

    @Override
    public int getItemCount() {
        return ownCards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        View container;

        ViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            container = imgThumbnail;
        }
    }
}