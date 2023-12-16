package com.xsy420.login.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.code.kaptcha.Constants;
import com.xsy420.login.common.CommonResponse;
import com.xsy420.login.common.CommonResult;
import com.xsy420.login.domain.dto.JwtUserDTO;
import com.xsy420.login.domain.po.UserPO;
import com.xsy420.login.domain.request.LoginRequest;
import com.xsy420.login.domain.response.LoginResponse;
import com.xsy420.login.enums.CaptchaEnum;
import com.xsy420.login.mapper.UserMapper;
import com.xsy420.login.services.UserService;
import com.xsy420.login.utils.JWTUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements UserService {

    private JWTUtils jwtUtils;
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public void setJwtUtils(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private String checkCaptcha(@NotNull HttpServletRequest request, String code) {
        Object attribute = request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (attribute == null) {
            if (!CaptchaEnum.IMAGE_TEXT_IDENTIFY.getName().equals(request.getSession().getAttribute("CAPTCHA_TYPE"))) return null;
            return "请刷新";
        }
        String captcha = attribute.toString();
        if (!code.equals(captcha)) {
            return "请输入正确的验证码";
        }
        Long expire = redisTemplate.getExpire("captcha:" + code, TimeUnit.SECONDS);
        if (expire != null && expire == -2) {
            return "验证码已过期";
        }
        return null;
    }

    @Override
    public CommonResult<LoginResponse> login(HttpServletRequest httpServletRequest, LoginRequest loginRequest) {
        String code = loginRequest.getCode();
        String username = loginRequest.getUsername();
        String checkCaptcha = checkCaptcha(httpServletRequest, code);
        if (checkCaptcha != null) {
            return CommonResponse.error(checkCaptcha);
        }
        LambdaQueryWrapper<UserPO> queryWrapper = Wrappers.<UserPO>lambdaQuery()
                .eq(UserPO::getUsername, username);
        UserPO userPO = getOne(queryWrapper);
        if (userPO == null) {
            return CommonResponse.error("账号不存在");
        }
        if (!userPO.getPassword().equals(loginRequest.getPassword())) {
            return CommonResponse.error("密码不正确");
        }
        JwtUserDTO jwtUserDTO = new JwtUserDTO();
        long currentTimeMillis = System.currentTimeMillis();
        jwtUserDTO.setStartTime(currentTimeMillis);
        jwtUserDTO.setEndTime(currentTimeMillis + jwtUtils.getExpiration());
        BeanUtils.copyProperties(userPO, jwtUserDTO);
        String jwt = jwtUtils.createJwt(jwtUserDTO);
        LoginResponse loginResponse = new LoginResponse(jwt);
        boolean updateById = updateById(userPO);
        if (!updateById) {
            return CommonResponse.error("登录失败");
        }
        redisTemplate.delete("captcha:" + code);
        return CommonResponse.success(username + "登录成功", loginResponse);
    }
}
