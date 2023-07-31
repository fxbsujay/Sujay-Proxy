import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import Components from 'unplugin-vue-components/vite'
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import * as path from 'path'

export default defineConfig({
  base: '/',
  resolve: {
    alias: {
        "@": path.resolve(__dirname, 'src'),
    }
  },
  plugins: [
      vue(),
      vueJsx(),
      Components({
        resolvers: [AntDesignVueResolver()]
      }),
      createSvgIconsPlugin({
          iconDirs: [path.resolve(process.cwd(), 'src/assets/icons')],
          symbolId: '[name]'
      })
  ],
  server: {
      open: true,
      port: 8891,
      proxy: {
          '/api': {
              target: 'https://fengkeai-ui-pro.1cno.com',
              changeOrigin: true,
              ws: true,
              rewrite: (path) => path.replace(/^\/api/, 'api'),
          }
      }
  },
  build: {
      chunkSizeWarningLimit: 1600
  }
})
