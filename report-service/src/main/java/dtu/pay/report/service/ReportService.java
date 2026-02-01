package dtu.pay.report.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dtu.pay.report.PaymentCompletedEvent;
import dtu.pay.report.PaymentRecord;
import dtu.pay.report.dto.CustomerPaymentView;
import dtu.pay.report.dto.CustomerReport;
import dtu.pay.report.dto.CustomerReportRequest;
import dtu.pay.report.dto.ManagerReport;
import dtu.pay.report.dto.ManagerReportRequest;
import dtu.pay.report.dto.MerchantPaymentView;
import dtu.pay.report.dto.MerchantReport;
import dtu.pay.report.dto.MerchantReportRequest;

public class ReportService {

    private final List<PaymentRecord> records = new ArrayList<>();

    public void onPaymentCompleted(PaymentCompletedEvent event) {
        PaymentRecord record = new PaymentRecord(
                event.getToken(),
                event.getCustomerId(),
                event.getMerchantId(),
                event.getAmount(),
                event.getDescription(),
                event.getTimestamp()
        );
        records.add(record);
    }

    public ManagerReport handleManagerReportRequested(ManagerReportRequest request) {
        return new ManagerReport(request.getRequestId(), Collections.unmodifiableList(records));
    }

    public MerchantReport handleMerchantReportRequested(MerchantReportRequest request) {
        List<MerchantPaymentView> payments = new ArrayList<>();
        for (PaymentRecord record : records) {
            if (record.getMerchantId().equals(request.getMerchantId())) {
                payments.add(new MerchantPaymentView(
                        record.getAmount(),
                        record.getToken(),
                        record.getDescription()
                ));
            }
        }
        return new MerchantReport(request.getRequestId(), Collections.unmodifiableList(payments));
    }

    public CustomerReport handleCustomerReportRequested(CustomerReportRequest request) {
        List<CustomerPaymentView> payments = new ArrayList<>();
        for (PaymentRecord record : records) {
            if (record.getCustomerId().equals(request.getCustomerId())) {
                payments.add(new CustomerPaymentView(
                        record.getAmount(),
                        record.getMerchantId(),
                        record.getDescription(),
                        record.getToken()
                ));
            }
        }
        return new CustomerReport(request.getRequestId(), Collections.unmodifiableList(payments));
    }
}
