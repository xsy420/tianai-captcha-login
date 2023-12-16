package com.xsy420.login.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUserDTO {
    private String userId;
    private String username;
    private Long startTime;
    private Long endTime;
}
