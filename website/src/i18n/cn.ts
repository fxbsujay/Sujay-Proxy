const modules = import.meta.globEager('./modules/cn/*.ts' )

let moduleI18n: object = {}
Object.values(modules).forEach((mod ) => {
    const tmp = (mod as Record<string, any>).default || {}
    moduleI18n = {
        ...moduleI18n,
        ...tmp
    }
})

export default {
    account: '账户',
    password: '密码',
    login: '登录',
    logout: '退出',
    checkCode: '校验码',
    phoneNumber: '手机号',
    email: '邮箱',
    verificationCode: '验证码',
    pleaseEnter: '请填写@:{key}',
    pleaseChoose: '请选择@:{key}',
    pleaseUpload: '请上传@:{key}',
    operation: '操作',
    query: '查询',
    add: '新建',
    edit: '编辑',
    delete: '删除',
    save: '保存',
    submit: '提交',
    clear: '清空',
    insert: '插入',
    import: '导入',
    export: '导出',
    ok: '确定',
    close: '关闭',
    cancel: '取消',
    update: '更新',
    collapse: '收起',
    select: '选择',
    expand: '展开',
    preview: '预览',
    fullScreen: '全屏',
    moreActions: '更多操作',
    yes: '是',
    no: '否',
    name: '姓名',
    remark: '备注',
    descr: '描述',
    upload: '上传',
    download: '下载',
    small: '迷 你',
    middle: '中 等',
    large: '偏 大',
    ...moduleI18n
}