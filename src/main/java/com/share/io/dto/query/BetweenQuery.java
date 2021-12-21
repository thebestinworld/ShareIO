package com.share.io.dto.query;

public class BetweenQuery<T extends Comparable<? super T>> {

    private T min;
    private T max;

    public BetweenQuery() {
    }

    public BetweenQuery(T min, T max) {
        this.setMin(min);
        this.setMax(max);
//        this.setSearchRule(BetweenQuerySearchRule.EQUALS);
    }

    public T getMin() {
        return min;
    }

    public void setMin(T min) {
        this.min = min;
    }

    public T getMax() {
        return max;
    }

    public void setMax(T max) {
        this.max = max;
    }
}
