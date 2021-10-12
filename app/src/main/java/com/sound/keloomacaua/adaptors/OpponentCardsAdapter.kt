@file:Suppress("JoinDeclarationAndAssignment")

package com.sound.keloomacaua.adaptors

import android.annotation.SuppressLint
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sound.keloomacaua.R
import com.sound.keloomacaua.game.CardMoves
import kotlin.math.roundToInt

class OpponentCardsAdapter : RecyclerView.Adapter<OpponentCardsAdapter.ViewHolder>() {
    private var cards: Int = 0
    private var parentWidth: Int = 0

    @SuppressLint("NotifyDataSetChanged")
    fun setCards(numCards: Int) {
        this.cards = numCards
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return cards
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_card_back, viewGroup, false)
        parentWidth = viewGroup.width
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, cardIndex: Int) {
        val context = holder.cardContainer.context

        // overwrite width to allow cards to stack
        val smallCardWidth =
            context.resources.getDimension(R.dimen.smallCardWidth)
        val width = (parentWidth - 2 * smallCardWidth) / itemCount
        val params = holder.cardContainer.layoutParams
        params.width = width.roundToInt()
        holder.cardContainer.layoutParams = params

        // rotate cards for fan effect
        holder.cardContainer.rotation = -(itemCount / 2f - cardIndex.toFloat()) * 2f
        // push cards toward the center to create more of a fan effect
        holder.cardContainer.translationX =
            1.1f * smallCardWidth * (1 - cardIndex.toFloat() / itemCount)
        holder.cardContainer.pivotX = smallCardWidth * 0.5f
        holder.cardContainer.pivotY = smallCardWidth

        // elevate opponent cards when it's their turn
        if (CardMoves.isCurrentPlayer) {
            holder.imgThumbnail.elevation = 1f
        } else {
            holder.imgThumbnail.elevation = 32.dp.toFloat()
        }
    }

    class ViewHolder internal constructor(var cardContainer: View) :
        RecyclerView.ViewHolder(cardContainer) {
        var imgThumbnail: ImageView

        init {
            this.imgThumbnail = cardContainer.findViewById(R.id.imgThumbnail)
        }
    }

    private val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

}