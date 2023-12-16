package com.xsy420.login.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xsy420.login.domain.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

}
