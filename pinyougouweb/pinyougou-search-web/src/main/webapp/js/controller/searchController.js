// 窗口加载完
window.onload = function () {
    var vue = new Vue({
        el: '#app', // 元素绑定
        data: { // 数据模型,注意:只有在vue初始化存在的属性才能是响应的
            searchParam: {
                keywords: '',
                category: '',
                brand: '',
                spec: {},
                price: '',
                page: 1,
                sortField: '',
                sortValue: ''
            },//搜索参数
            resultMap: {},   //搜索结果
            pageNums: [],    //页码数组
            keywords: '',    //搜索关键字
            jumpPage: 1,     //跳转页码
            firstDot: true,   //前面加点
            lastDot: true,      //后面加点
            redirectUrl:'',   //重定向url
            loginName:''      //登录用户名
        },
        methods: { // 操作方法
            search: function () {
                axios.post("/search", this.searchParam).then(function (response) {
                    vue.keywords = vue.searchParam.keywords;
                    vue.resultMap = response.data;
                    vue.initPageNum();
                });
            },
            addSearchItem: function (key, value) {
                if (key == "brand" || key == "category" || key == "price") {
                    this.searchParam[key] = value;
                } else {
                    // this.searchParam.spec[key] = value;
                    Vue.set(vue.searchParam.spec, key, value);
                }
                this.search();
            },
            removeSearchItem: function (key) {
                if (key == "brand" || key == "category" || key == "price") {
                    this.searchParam[key] = "";
                } else {
                    // delete this.searchParam.spec[key];
                    Vue.delete(this.searchParam.spec, key);
                }
                this.search();
            },
            initPageNum: function () {
                this.pageNums = [];
                var totalPages = this.resultMap.totalPages;
                var firstPage = 1;
                var lastPage = totalPages;
                this.firstDot = true;
                this.lastDot = true;
                if (totalPages > 5) {
                    if (this.searchParam.page < 3) {
                        lastPage = 5;   //生成前五页
                        this.firstDot = false;
                    } else if (this.searchParam.page >= totalPages - 3) {
                        firstPage = totalPages - 4; //生成后5页
                        this.lastDot = false;
                    } else {
                        firstPage = this.searchParam.page - 2;
                        lastPage = this.searchParam.page + 2;
                    }
                } else {
                    this.firstDot = false;
                    this.lastDot = false;
                }

                for (var i = firstPage; i <= lastPage; i++) {
                    this.pageNums.push(i);
                }
            },
            pageSearch: function (page) {
                page = parseInt(page);
                if (page >= 1 && page <= this.resultMap.totalPages && page != this.searchParam.page) {
                    this.searchParam.page = page;
                    this.jumpPage = page;
                    this.search();
                }
            },
            sortSearch: function (sortField, sortValue) {
                this.searchParam.sortField = sortField;
                this.searchParam.sortValue = sortValue;
                this.search();
            },
            initSearch: function () {
                var keywords = this.getUrlParam("keywords");
                this.searchParam.keywords = keywords ? keywords : '';
                this.search();
            },
            // 获取登录用户名
            loadUsername : function () {
                // 定义重定向URL
                this.redirectUrl = window.encodeURIComponent(location.href);
                // 获取登录用户名
                axios.get("/user/showName").then(function(response){
                    vue.loginName = response.data.loginName;
                });
            }
        },
        created: function () { // 创建生命周期
            this.initSearch();
            this.loadUsername();
        }
    });
};