package com.smart_finance_manager_backend.service;

import com.smart_finance_manager_backend.dto.*;
import com.smart_finance_manager_backend.entity.User;
import com.smart_finance_manager_backend.enums.Role;
import com.smart_finance_manager_backend.enums.SubscriptionPlan;
import com.smart_finance_manager_backend.exception.BadRequestException;
import com.smart_finance_manager_backend.exception.ResourceNotFoundException;
import com.smart_finance_manager_backend.exception.UnauthorizedException;
import com.smart_finance_manager_backend.repository.UserRepository;
import com.smart_finance_manager_backend.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    public AuthResponseDto register(RegisterDto registerDto) {
        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        String email = registerDto.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email is already registered");
        }

        if (registerDto.getPhoneNumber() != null && !registerDto.getPhoneNumber().isBlank()
                && userRepository.existsByPhoneNumber(registerDto.getPhoneNumber())) {
            throw new BadRequestException("Phone number is already registered");
        }

        User user = User.builder()
                .fullName(registerDto.getFullName().trim())
                .phoneNumber(
                        registerDto.getPhoneNumber() != null
                                ? registerDto.getPhoneNumber().trim()
                                : null
                )
                .email(registerDto.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(registerDto.getPassword()))
                .role(Role.USER)
                .subscriptionPlan(SubscriptionPlan.FREE)
                .emailVerified(false)
                .phoneVerified(false)
                .accountLocked(false)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser);

        return mapToAuthResponse(savedUser, token);
    }

    public AuthResponseDto login(LoginDto loginDto) {
        String email = loginDto.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (Boolean.FALSE.equals(user.getEnabled())) {
            throw new UnauthorizedException("Account is disabled. Please contact support.");
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new UnauthorizedException("Account is locked. Please contact support.");
        }

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        return mapToAuthResponse(user, token);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private AuthResponseDto mapToAuthResponse(User user, String token) {
        return AuthResponseDto.builder()
                .token(token)
                .type("Bearer")
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .subscriptionPlan(user.getSubscriptionPlan())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    public void forgotPassword(ForgotPasswordDto dto) {
        User user = userRepository.findByEmail(dto.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with this email"));

        String otp = String.valueOf(
                100000 + SECURE_RANDOM.nextInt(900000)
        );
        user.setResetOtp(otp);
        user.setResetOtpExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        emailService.sendOtpEmail(user.getEmail(), otp);
    }

    public void verifyOtp(VerifyOtpDto dto) {
        User user = userRepository.findByEmail(dto.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(dto.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }
        if (LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            throw new BadRequestException("OTP expired");
        }
    }

    public void resetPassword(ResetPasswordDto dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        String email = dto.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getResetOtp() == null || !user.getResetOtp().equals(dto.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }
        if (LocalDateTime.now().isAfter(user.getResetOtpExpiry())) {
            throw new BadRequestException("OTP expired");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        userRepository.save(user);
    }
}
