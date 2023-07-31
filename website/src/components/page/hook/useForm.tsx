/**
 * <p>表单 hook</p>
 * @author fxbsujay@gmail.com
 * @version 10:24 2023/03/10
 */
import { reactive, Ref, nextTick } from 'vue'

import { BaseModel } from '@/model/BaseObject'
import { Props } from 'ant-design-vue/lib/form/useForm'
import { Form, message } from 'ant-design-vue'
import Copy from '@/utils/copy'
import { t } from '@/i18n'

export interface FormContext {
    title: string
    centered: boolean
    width: string | number | Ref<string>
    visible: boolean
    footer?: null
    loading: boolean,
    callbackFunc?: Function
    init: (callback: () => void, data?: any) => void
    onCloseHandle?: () => void
    onAddOrUpdateSubmitHandle?: () => void
}

/**
 * initializerHandle： 初始化调用
 * beforeSubmitHandle: 请求前置处理
 * afterSubmitHandle： 请求后置
 */
export interface FormOptions<T extends BaseModel = any> {
    title?: string,
    formState: T | Ref<T>
    callbackFunction?: string,
    rules: Props | Ref<Props>
    width?: string | number | Ref<string>
    initializerHandle?: (data?: any) => void
    beforeSubmitHandle?: () => void
    afterSubmitHandle?: () => void
    afterCloseHandle?: () => void
    saveApi?: (...arg: any) => Promise<any>
    updateApi: (...arg: any) => Promise<any>
}

export const useRegisterForm = (options: FormOptions) => {
    const useForm = Form.useForm

    const formValidate = useForm(options.formState,options.rules)
    const originalFormState = Copy.DeepClone(options.formState,{})

    function successHandle () {

        message.success(t('component.submittedSuccessfully'),0.5).then( () => {

            formContext.loading = false
            formContext.visible = false

            if (options.afterSubmitHandle) {
                options.afterSubmitHandle()
            }
            if (formContext.callbackFunc) {
                formContext.callbackFunc()
            }
        })
    }

    const formContext = reactive<FormContext>({
        title: '',
        centered: true,
        width: options.width ? options.width: '50%',
        visible: false,
        loading: false,
        init: (callback: () => void, data: any ) => {

            if (callback) {
                formContext.callbackFunc = callback
            }

            if (data && data.id) {
                Copy.SimpleClone(data, options.formState)
                formValidate.clearValidate()
            } else {
                Copy.DeepClone(originalFormState,options.formState)
                formValidate.resetFields()
            }

            if (options.initializerHandle) {
                options.initializerHandle(data)
            }

            if (options.title) {
                formContext.title = options.title
            } else {
                formContext.title = data ?  'edit' : 'add'
            }

            formContext.visible = true
            nextTick(() => formValidate.clearValidate())
        },
        onCloseHandle: () => {
            formContext.visible = false
            formContext.loading = false
            formValidate.clearValidate()
            if (options.afterCloseHandle) {
                options.afterCloseHandle()
            }
        },
        onAddOrUpdateSubmitHandle: () => {
            const { validate } = formValidate
            validate().then(() => {

                if (options.beforeSubmitHandle) {
                    options.beforeSubmitHandle()
                }
                let data = Copy.JsonClone(options.formState)
                const { saveApi, updateApi } = options
                formContext.loading = true
                if (options.formState.id) {
                    updateApi(data).then(() => {
                        successHandle()
                    }).catch(() => {
                        formContext.loading = false
                    })
                } else {
                    if (saveApi) {
                        saveApi(data).then(() => {
                            successHandle()
                        }).catch(() => {
                            formContext.loading = false
                        })
                    }
                }
            })
        }
    })

    return {
        formContext,
        ...formValidate
    }

}