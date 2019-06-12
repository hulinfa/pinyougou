// 窗口加载完
$(function(){
    // 创建Vue对象
    var vue = new Vue({
        el : '#app', // 元素绑定
        data : { // 数据模型
            goods : {} // 数据封装对象(表单)
        },
        methods : { // 定义操作方法
            saveOrUpdate : function () { // 添加或修改
                // 发送异步请求
                axios.post("/goods/save", this.goods)
                    .then(function(response){
                    // 获取响应数据
                    if (response.data){ // 操作成功
                        // 清空表单数据
                        vue.goods = {};
                    }else {
                        alert('操作失败！');
                    }
                });
            }
        },
        created : function () { // 创建生命周期(初始化方法)

        }
    });
});