<template>
  <a-card class="card-box">
    <div class="card-header">
      <span>{{ name ? name : $t('home.title') }}</span>{{ unit ? unit : $t('home.unit') }}
    </div>
    <div class="card-main">
      <div class="card-number">
        <span :style="{ color: color }" class="value">{{ !value || value < 0 ? '---' : value }}</span>
        <span class="square-card-value" v-if="type === 'PLATE' && cardType === '0'">{{ $t('home.totalValue') }}： {{ totalValue }}</span>
        <div style="float: right;margin-top: -10px" v-else-if="type === 'PLATE' && cardType === '2'">
          <a-progress strokeLinecap="square" type="circle" :percent="(value  / totalValue).toFixed(2) * 100" :width="80" :strokeWidth="10" :strokeColor="color" />
        </div>
      </div>
      <div v-if="type === 'PLATE' && cardType === '0'">
        <a-progress strokeLinecap="square" :percent="(value  / totalValue).toFixed(2) * 100" :show-info="false" :strokeColor="color" />
      </div>
      <div v-else-if="type=== 'TEXT'" class="card-footer">
        <span >{{ $t('home.thanLastMonth') }}
          <IconSvg size="16" :name="parseInt(value) >= parseInt(totalValue) ? 'up' : 'down'" />
          {{ Math.abs(parseInt(value) -  parseInt(totalValue)) }} {{ unit }}
        </span>
      </div>
    </div>
  </a-card>
</template>

<script lang="ts" setup>
import IconSvg from '@/components/iconSvg/iconSvg.vue'
interface BITemplate {
  type: string
  name: string
  unit: string
  dataType: string
  value: string
  totalValue: string
  cardType: string
  size: number
  color: string
}

const props = defineProps<BITemplate>()

</script>

<style lang="less" scoped>
/deep/ .ant-card-body {
  padding: 16px 20px 0 20px;
  border-radius: 12px;
}
.card-box {
  width: 100%;
  height: 138px;
  border-radius: 12px;
  .card-header {
    span {
      font-size: 16px;
      font-weight: bold;
      margin-right: 19px;
    }
  }

  .card-main {
    margin-top: 10px;
    margin-bottom: 3px;
    position: relative;
    .value {
      font-size: 30px;
      font-weight: bold;
    }

    .card-number {
      height: 50px;
      position: relative;
      .square-card-value {
        position: absolute;
        right: 0;
        bottom: 0
      }
    }
    .card-footer {
      padding: 5px 0;
      border-top: 2px solid rgba(232, 232, 232, 1);
    }
  }
}

@media (max-width: 1400px) {
  .card-box {
    .card-header {
      span {
        font-size: 14px;
      }
    }
    .card-main {
      .value {
        font-size: 24px;
      }
    }
  }
}
</style>