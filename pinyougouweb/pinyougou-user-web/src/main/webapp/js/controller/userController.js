// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            user: {},//用户
            password: '',//确认密码
            smsCode: ''  //短信验证码
        },
        methods: { // 操作方法
            save: function () {
                if (this.user.password != this.password) {
                    alert("密码不一致，请重新输入！");
                    return;
                }
                axios.post("/user/save?smsCode=" + this.smsCode, this.user).then(function (response) {
                    if (response.data) {
                        alert("注册成功！");
                        vue.user = {};
                        vue.password = "";
                        vue.smsCode = '';
                    } else {
                        alert("注册失败！");
                    }
                });
            },
            sendCode: function () {
                if (this.user.phone) {
                    axios.get("/user/sendCode?phone=" + this.user.phone).then(function (response) {
                        alert(response.data ? "发送成功！" : "发送失败!");
                    });
                } else {
                    alert("请输入手机号!");
                }
            }

        },
        created: function () { // 创建生命周期

        }
    });
};