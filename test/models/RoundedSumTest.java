package models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RoundedSumTest {

    @Test
    public void calculate_rounded_sum_to_the_upper_hundred() {
        assertEquals(100, new RoundedSum(100.0).getSum());
        assertEquals(100, new RoundedSum(50.0).getSum());
        assertEquals(100, new RoundedSum(50.42).getSum());
        assertEquals(200, new RoundedSum(101.0).getSum());
        assertEquals(100, new RoundedSum(0.42).getSum());
        assertEquals(0, new RoundedSum(0.0).getSum());
    }
}