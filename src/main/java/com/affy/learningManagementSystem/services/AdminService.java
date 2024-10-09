package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.dtos.admin.AdminAddDto;
import com.affy.learningManagementSystem.dtos.admin.AdminDetailDto;
import com.affy.learningManagementSystem.dtos.admin.AdminUpdateDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
    Map<String, Object> createAdmin(AdminAddDto adminDto);
    Map<String, Object> updateAdmin(UUID id, AdminUpdateDto adminDto);
    AdminDetailDto getAdminById(UUID id);
    List<AdminDetailDto> getAllAdmins();
    void deleteAdmin(UUID id);
    public boolean resetPassword(UUID id, String oldPassword, String newPassword);
}
