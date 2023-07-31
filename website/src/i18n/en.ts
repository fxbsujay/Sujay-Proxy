const modules = import.meta.globEager('./modules/en/*.ts' )

let moduleI18n: object = {}
Object.values(modules).forEach((mod ) => {
    const tmp = (mod as Record<string, any>).default || {}
    moduleI18n = {
        ...moduleI18n,
        ...tmp
    }
})

export default {
    account: 'account',
    password: 'password',
    login: 'Sign In',
    logout: 'Sign Out',
    checkCode: 'check code',
    phoneNumber: 'phone',
    email: 'email',
    verificationCode: 'code',
    pleaseEnter: 'Please Enter @:{key}',
    pleaseChoose: 'Please Select @:{key}',
    pleaseUpload: 'Please Upload @:{key}',
    operation: 'Operation',
    query: 'Query',
    add: 'Add',
    edit: 'Edit',
    delete: 'Delete',
    save: 'Save',
    submit: 'Submit',
    clear: 'Clear',
    insert: 'Insert',
    import: 'Import',
    export: 'Export',
    ok: 'Ok',
    close: 'Close',
    cancel: 'Cancel',
    update: 'Update',
    collapse: 'collapse',
    select: 'Select',
    expand: 'expand',
    preview: 'Preview',
    fullScreen: 'Full Screen',
    moreActions: 'More Actions',
    yes: 'Yes',
    no: 'No',
    name: 'name',
    remark: 'Remark',
    descr: 'Description',
    upload: 'Upload',
    download: 'Download',
    small: 'Small',
    middle: 'Middle',
    large: 'Large',
    ...moduleI18n
}