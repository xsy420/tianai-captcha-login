package com.xsy420.login.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Entity {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String createTime;
    @TableField(value = "modify_time", update = "now()")
    private String modifyTime;
}
