package dtu.pay.facade;

import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void resetFacadeContext() {
        FacadeTestContext.reset();
    }
}
