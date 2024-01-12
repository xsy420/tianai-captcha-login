目录
=================

<!--ts-->
   * [tianai-captcha-login](#tianai-captcha-login)
       * [分支介绍](#分支介绍)
       * [本项目包含以下几个特点](#本项目包含以下几个特点)
       * [未实现 / 不太支持的功能](#未实现-%2F-不太支持的功能)
       * [\<tianai-captcha\>组件的传参说明](#<tianai-captcha>组件的传参说明)
       * [声明](#声明)
<!--te-->

# **tianai-captcha-login**

本项目为[tianai-captcha-demo](https://github.com/tianaiyouqing/tianai-captcha-demo)的Vue简单复刻版。

由于该demo自[2ae2270](https://github.com/tianaiyouqing/tianai-captcha-demo/commit/2ae2270acdec1c3f202642d82fa27e280838f0b6)开始接入了新UI，并对前端js代码做了加密，在Vue直接调用不是太方便。

不太方便的原因可能有以下几点：

- 使用时请求验证码接口和验证接口结构必须符合天爱的接口结构，可能过于严苛，不易扩展

- 在当时好像还没有在Vue下的调用demo

- 不是太想引入jQuary

所以通过在运行demo时扒html代码，以及[2ae2270](https://github.com/tianaiyouqing/tianai-captcha-demo/commit/2ae2270acdec1c3f202642d82fa27e280838f0b6)之前的代码作为参考，做了这个demo。 并打包成了一个vue组件`<tianai-captcha>`

## 分支介绍

本项目分为3个主分支

- [master](../../tree/master) 项目介绍

- [backend](../../tree/backend) 项目SpringBoot后端

- [frontend](../../tree/frontend) 项目Vue前端

## 本项目包含以下几个特点

- 通过Vue实现了天爱验证码的前端

- 支持类型为 `SLIDER`, `CONCAT`, `ROTATE`, `WORD_IMAGE_CLICK` 的行为验证码

- 附加了一个登录demo， 展现了`<tianai-captcha>`的使用方法

- 另添加了一个看图识字的验证码 `IMAGE_TEXT_IDENTIFY` (随机生成4个数字字母，前端做输入验证)

    **注：该功能不在`<tianai-captcha>`组件内实现，只是作为本示例的一个扩展演示**

- 后端接口可以对原始验证码数据进行包装

本示例进行如下封装进行传出

```jsonc
{
    "code": 0,
    "data": {
        //原始验证码数据
    },
    "message" : "成功"
}
```

## 未实现 / 不太支持的功能

- 验证码的图片只能是官方demo中提供的图片，因为前端图片宽高目前还是写死的，没有过多的计算比例

- 不包含官方增强版的内容

- 暂不支持手机端

- ...

## `<tianai-captcha>`组件的传参说明

- Props:
    - captcha\_data 天爱验证码的原始验证码数据类型

    - captcha\_valid 天爱验证码验证结果

    - refresh\_captcha 是否重新获取验证码

- Emits: 需要传入修改以上三个参数的方法
    - changeData

    - changeValid

    - changeRefresh

## 声明

**本项目是通过对前端效果模拟自主实现的，和官方提供的可能还是有差异和不太支持的功能。只能做到看起来像，可以运行，没有明显bug。**
