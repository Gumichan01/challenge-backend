package models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoundedSum {

    @JsonProperty("sum")
    private int sum;

    public RoundedSum(double amount) {
        sum = (((int) Math.ceil(amount)) + 99) / 100 * 100;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
