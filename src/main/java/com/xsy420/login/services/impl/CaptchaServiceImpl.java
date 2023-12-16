package com.xsy420.login.services.impl;

import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.store.impl.RedisCacheStore;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.xsy420.login.domain.request.ImageCaptchaCheckRequest;
import com.xsy420.login.enums.CaptchaEnum;
import com.xsy420.login.enums.EnumUtil;
import com.xsy420.login.services.CaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    private Producer captchaProducer;
    private ImageCaptchaApplication imageCaptchaApplication;
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public void setCaptchaProducer(Producer captchaProducer) {
        this.captchaProducer = captchaProducer;
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    public void setImageCaptchaApplication(ImageCaptchaApplication imageCaptchaApplication) {
        this.imageCaptchaApplication = imageCaptchaApplication;
    }

    private CaptchaResponse<ImageCaptchaVO> image_text_identify(HttpServletRequest request) throws Exception {
        String captchaId = UUID.randomUUID().toString().replace("-", "");

        Object oldCaptcha = request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
        if (oldCaptcha != null) {
            stringRedisTemplate.delete("captcha:" + oldCaptcha);
        }

        // create the text for the image
        String capText = captchaProducer.createText();

        stringRedisTemplate.opsForValue().set("captcha:" + capText, capText, 20, TimeUnit.SECONDS);
        // store the text in the session
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        log.info("验证码是：" + capText);

        // create the image with the text
        BufferedImage bi = captchaProducer.createImage(capText);

        // https://devv.ai/search?threadId=d4aq5tbvlz40
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpeg", baos);
        byte[] byteArray = baos.toByteArray();
        baos.close();
        ImageCaptchaVO imageCaptchaVO = new ImageCaptchaVO();
        imageCaptchaVO.setType(CaptchaEnum.IMAGE_TEXT_IDENTIFY.getName());
        imageCaptchaVO.setBackgroundImage("data:image/jpeg;base64," + Base64.getEncoder().encodeToString(byteArray));
        return CaptchaResponse.of(captchaId, imageCaptchaVO);
    }

    /**
     * 如果 Session 中没有存验证码类型 随机生成一个类型
     * 如果之前 CAPTCHA_TYPE 为 IMAGE_TEXT_IDENTIFY，则一直为 IMAGE_TEXT_IDENTIFY
     * 如果不是，随机生成一个天爱验证码
     */
    @Override
    public CaptchaResponse<ImageCaptchaVO> generate(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        Object CAPTCHA_TYPE = session.getAttribute("CAPTCHA_TYPE");
        int code;

        if (CAPTCHA_TYPE == null) {
            code = ThreadLocalRandom.current().nextInt(1, 6);
            CAPTCHA_TYPE =  EnumUtil.fromCode(CaptchaEnum.class, code).getName();
        }
        if (!CAPTCHA_TYPE.equals(CaptchaEnum.IMAGE_TEXT_IDENTIFY.getName())) {
            code = ThreadLocalRandom.current().nextInt(1, 5);
            CAPTCHA_TYPE =  EnumUtil.fromCode(CaptchaEnum.class, code).getName();
        }

        code = EnumUtil.fromValue(CaptchaEnum.class, CAPTCHA_TYPE.toString()).getCode();

        session.setAttribute("CAPTCHA_TYPE", CAPTCHA_TYPE);
        if (code == 5) {
            return image_text_identify(request);
        } else {
            CaptchaResponse<ImageCaptchaVO> image = imageCaptchaApplication.generateCaptcha(CAPTCHA_TYPE.toString());
            imageCaptchaApplication.setCacheStore(new RedisCacheStore(stringRedisTemplate));
            return image;
        }
    }

    @Override
    public ApiResponse<?> checkCaptcha(ImageCaptchaCheckRequest data, HttpServletRequest request) {
        return imageCaptchaApplication.matching(data.getId(), data.getData());
    }
}
