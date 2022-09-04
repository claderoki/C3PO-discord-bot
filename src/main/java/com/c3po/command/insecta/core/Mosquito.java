package com.c3po.command.insecta.core;

public class Mosquito extends Insecta {
    @Override
    public int getRatePerSecond() {
        return 3;
    }

    @Override
    public int getCost() {
        return 50;
    }

    @Override
    public String getKey() {
        return "mosquito";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}