package dtu.pay.token;

import io.cucumber.java.Before;

public class Hooks {
    @Before
    public void resetContext() {
        TokenTestContext.reset();
    }
}

