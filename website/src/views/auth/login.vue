<template>
  <div class="login-bg">
    <span class="title">PROXY</span>
    <form class="form-box">
      <a-input v-model:value="formState.username" size="large" placeholder="Username" />
      <a-input v-model:value="formState.password" size="large" placeholder="Password" />
      <a-button :loading="loading" @click="loginSubmit">
        Login
      </a-button>
    </form>
    <span class="footer">fxbsujay@gmil.com</span>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { authStore } from '@/store/auth'
import { LoginModel } from '@/model/AuthModel'
const loading = ref<boolean>(false)
const formState = reactive<LoginModel>({
  username: '',
  password: ''
})

const loginSubmit = () => {
  if (!formState.username || formState.username.trim().length <= 4) {
    return
  }
  if (!formState.password || formState.password.trim().length <= 4) {
    return
  }

  loading.value = true
  authStore().login(formState).catch(() => loading.value = false)

}
</script>

<style scoped lang="less">
@import "../../styles/variable";

.login-bg {
  height: 100vh;
  background-color: @ThemeMasterColor;
  position: relative;

  @keyframes wawes {
    from {
      transform: rotate(0);
    }
    to {
      transform: rotate(360deg);
    }
  }
  .title {
    color: white;
    font-weight: bolder;
    font-size: 50px;
    margin: 20px 50px;
    position: absolute;
  }
  .footer {
    width: 100%;
    text-align: center;
    color: white;
    position: absolute;
    bottom: 30px;
  }

  .form-box {
    overflow: hidden;
    background-color: white;
    padding: 40px 30px 30px 30px;
    border-radius: 10px;
    position: absolute;
    top: 50%;
    left: 50%;
    width: 400px;
    transform: translate(-50%, -50%);
    transition: transform 300ms, box-shadow 300ms;
    box-shadow: 5px 10px 10px rgba(rgba(2, 128, 144, 1), 0.2);

    &::before, &::after {
      content: '';
      position: absolute;
      width: 600px;
      height: 600px;
      border-radius: 40% 45% 40% 35%;
    }

    &::before {
      left: 40%;
      bottom: -130%;
      background-color: rgba(80, 152, 229, 0.28);
      animation: wawes 6s infinite linear;
      z-index: -2;
    }

    &::after {
      left: 35%;
      bottom: -125%;
      background-color: rgba(2, 128, 144, 0.2);
      animation: wawes 7s infinite;
      z-index: -1;
    }

    > input {
      width: 100%;
      font-weight: bolder;
      background: white;
      border: 3px solid transparent;
      margin: 8px 0;
    }

    input::placeholder {
      color: rgba(73, 69, 69, 0.66);
    }

    .ant-input:focus, .ant-input-focused,.ant-input:hover {
      border-color: @ThemeMasterColor;
    }

    > button {
      cursor: pointer;
      color: #fff;
      font-size: 16px;
      text-transform: uppercase;
      min-width: 80px;
      border: 0;
      height: 40px;
      margin-top: 10px;
      margin-left: 10px;
      border-radius: 5px !important;
      background-color: @ThemeMasterColor;
      font-weight: bolder;
      @include transition(background-color 300ms);

      &:hover {
        background-color: darken(@ThemeMasterColor, 5%);
      }
    }
  }
}

</style>
