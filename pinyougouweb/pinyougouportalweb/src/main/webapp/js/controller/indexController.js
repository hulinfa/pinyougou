// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            contentList: [],    //打广告数组
            keywords: '',     //搜索关键字
            redirectUrl: '',   //重定向url
            loginName: ''    //登录用户名
        },
        methods: { // 操作方法
            findContentByCategoryId: function (categoryId) {
                axios.get("/content/findContentByCategoryId?categoryId=" + categoryId).then(function (response) {
                    vue.contentList = response.data;
                });
            },
            search: function () {
                location.href = "http://search.pinyougou.com?keywords=" + this.keywords;
            },
            //获取登录用户名
            loadUsername: function () {
                //定义重定向url
                this.redirectUrl = window.encodeURIComponent(location.href);
                //获取登录用户名
                axios.get("/user/showName").then(function (response) {
                    vue.loginName = response.data.loginName;
                });
            }
        },
        created: function () { // 创建生命周期
            this.findContentByCategoryId(1);
            this.loadUsername();
        }
    });
};