package com.sound.KelooMacaua.adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sound.KelooMacaua.ActualCard;
import com.sound.KelooMacaua.R;

import java.util.List;

public class BottomCardsAdaptor extends ArrayAdapter<ActualCard> {

    Context mContext;

    public BottomCardsAdaptor(@NonNull Context context, int resource, List<ActualCard> cardList) {
        super(context, resource, cardList);
        mContext = context;
    }

    private static class ViewHolder {
        ImageView imageView;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ActualCard card = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item, parent, false);

            viewHolder.imageView = convertView.findViewById(R.id.imgThumbnail);


            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        int imageId = mContext.getResources().getIdentifier(card.getCardDescription(),
                "drawable", mContext.getPackageName());
        viewHolder.imageView.setImageResource(imageId);

        return result;
    }
}
