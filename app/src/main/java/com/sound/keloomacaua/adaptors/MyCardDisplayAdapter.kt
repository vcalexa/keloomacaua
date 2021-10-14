package com.sound.keloomacaua.adaptors

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sound.keloomacaua.R
import com.sound.keloomacaua.game.CardMoves
import com.sound.keloomacaua.game.CardUtils.Companion.cardToImageId
import com.sound.keloomacaua.interfaces.CardTapListener

class MyCardDisplayAdapter(cardTapListener: CardTapListener? = null) :
    RecyclerView.Adapter<MyCardDisplayAdapter.ViewHolder>() {
    private val ownCards: MutableList<Int> = ArrayList()
    private var clickListener: CardTapListener? = cardTapListener

    @SuppressLint("NotifyDataSetChanged")
    fun setOwnCards(ownCards: List<Int>) {
        this.ownCards.clear()
        this.ownCards.addAll(ownCards)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_card_front, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, cardIndex: Int) {
        val context = viewHolder.container.context
        viewHolder.imgThumbnail.setImageResource(cardToImageId(ownCards[cardIndex], context))
        if (CardMoves.canPlayCardAt(cardIndex)) {
            viewHolder.container.elevation = 50f
            viewHolder.container.animate().translationY(-16f).start()
        } else {
            viewHolder.container.animate().translationY(32f).start()
            viewHolder.container.elevation = 0f
        }
        clickListener?.let { listener ->
            viewHolder.container.setOnClickListener {
                listener.onCardTapped(cardIndex)
            }
        }
    }

    override fun getItemCount(): Int {
        return ownCards.size
    }

    class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        var container: View = imgThumbnail

    }
}