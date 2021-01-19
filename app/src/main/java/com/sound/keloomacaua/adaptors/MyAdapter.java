package com.sound.keloomacaua.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;
import com.sound.keloomacaua.interfaces.ItemClickListener;
import com.sound.keloomacaua.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<Integer> actualCards;
    private final Context context;
    private ImageView tablePile;

    public MyAdapter(Context context, List<Integer> actualCards, ImageView tablePile) {
        super();
        this.context = context;
        this.actualCards = actualCards;
        this.tablePile = tablePile;
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
        CardMoves cardMoves = CardMoves.getInstance();
        CardUtils cardUtils = cardMoves.getCardUtils();
        String imageTitle = cardUtils.getImageViewName(actualCards.get(i));

        int imageId = context.getResources().getIdentifier(imageTitle,
                "drawable", context.getPackageName());
        viewHolder.imgThumbnail.setImageResource(imageId);
        viewHolder.setClickListener((view, position, isLongClick) -> {
            if (isLongClick) {
                Toast.makeText(context, "#" + position + " - " + actualCards.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                //context.startActivity(new Intent(context, MainActivity.class));
            } else {
                if(cardMoves.playIfPossible(actualCards.get(position))) {
                    Toast.makeText(context, "Played:" + position + " - " + imageTitle,
                            Toast.LENGTH_SHORT).show();

                    int clickedImageId = context.getResources().getIdentifier(imageTitle,
                            "drawable", context.getPackageName());

                    tablePile.setImageResource(clickedImageId);

                    actualCards.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, actualCards.size());
                } else {
                    Toast.makeText(context, "Cannot play:" + position + " - " + imageTitle,
                            Toast.LENGTH_SHORT).show();
                }

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
            clickListener.onClick(view, getLayoutPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getLayoutPosition(), true);
            return true;
        }
    }
}