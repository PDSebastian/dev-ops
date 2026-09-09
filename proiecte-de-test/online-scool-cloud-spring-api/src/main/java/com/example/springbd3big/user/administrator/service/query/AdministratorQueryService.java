package com.example.springbd3big.user.administrator.service.query;

import com.example.springbd3big.user.administrator.dtos.AdministratorResponse;

import java.util.List;

public interface AdministratorQueryService {

    List<AdministratorResponse> getAllAdministrators();

    AdministratorResponse getAdministratorById(Long administratorId);

    AdministratorResponse findByEmail(String email);

    List<AdministratorResponse> searchAdministrators(String query);
}
