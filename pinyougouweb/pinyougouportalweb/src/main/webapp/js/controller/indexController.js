// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            contentList: []     //打广告数组
        },
        methods: { // 操作方法
            findContentByCategoryId: function (categoryId) {
                axios.get("/content/findContentByCategoryId?categoryId=" + categoryId).then(function (response) {
                    vue.contentList = response.data;
                });
            }
        },
        created: function () { // 创建生命周期
            this.findContentByCategoryId(1);
        }
    });
};