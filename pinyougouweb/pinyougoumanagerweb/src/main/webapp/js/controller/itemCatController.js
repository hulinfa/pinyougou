// 窗口加载完
window.onload = function () {
    // 创建Vue对象
    var vue = new Vue({
            el: '#app', // 元素绑定
            data: { // 数据模型
                dataList: [], // 定义数组，接收后台响应数据
                entity: {typeId: 0}, // 数据封装对象(表单)
                page: 1, // 当前页码
                pages: 0, // 总页数
                searchEntity: {}, // 搜索条件数据封装
                ids: [], // 复选框选中的id数组
                checked: false, // 全选复选框是否选中
                itemCat1: {},   //一级商品分类
                itemCat2: {},   //二级商品分类
                grade: 1,    //分类级别
                typeTemplateList: [],//类型模板列表
                parentId: 0     //定义父级id
            },
            methods: { // 定义操作方法
                findItemCatByParentId: function (parentId) {
                    axios.get("/itemCat/findItemCatByParentId?parentId=" + parentId).then(function (response) {
                        vue.dataList = response.data;
                        vue.parentId = parentId;
                        vue.ids = [];
                    });
                },
                saveOrUpdate: function () { // 添加或修改
                    var url = "save"; // 添加
                    if (this.entity.id) {
                        url = "update"; // 修改
                    } else { // 保存分类时需要知道父级的id
                        this.entity.parentId = this.parentId;
                    }
                    // 发送异步请求
                    axios.post("/itemCat/" + url, this.entity)
                        .then(function (response) {
                            // 获取响应数据
                            if (response.data) { // 操作成功
                                vue.entity = {typeId: vue.typeTemplateList[0].id};
                                // 重新加载数据
                                vue.findItemCatByParentId(vue.parentId);
                            } else {
                                alert('操作失败！');
                            }
                        });
                },
                show: function (entity) { // 数据回显
                    // 把entity对象转化成json字符串
                    var jsonStr = JSON.stringify(entity);
                    // 把json字符串转化成一个新的json对象
                    this.entity = JSON.parse(jsonStr);
                }
                ,
                checkAll: function (e) { // 全选复选框
                    this.ids = []; // 先清空数组
                    if (e.target.checked) { // 判断复选框是否选中
                        for (var i = 0; i < this.dataList.length; i++) {
                            this.ids.push(this.dataList[i].id);
                        }
                    }
                }
                ,
                del: function () { // 删除
                    if (this.ids.length > 0) {
                        axios.get("/itemCat/delete?ids="
                            + this.ids).then(function (response) {
                            if (response.data) {
                                vue.findItemCatByParentId(vue.parentId);
                            } else {
                                alert("删除失败！");
                            }
                        });
                    } else {
                        alert("请选择要删除的记录！");
                    }
                },
                selectList: function (entity, grade) {
                    this.grade = grade;
                    if (grade == 1) {
                        this.itemCat1 = {};
                        this.itemCat2 = {};
                    } else if (grade == 2) {
                        this.itemCat1 = entity;
                        this.itemCat2 = {};
                    } else if (grade == 3) {
                        this.itemCat2 = entity;
                    }
                    this.findItemCatByParentId(entity.id);
                },
                findTypeTemplateList: function () {
                    axios.get("/typeTemplate/findTypeTemplateList").then(function (response) {
                        vue.typeTemplateList = response.data;
                        vue.entity.typeId = vue.typeTemplateList[0].id;
                    });
                }
            },
            created: function () { // 创建生命周期(初始化方法)
                this.findItemCatByParentId(0);
                this.findTypeTemplateList();
            },
            updated: function () { // 更新数据生命周期
                // 检查全选checkbox是否选中
                this.checked = (this.ids.length == this.dataList.length);
            }
        })
    ;
};