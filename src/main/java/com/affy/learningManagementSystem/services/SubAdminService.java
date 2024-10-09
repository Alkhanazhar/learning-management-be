package com.affy.learningManagementSystem.services;

import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminAddDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminDetailDto;
import com.affy.learningManagementSystem.dtos.subAdmin.SubAdminUpdateDto;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface SubAdminService {
    Map<String, Object> createSubAdmin(SubAdminAddDto subAdminAddDto);
    Map<String, Object> updateSubAdmin(UUID id, SubAdminUpdateDto subAdminUpdateDto);
    SubAdminDetailDto getSubAdminById(UUID id);
    List<SubAdminDetailDto> getAllSubAdminsByAdminId(UUID adminId);
    void deleteSubAdmin(UUID id);
    boolean resetPassword(UUID id, String oldPassword, String newPassword);
    public List<SubAdminDetailDto> getAllSubAdminsByRole();
}
