package com.phungquocthai.symphony.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vnpay")
public class VNPayProperties {

    public static final String PAY_URL = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    public static final String RETURN_URL = "http://localhost:8080/symphony/api/payment/ipn";
    public static final String API_URL = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    private String tmnCode;
    private String secretKey;
}