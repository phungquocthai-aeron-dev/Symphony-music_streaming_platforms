package com.phungquocthai.symphony.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.phungquocthai.symphony.repository.ListenRepository;

@Service
public class ListenService {
    
	@Autowired
    private ListenRepository listenRepository;
   
    
    public Integer thongKeTheoThang(int thang, int nam) {
        return listenRepository.thongKeTheoThang(thang, nam);
    }
}