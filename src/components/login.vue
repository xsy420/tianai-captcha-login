<script lang="ts" setup>
import type {FormInstance, FormRules} from 'element-plus'
import axios from "axios";
import {ref, reactive, computed, watchEffect, Ref} from "vue";
import MD5Util from "@/utils/MD5Util.ts";
import api from "@/assets/api.ts";
import TianaiCaptcha from "@/components/tianai-captcha.vue";

class LoginForm {
  code: string = ''
  username: string = 'administrator'
  password: string = 'admin123456'
}

const ruleFormRef = ref<FormInstance>()
const loginForm = ref(new LoginForm())

const rules = reactive<FormRules<LoginForm>>({
  code: [
    {required: true, message: "请输入验证码", trigger: 'blur'},
    {min: 4, max: 4, message: '请输入4位验证码', trigger: 'blur'}
  ],
  username: [
    {required: true, message: "请输入用户名", trigger: 'blur'},
    {min: 4, max: 15, message: '用户名必须为4-15位', trigger: 'blur'}
  ],
  password: [
    {required: true, message: "请输入密码", trigger: 'blur'},
    {min: 6, max: 15, message: '密码必须为6-15位', trigger: 'blur'}
  ]
})

interface LoginResponse {
  readonly jwt: string
}

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid) => {
    if (valid) {
      if (start_captcha.value === false) {
        start_captcha.value = true
        refresh_captcha.value = true
        return
      }
      if (!isITI.value && !captcha_valid.value) {
        api.message(1, "验证码未通过")
        return
      }
      api.post('basis-login/login', {
        ...loginForm.value,
        password: MD5Util.Instance.get_md5(loginForm.value.password)
      }).then(value => {
        let {code, data} = value
        if (code === 0) {
          localStorage.setItem('token', (data as LoginResponse).jwt)
        } else {
          refresh_captcha.value = true
        }
      })
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  start_captcha.value = false
  captcha_valid.value = false
  formEl.resetFields()
}

// 到此，登录相关基础逻辑结束
// 下面开始验证码逻辑

const start_captcha = ref(false)

interface CaptchaInnerDataTemplate {
  randomY: number
}

class CaptchaType {
  type: 'SLIDER' | 'CONCAT' | 'ROTATE' | 'WORD_IMAGE_CLICK' | 'IMAGE_TEXT_IDENTIFY' | '' = ''
  backgroundImage: string = ''
  backgroundImageHeight: number = 0
  backgroundImageWidth: number = 0
  templateImage: string = ''
  templateImageHeight: number = 0
  templateImageWidth: number = 0
  data: CaptchaInnerDataTemplate = {} as CaptchaInnerDataTemplate
}

class CaptchaData {
  id: string = ''
  captcha: CaptchaType = new CaptchaType()
}

const change_captcha = () => {
  axios.create({
    withCredentials: true
  }).get('api/captcha').then(value => value.data as CaptchaData)
      .then(value => captcha_data.value = value)
}


// 父 <-> 子 : captcha_data 的 type
// 父 <-> 子 : 是否 valid
// 父 <-> 子 : 是否需要重置(IMAGE_TEXT_IDENTIFY)验证码
const captcha_data = ref(new CaptchaData())
const captcha_valid = ref(false)
const refresh_captcha = ref(false)

function defaultChangeEmits<T>(valRef: Ref<T>) {
  return (val: T) => valRef.value = val
}
const change_captcha_data = defaultChangeEmits(captcha_data)
const change_valid = defaultChangeEmits(captcha_valid)
const change_refresh = defaultChangeEmits(refresh_captcha)

const isITI = computed(() => {
  return captcha_data.value.captcha.type === 'IMAGE_TEXT_IDENTIFY'
})

watchEffect(() => {
  if (refresh_captcha.value) {
    change_captcha()
    refresh_captcha.value = false
  }
})
</script>

<template>
    <el-form
        ref="ruleFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="120px"
        status-icon
    >
      <el-form-item label="用户名：" prop="username" :label-width=100>
        <el-input v-model="loginForm.username"/>
      </el-form-item>
      <el-form-item label="密码：" prop="password" :label-width=100>
        <el-input type="password" v-model="loginForm.password"/>
      </el-form-item>

      <!--下面两个 el-form-item 当 type === 'IMAGE_TEXT_IDENTIFY' 时出现-->
      <el-form-item label="验证码：" prop="code" v-if="start_captcha && isITI && !captcha_valid" :label-width=100>
        <el-input v-model="loginForm.code"/>
      </el-form-item>
      <el-form-item label=" " :label-width=100 v-if="start_captcha && isITI && !captcha_valid">
        <el-image :src="captcha_data.captcha.backgroundImage" @click="change_captcha"/>
      </el-form-item>

      <!--引入tianai-captcha组件-->
      <tianai-captcha
          @change-valid="change_valid" @change-refresh="change_refresh" @change-data="change_captcha_data"
          :captcha_data="captcha_data" :refresh_captcha="refresh_captcha" :captcha_valid="captcha_valid"
          v-if="start_captcha && !isITI && !captcha_valid">
      </tianai-captcha>

      <el-form-item v-if="captcha_valid" style="color: red; font-weight: bold">
        验证成功
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submitForm(ruleFormRef)">登录</el-button>
        <el-button @click="resetForm(ruleFormRef)">重置</el-button>
      </el-form-item>
    </el-form>
</template>

<style scoped>

</style>
