// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            loginName: '',//登录用户名
            redirectUrl: '',//重定向用户名
            carts: [],   //购物车数组
            totalEntity: {totalNum: 0, totalMoney: 0.00},//总计对象
            addressList: [],  //收件地址
            address: {},      //选中的地址
            order: {paymentType: '1'}   //订单对象
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
            },
            ///查询用户收件地址
            findAddressByUser: function () {
                axios.get("/order/findAddressByUser").then(function (response) {
                    vue.addressList = response.data;
                    vue.address = response.data[0];
                });
            },
            selectAddress: function (item) {
                this.address = item;
            },
            isSelectedAddress: function (item) {
                return this.address == item;
            },
            // 保存订单
            saveOrder: function () {
                // 设置收件人地址
                this.order.receiverAreaName = this.address.address;
                // 设置收件人手机号码
                this.order.receiverMobile = this.address.mobile;
                // 设置收件人
                this.order.receiver = this.address.contact;
                // 设置订单来源(pc端)
                this.order.sourceType = 2;
                // 发送异步请求
                axios.post("/order/save", this.order)
                    .then(function (response) {
                        if (response.data) {
                            // 如果是微信支付，跳转到扫码支付页面
                            if (vue.order.paymentType == 1) {
                                location.href = "/order/pay.html";
                            } else {
                                // 如果是货到付款，跳转到成功页面
                                location.href = "/order/paysuccess.html";
                            }
                        } else {
                            alert("订单提交失败！");
                        }
                    });
            }
        },
        created: function () { // 创建生命周期
            this.loadUserName();
            this.findCart();
            this.findAddressByUser();
        }
    });
};