import { nextTick, ref } from 'vue'

export enum ScrollPosition {
    TOP = 'TOP',
    BOTTOM = 'BOTTOM'
}

/**
 * <p>监听页面滚动 hook</p>
 * @author fxbsujay@gmail.com
 * @version 17:07 2023/05/17
 */
export const useScroll = (selectorName: string) => {

    let dom: Element

    const instantiatingElement = () => {
        nextTick(() => {
            dom = document.querySelector(selectorName)!
        })
    }

    instantiatingElement()

    /**
     * 滚动到最底部时触发
     * @param fun 触发的方法
     */
    const onScrollBottomHandle = (fun: Promise<boolean>) => {
        const state = ref<boolean>(false)
        if (!dom) {
            return
        }

        dom.addEventListener('scroll', function () {
            if (state.value) {
                return
            }

            if (dom!.scrollHeight <= dom!.scrollTop + dom!.clientHeight) {
                state.value = true
                fun.then(() => { state.value = false }).catch(() => state.value = false)
            }
        }, { passive: true })
    }

    const scrollTo = (position: number | ScrollPosition,) => {

        if (!dom) {
            return
        }

        nextTick(() => {
            if (typeof position === 'number') {
                dom.scrollTop = position
                return;
            }
            switch (position) {
                case ScrollPosition.TOP:
                    dom.scrollTop = 0
                    break
                case ScrollPosition.BOTTOM:
                    dom.scrollTop = dom.scrollHeight
                    break
                default:
                    break
            }
        })

    }

    return {
        instantiatingElement,
        onScrollBottomHandle,
        scrollTo
    }
}

export default useScroll