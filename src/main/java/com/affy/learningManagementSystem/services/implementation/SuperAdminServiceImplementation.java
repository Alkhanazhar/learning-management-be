package com.affy.learningManagementSystem.services.implementation;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.affy.learningManagementSystem.dtos.login.LoginDto;
import com.affy.learningManagementSystem.dtos.superAdmin.SuperAdminDetailDto;
import com.affy.learningManagementSystem.models.User;
import com.affy.learningManagementSystem.repositories.SuperAdminRepository;
import com.affy.learningManagementSystem.services.SuperAdminService;
import com.affy.learningManagementSystem.utils.JwtUtil;
import com.affy.learningManagementSystem.utils.PasswordUtils;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SuperAdminServiceImplementation implements SuperAdminService {
    @Autowired
    private SuperAdminRepository repository;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @PostConstruct
    public void createSuperAdminAccount() {

        User superAdminAccount = repository.findByRole("SUPER_ADMIN");

        if (superAdminAccount == null) {
            User superAdmin = new User();
            superAdmin.setEmail("superadmin@test.com");
            superAdmin.setName("Super Admin");
            superAdmin.setPhoneNumber("7725050222");
            superAdmin.setRole("SUPER_ADMIN");
            superAdmin.setUsername("super_admin");
            superAdmin.setPassword(passwordUtils.encryptPassword("123456")); // Encrypt password
            repository.save(superAdmin);
        }
    }

    @Override
    public Map<String, Object> authenticateSuperAdmin(LoginDto request) {
        User user = repository.findByEmail(request.getEmail());
        Map<String, Object> response = new HashMap<>();

        if (user != null) {
            BCrypt.Result result = BCrypt.verifyer().verify(request.getPassword().toCharArray(), user.getPassword());
            SuperAdminDetailDto superAdminDetails = modelMapper.map(user, SuperAdminDetailDto.class);
            if (result.verified) {
                String token = jwtUtil.generateTokenForSuperAdmin(user.getEmail(), superAdminDetails);
                response.put("response", token);
            } else {
                response.put("response", "Invalid Password");
            }
        } else {
            response.put("response", "User Not Found");
        }
        return response;
    }



}
