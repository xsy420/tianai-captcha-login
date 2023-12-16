package com.xsy420.login.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.xsy420.login.domain.Entity;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@TableName("basis_user")
public class UserPO extends Entity {
    private String userId;
    private String username;
    private String password;
}
