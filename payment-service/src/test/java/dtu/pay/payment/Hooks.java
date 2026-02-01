package dtu.pay.payment;

import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void setUp() {
        PaymentTestContext.reset();
    }
}
