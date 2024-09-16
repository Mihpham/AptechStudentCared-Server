package com.example.aptechstudentcaredserver.service;

import com.example.aptechstudentcaredserver.bean.request.SroRequest;
import com.example.aptechstudentcaredserver.bean.response.SroResponse;

import java.util.List;

public interface SroService {
    public void registerSro(SroRequest sroRequest);
    public List<SroResponse> findAllSro();
    public SroResponse findSroById(int sroId);
    public SroResponse updateSro(int sroId, SroRequest sroRequest);
    public void deleteSro(int sroId);
}
