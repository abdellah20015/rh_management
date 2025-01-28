export default {

    login : 'login',
    checkauth : '/private/user/checkauth',
    logout: '/private/logout',
    user:{
        list:'/private/user/list',
        create:'/private/user/create',
        update:'/private/user/update',
        delete:'/private/user/delete',
        profile:'/private/user/profile',
        resetPassword:'/private/reset_password',
        import_users : '/private/user/import',
        manager: '/private/user/manager' ,
        stats : '/private/user/stats',
    },
    demand:{
        listByManager:'/private/demand/manager/list',
        listByUser:'/private/demand/user/list',
        update:'/private/demand/update',
        create:'/private/demand/create',
        stats : '/private/demand/stats'
    },
    notification:{
        list: 'private/notification',
        updateStatus : 'private/notification/update/status'
    },
    file:{
        list :'/private/file/get',
        download:'/private/file/download',
        upload : '/private/file/upload'
    },
    contract:{
        get : '/private/contract/get',
        create:'/private/contract/create',
        update:'/private/contract/update'
    },
}
