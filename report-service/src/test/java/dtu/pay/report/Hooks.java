package dtu.pay.report;

import io.cucumber.java.Before;

public class Hooks {

    @Before
    public void resetReportContext() {
        ReportTestContext.reset();
    }
}
