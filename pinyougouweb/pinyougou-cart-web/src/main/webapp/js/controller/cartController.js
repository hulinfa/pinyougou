// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            loginName: '',//登录用户名
            redirectUrl: '',//重定向用户名
            carts: [],   //购物车数组
            totalEntity: {totalNum: 0, totalMoney: 0.00}//总计对象
        },
        methods: { // 操作方法
            //加载用户
            loadUserName: function () {
                this.redirectUrl = window.encodeURIComponent(location.href);
                axios.get("/user/showName").then(function (response) {
                    vue.loginName = response.data.loginName;
                });
            },
            //查询购物车
            findCart: function () {
                axios.get("/cart/findCart").then(function (response) {
                    vue.carts = response.data;
                    vue.totalEntity = {totalNum: 0, totalMoney: 0.00};
                    for (var i = 0; i < response.data.length; i++) {
                        //获取购物车
                        var cart = response.data[i];
                        for (var j = 0; j < cart.orderItems.length; j++) {
                            var orderItem = cart.orderItems[j];
                            vue.totalEntity.totalNum += orderItem.num;
                            vue.totalEntity.totalMoney += orderItem.totalFee;
                        }
                    }
                });
            },
            //加入购物车
            addCart: function (itemId, num) {
                axios.get("/cart/addCart?itemId=" + itemId + "&num=" + num).then(function (response) {
                    if (response.data) {
                        vue.findCart();
                    } else {
                        alert("操作失败!");
                    }
                });
            }
        },
        created: function () { // 创建生命周期
            this.loadUserName();
            this.findCart();
        }
    });
};