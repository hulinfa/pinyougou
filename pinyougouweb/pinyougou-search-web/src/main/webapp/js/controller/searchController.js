// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            searchParam: {keywords: ''},//搜索参数
            resultMap: {}
        },
        methods: { // 操作方法
            search: function () {
                axios.post("/search", this.searchParam).then(function (response) {
                    vue.resultMap = response.data;
                });
            }
        },
        created: function () { // 创建生命周期
            this.search();
        }
    });
};