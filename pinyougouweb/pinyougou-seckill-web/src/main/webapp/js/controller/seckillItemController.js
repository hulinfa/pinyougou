// 窗口加载完
$(function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            loginName: '', // 登录用户名
            redirectUrl: '', // 重定向URL
            entity: {},     //秒杀商品
            timeStr: ''      //时间字符串
        },
        methods: { // 操作方法
            // 加载用户
            loadUsername: function () {
                // 定义重定向URL
                this.redirectUrl = window.encodeURIComponent(location.href);
                // 获取登录用户名
                axios.get("/user/showName").then(function (response) {
                    vue.loginName = response.data.loginName;
                });
            },
            //根据秒杀商品id，查找商品参数。
            findOne: function () {
                var id = this.getUrlParam("id");
                axios.get("/seckill/findOne?id=" + id).then(function (response) {
                    vue.entity = response.data;
                    vue.downcount(vue.entity.endTime);
                });
            },
            //倒计时方法
            downcount: function (endTime) {
                //计算出相差的毫秒数
                var milliSeconds = endTime - (new Date()).getTime();
                //计算出相差的秒数
                var seconds = Math.floor(milliSeconds / 1000);
                //判断秒是否大于0
                if (seconds >= 0) {
                    //计算出分钟
                    var minutes = Math.floor(seconds / 60);
                    //计算出小时
                    var hours = Math.floor(minutes / 60);
                    //计算出天数
                    var days = Math.floor(hours / 24);
                    //resArr封装最后显示的时间
                    var resArr = new Array();
                    if (days > 0) {
                        resArr.push(this.calc(days) + "天");
                    }
                    if (hours > 0) {
                        resArr.push(this.calc(hours - days * 24) + ":");
                    }
                    if (minutes > 0) {
                        resArr.push(this.calc(minutes - hours * 60) + ":");
                    }
                    if (seconds > 0) {
                        resArr.push(this.calc(seconds - minutes * 60));
                    }

                    this.timeStr = resArr.join("");
                    //开启延迟定时器
                    setTimeout(function () {
                        vue.downcount(endTime);
                    }, 1000)

                } else {
                    this.timeStr = "秒杀结束！";
                }
            },
            //计算不够两位前面补0
            calc: function (num) {
                return num > 9 ? num : '0' + num;
            },
            submitOrder: function () {
                //判断是否登录
                if (this.loginName) {   //已登录
                    axios.get("/order/submitOrder?id=" + this.entity.id).then(function (response) {
                        if (response.data) {
                            location.href = "/order/pay.html";
                        } else {
                            alert("下单失败！");
                        }
                    });
                } else {    //未登录
                    //跳转到单点登录系统
                    location.href = "http://sso.pinyougou.com?service=" + this.redirectUrl;
                }
            }
        },
        created: function () { // 创建生命周期
            this.loadUsername();
            this.findOne();
        }
    });
});