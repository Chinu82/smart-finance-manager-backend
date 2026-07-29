package com.smart_finance_manager_backend.dto;

import com.smart_finance_manager_backend.enums.Role;
import com.smart_finance_manager_backend.enums.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDto {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;
    private SubscriptionPlan subscriptionPlan;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private String profileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
