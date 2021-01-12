package com.sound.KelooMacaua;

public class ActualCard {
    int cardNumber;
    String cardDescription;

    public ActualCard(int cardNumber, String cardDescription) {
        this.cardNumber = cardNumber;
        this.cardDescription = cardDescription;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
    }
}
