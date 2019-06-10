//监听文档加载完
window.onload = function (ev) {
    console.log("开始加载数据...");
    var vue = new Vue({
        el: '#app',
        data: {
            dataList: [],   //品牌数组
            entity: {},      //封装表单数据
            pages: 0,      //总页数
            page: 1,          //当前页码
            searchEntity: {},    //封装查询条件
            ids: [],          //封装删除的品牌的id
            checked: false      //全选是否选中
        },
        methods: {
            search: function (page) {
                this.page = page;
                axios.get("/brand/findByPage?page=" + page, {params: this.searchEntity}).then(function (response) {
                    var pageResult = response.data;
                    vue.dataList = pageResult.rows;
                    vue.pages = pageResult.pages;
                    vue.page = page;
                    vue.ids = [];
                })
            },
            saveOrUpdate: function () {
                var url = "save";
                if (this.entity.id) {
                    url = "update";
                }
                axios.post("/brand/" + url, this.entity).then(function (response) {
                    var data = response;
                    if (data) {
                        vue.search(vue.page);
                    } else {
                        alert("操作失败...");
                    }
                });
            },
            show: function (entity) {
                var jsonStr = JSON.stringify(entity);
                this.entity = JSON.parse(jsonStr);
            },
            checkAll: function (e) {
                this.ids = [];
                if (e.target.checked) {
                    for (var i = 0; i < this.dataList.length; i++) {
                        this.ids.push(this.dataList[i].id);
                    }
                }
            },
            del: function () {
                if (this.ids.length > 0) {
                    axios.get("/brand/delete?ids=" + this.ids).then(function (response) {
                        if (response.data) {
                            vue.search(vue.page);
                        } else {
                            alert("操作失败!");
                        }
                    });
                } else {
                    alert("请选择要删除的品牌")
                }
            },
            closeDialog: function () {
                vue.entity = {};
            }
        },
        created: function () {
            this.search(1);
        },
        updated: function () {
            var length = this.dataList.length;
            if (length > 0) {
                this.checked = length == this.ids.length;
            }
        }
    });
}