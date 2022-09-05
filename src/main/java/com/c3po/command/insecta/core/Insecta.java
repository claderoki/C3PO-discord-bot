package com.c3po.command.insecta.core;

import java.util.Objects;

public abstract class Insecta {
    public abstract int getRatePerSecond();
    public abstract int getCost();
    public abstract String getKey();
    public String getDidYouKnow() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Insecta that = (Insecta) o;
        return Objects.equals(((Insecta) o).getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

}
