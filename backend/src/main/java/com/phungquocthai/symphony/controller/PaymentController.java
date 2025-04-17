package com.phungquocthai.symphony.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phungquocthai.symphony.configuration.VNPayConfig;
import com.phungquocthai.symphony.constant.Role;
import com.phungquocthai.symphony.constant.SubscriptionStatus;
import com.phungquocthai.symphony.dto.ApiResponse;
import com.phungquocthai.symphony.entity.Subscription;
import com.phungquocthai.symphony.entity.User;
import com.phungquocthai.symphony.entity.Vip;
import com.phungquocthai.symphony.repository.SubscriptionRepository;
import com.phungquocthai.symphony.repository.UserRepository;
import com.phungquocthai.symphony.repository.VipRepository;
import com.phungquocthai.symphony.utils.VNPayUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	VipRepository vipRepository;
	
	@Autowired
	SubscriptionRepository subscriptionRepository;
	
	@PostMapping("/create-payment")
	public ResponseEntity<ApiResponse<String>> createPayment(@RequestParam(required = true) int amount,
	                                       @RequestParam(required = false) String language,
	                                       @RequestParam(required = false) String bankCode,
	                                       @RequestParam(required = true) Integer userId,
	                                       @RequestParam(required = true) Integer vipId,
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
	    vnp_Params.put("vnp_OrderInfo", 
	    	    "userId=" + userId + 
	    	    "|vipId=" + vipId + 
	    	    "|Thanh toan don hang: " + vnp_TxnRef);
	    vnp_Params.put("vnp_OrderType", orderType);
	    vnp_Params.put("vnp_Locale", language != null ? language : "vn");
	    vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
	    vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
	    System.out.println("us:" + userId + "vip" + vipId);
   
	    
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
	public void handleVnpayReturn(@RequestParam Map<String, String> allParams, HttpServletResponse response) throws IOException {
	    String vnp_ResponseCode = allParams.get("vnp_ResponseCode");
	    String payment_id = allParams.get("vnp_TxnRef");
	    String vnp_OrderInfo = allParams.get("vnp_OrderInfo");

	    // Kiểm tra hash (bảo mật)
	    boolean isValid = VNPayConfig.validateSignature(allParams);
	    if (!isValid) {
	    	response.sendRedirect("http://localhost:4200/error");
	    }

	    if ("00".equals(vnp_ResponseCode)) {
	    	Map<String, String> infoMap = parseOrderInfo(vnp_OrderInfo);

	    	System.out.println("us=" + infoMap.get("userId") + "vip" + infoMap.get("vipId"));
	    	String userIdStr = infoMap.get("userId");
	    	String vipIdStr = infoMap.get("vipId");

	    	int userId = Integer.parseInt(userIdStr);
	    	int vipId = Integer.parseInt(vipIdStr);
	    	
	    	User user = userRepository.findById(userId).orElseThrow();
	    	Vip vip = vipRepository.findById(vipId).orElseThrow();
	    	
	    	LocalDate startDate = LocalDate.now();
	    	LocalDate endDate = startDate.plusDays(vip.getDuration_days());
	    	
	    	Subscription subscription = new Subscription(null, user, vip, 
	    			startDate, endDate, SubscriptionStatus.ACTIVE.getDisplayName(), payment_id, null);
	    	
	    	subscriptionRepository.save(subscription);
	    	user.setRole(Role.VIP.getValue());
	    	userRepository.save(user);

	        response.sendRedirect("http://localhost:4200/upgrade");
	    } else {
	    	response.sendRedirect("http://localhost:4200/error");
	    }
	}

	public static Map<String, String> parseOrderInfo(String orderInfo) {
	    Map<String, String> result = new HashMap<>();
	    String[] parts = orderInfo.split("\\|");
	    
	    for (String part : parts) {
	        if (part.contains("=")) {
	            String[] keyValue = part.split("=", 2);
	            result.put(keyValue[0], keyValue[1]);
	        }
	    }
	    
	    return result;
	}


}
