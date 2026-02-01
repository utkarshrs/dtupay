package dtu.pay.account;

import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void resetAccountContext() {
        AccountTestContext.reset();
    }
}
