package com.c3po.error;

public class ThresholdFailed extends Exception {
    public ThresholdFailed(int before, int after) {
        super("Threshhold failed.");
    }
}
