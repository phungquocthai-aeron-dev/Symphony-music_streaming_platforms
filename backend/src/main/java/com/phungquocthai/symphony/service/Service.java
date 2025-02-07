package com.phungquocthai.symphony.service;

import java.util.List;

public interface Service<T, ID> {
    T create(T dto);
    T update(T dto);
    T findById(ID id);
    List<T> findAll();
    void delete(ID id);
}
