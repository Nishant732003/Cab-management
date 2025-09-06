package com.cabbooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cabbooking.dto.AdminRegistrationRequest;
import com.cabbooking.model.Admin;
import com.cabbooking.repository.AdminRepository;
import com.cabbooking.repository.CustomerRepository;
import com.cabbooking.repository.DriverRepository;

@Service
public class AdminRegistrationServiceImpl implements IAdminRegistrationService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin registerAdmin(AdminRegistrationRequest request) {
        if (adminRepository.existsByUsername(request.getUsername()) || 
            customerRepository.existsByUsername(request.getUsername()) || 
            driverRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (adminRepository.existsByEmail(request.getEmail()) || 
            customerRepository.existsByEmail(request.getEmail()) || 
            driverRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        // --- Correctly use the fields inherited from AbstractUser ---
        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPassword(passwordEncoder.encode(request.getPassword()));
        admin.setEmail(request.getEmail());
        admin.setAddress(request.getAddress());
        admin.setMobileNumber(request.getMobileNumber());
        admin.setVerified(false);

        return adminRepository.save(admin);
    }
}