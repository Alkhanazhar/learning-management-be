package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.dtos.login.LoginDto;

import java.util.Map;

public interface SuperAdminService {
    public void createSuperAdminAccount();
    public Map<String, Object> authenticateSuperAdmin(LoginDto request);
}
