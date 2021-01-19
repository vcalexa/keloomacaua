package com.sound.keloomacaua;

import com.sound.keloomacaua.game.CardMoves;
import com.sound.keloomacaua.game.CardUtils;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    private CardUtils card = new CardUtils();
    private CardMoves cardMoves = CardMoves.getInstance();





    @Test
    public void addition_isCorrect() {
        cardMoves.deal();
        card.hasSameRank(1,6);
    }
}