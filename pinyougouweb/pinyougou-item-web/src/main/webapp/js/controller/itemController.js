// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型
            num: 1, //购买数量
            spec: {}, //规格选项
            sku: {}    //sku商品
        },
        methods: { // 操作方法
            addNum: function (x) {
                this.num = parseInt(this.num);
                var num = this.num += x;
                this.num = num < 1 ? 1 : num;
            },
            isSelected: function (name, value) {
                return this.spec[name] == value;
            },
            selectSpec: function (name, value) {
                Vue.set(this.spec, name, value);
                this.searchSku();
            },
            loadSku: function () {
                this.sku = itemList[0];
                this.spec = JSON.parse(this.sku.spec);
            },
            searchSku: function () {
                for (var i = 0; i < itemList.length; i++) {
                    if (itemList[i].spec == JSON.stringify(this.spec)) {
                        this.sku = itemList[i];
                        return;
                    }
                }
            },
            addToCart: function () {
                alert('sku商品id:' + this.sku.id + ", 购买数量：" + this.num);
            }

        },
        created: function () { // 创建生命周期
            this.loadSku();
        }
    });
};