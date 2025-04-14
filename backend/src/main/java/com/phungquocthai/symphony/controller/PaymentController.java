package com.phungquocthai.symphony.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.configuration.VNPayConfig;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.utils.VNPayUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	@PostMapping("/create-payment")
	public ResponseEntity<ApiResponse<String>> createPayment(@RequestParam(required = true) int amount,
	                                       @RequestParam(required = false) String language,
	                                       @RequestParam(required = false) String bankCode,
	                                       @RequestParam(required = true) Integer userId,
	                                       HttpServletRequest request
	                                       ) throws UnsupportedEncodingException {
	    String vnp_Version = "2.1.0";
	    String vnp_Command = "pay";
	    String orderType = "other";
	    long vnp_Amount = amount * 100L;
	    String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
	    String vnp_IpAddr = VNPayUtils.getIpAddress(request);
	    String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

	    Map<String, String> vnp_Params = new HashMap<>();
	    vnp_Params.put("vnp_Version", vnp_Version);
	    vnp_Params.put("vnp_Command", vnp_Command);
	    vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
	    vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
	    vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode != null ? bankCode : "NCB");
	    vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
	    vnp_Params.put("vnp_OrderInfo", "userId=" + userId + "|Thanh toan don hang: " + vnp_TxnRef);
	    vnp_Params.put("vnp_OrderType", orderType);
	    vnp_Params.put("vnp_Locale", language != null ? language : "vn");
	    vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
	    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
	    
   
	    
	    Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

	    List<String> fieldNames = new ArrayList<String>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;
	    Map<String, String> response = new HashMap<>();
	    response.put("code", "00");
	    response.put("message", "success");
	    response.put("data", paymentUrl);
	    
	    
	    return ResponseEntity.ok(
	    		ApiResponse.<String>builder()
	    		.result(paymentUrl)
	    		.build()
	    		);
	}

	@GetMapping("/ipn")
	public ResponseEntity<?> handleVnpayReturn(@RequestParam Map<String, String> allParams) {
	    String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
	    String vnp_OrderInfo = allParams.get("vnp_OrderInfo");

	    // Kiểm tra hash (bảo mật)
	    boolean isValid = VNPayConfig.validateSignature(allParams);
	    if (!isValid) {
	        return ResponseEntity.badRequest().body("Chữ ký không hợp lê");
	    }

	    if ("00".equals(vnp_ResponseCode)) {

	        String userId = extractUserIdFromOrderInfo(vnp_OrderInfo);

	        return ResponseEntity.ok("Thanh toán thành công");
	    } else {
	        return ResponseEntity.badRequest().body("Thanh toán thất bại: " + vnp_ResponseCode);
	    }
	}

	private String extractUserIdFromOrderInfo(String orderInfo) {
	    if (orderInfo == null) return null;
	    String[] parts = orderInfo.split("\\|");
	    for (String part : parts) {
	        if (part.startsWith("userId=")) {
	            return part.replace("userId=", "");
	        }
	    }
	    return null;
	}

}
