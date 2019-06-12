window.onload = function (ev) {
    var vue = new Vue({
        el: '#app',
        data: {
            loginName: ''
        },
        methods: {
            findLoginName: function () {
                axios.get("/user/findLoginName").then(function (response) {
                    vue.loginName = response.data.loginName;
                });
            }
        },
        created: function () {
            this.findLoginName();
        }
    });
}