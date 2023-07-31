<template>
  <a-result :status="404" title="404" :sub-title="$t('view404.subTitle')">
    <template #extra>
      <router-link to="/home">
        <a-button type="primary">{{ $t('view404.back') }}</a-button>
      </router-link>
    </template>
  </a-result>
</template>