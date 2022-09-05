package com.c3po.command.insecta.core;

public class Mosquito extends Insecta {
    @Override
    public int getRatePerSecond() {
        return 1;
    }

    @Override
    public String getDidYouKnow() {
        return "Mosquito's drink blood to help produce eggs; meaning only female mosquito's drink blood.";
    }

    @Override
    public int getCost() {
        return 50;
    }

    @Override
    public String getKey() {
        return "mosquito";
    }
}