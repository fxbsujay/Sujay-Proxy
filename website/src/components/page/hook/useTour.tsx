/**
 * <p>引导页</p>
 * @author fxbsujay@gmail.com
 * @version 10:24 2023/03/10
 */
import Driver from 'driver.js'
import { InitElement } from './element'
import { nextTick } from 'vue'
import store from '@/store'
import { t } from '@/i18n'

let nextClose: boolean = false

export const driver = new Driver({
    doneBtnText: '完成',
    allowClose: false,
    opacity: 0.2,
    animate: false,
    closeBtnText: '关闭',
    nextBtnText: '下一步',
    prevBtnText: '上一步',
    onReset: Element => {
        if (!nextClose) {
            store.state.app.currentStepsPath = ''
            store.state.app.tourVisible = false
        }
    }
})


export const TourElement = (element: string) => {
    return "<div class=\"a-tour\">\n" +
        "            <svg class=\"driver-close-btn\" xmlns=\"http://www.w3.org/2000/svg\" width=\"20\" height=\"20\" viewBox=\"0 0 48 48\" fill=\"rgba(0, 0, 0, 1)\">\n" +
        "    <path d=\"M38 12.83L35.17 10 24 21.17 12.83 10 10 12.83 21.17 24 10 35.17 12.83 38 24 26.83 35.17 38 38 35.17 26.83 24z\"></path>\n" +
        "    <path d=\"M0 0h48v48H0z\" fill=\"none\"></path>\n" +
        "</svg>\n" + element +
        "        </div>"
}

export const initTourElement = () => {
    return TourElement(InitElement)
}

export const defaultElement = (title: string, description: string, prev: boolean, done: boolean) => {

    let btnGroup = prev ? "<span class=\"driver-prev-btn driver-btn\">" + t('tour.prev') + "</span>" : ""

    if (done) {
        btnGroup = btnGroup + "<span class=\"driver-close-btn driver-btn\">" + t('tour.done') + "</span>"
    } else {
        btnGroup = btnGroup + "<span class=\"driver-next-btn driver-btn\">" + t('tour.next') + "</span>"
    }

    let titleElement = ""
    if (title && title.length > 0) {
        titleElement = "<span class=\"title\">" +
                             title +
                        "</span>"
    }

    let descElement = ""
    if (description && description.length > 0) {
        descElement = "<span class=\"desc\">" +
            description +
            "</span>"
    }

    const element = "<div class=\"default-tour-element\">\n" +
                        titleElement +
                        descElement +
        "            <div class=\"btn-group\">\n" +
                        btnGroup +
        "            </div>\n" +
        "        </div>"
    return TourElement(element)
}


export default function useTour(list: Array<Driver.Step>,done: boolean = false) {

    const visible = store.state.app.tourVisible
    if (!visible || list.length === 0) {
        return
    }

    const lastStep = list[list.length - 1]
    const lastNext = lastStep.onNext

    lastStep.onNext = Element => {
        if (done) {
            store.state.app.tourVisible = false
        } else {
            nextClose = true
        }

        if (lastNext) {
            lastNext(Element)
        }
    }

    for (let step of list) {
        const previous = step.onPrevious
        step.onPrevious = Element => {
            if (previous) {
                previous(Element)
            }
            nextClose = true
        }

        if (!step.popover!.title) {
            step.popover!.title = 'title'
        }
    }

    nextTick(() => {
        driver.defineSteps(list)
        driver.start()
        nextClose = false
    }).then()

}
export const highlight = (element: string) => {
    const driver = new Driver({
        allowClose: false,
        animate: false,

    })
    driver.highlight(element)
}