// 窗口加载完
$(function () {
    // 创建Vue对象
    var vue = new Vue({
            el: '#app', // 元素绑定
            data: { // 数据模型
                goods: {
                    goodsDesc: {itemImages: [], customAttributeItems: []},
                    category1Id: '',
                    category2Id: '',
                    category3Id: '',
                    typeTemplateId: '',
                    brandId: ''
                }, // 数据封装对象(表单)
                picEntity: {url: '', color: ''},  //上传图片
                itemCatList1: [],//一级商品分类
                itemCatList2: [],//二级商品分类
                itemCatList3: [],//三级商品分类
                brandList: []
            },
            methods: { // 定义操作方法
                saveOrUpdate: function () { // 添加或修改
                    this.goods.goodsDesc.introduction = editor.html();
                    // 发送异步请求
                    axios.post("/goods/save", this.goods)
                        .then(function (response) {
                            // 获取响应数据
                            if (response.data) { // 操作成功
                                // 清空表单数据
                                vue.goods = {goodsDesc: {}};
                                //清空富文本编辑器
                                editor.html('');
                            } else {
                                alert('操作失败！');
                            }
                        });
                },
                uploadFile: function () {//文件上传
                    var formData = new FormData();
                    //file可以获取到页面上所有的文件对象
                    formData.append('file', file.files[0]);
                    axios({
                        method: 'post',
                        url: '/upload',
                        data: formData,
                        headers: {
                            'Content-Type': 'multipart/form-data'
                        }
                    }).then(function (response) {
                        if (response.data.status == 200) {
                            vue.picEntity.url = response.data.url;
                        } else {
                            alert("上传失败!");
                        }
                    });

                },
                addPic: function () {
                    this.goods.goodsDesc.itemImages.push(this.picEntity);
                },
                removePic: function (index) {
                    var item = this.goods.goodsDesc.itemImages;
                    axios.get("/imgs/delete?url=" + item[index].url).then(function (response) {
                        if (response.data) {
                            vue.goods.goodsDesc.itemImages.splice(index, 1);
                        } else {
                            alert("操作失败！");
                        }
                    });
                },
                findItemCatByParentId: function (parentId, name) {
                    axios.get("/itemCat/findItemCatByParentId?parentId=" + parentId).then(function (response) {
                        vue[name] = response.data;
                    });
                }
            },
            created: function () { // 创建生命周期(初始化方法)
                this.findItemCatByParentId(0, "itemCatList1");
            },
            watch: {
                'goods.category1Id': function (newValue, oldValue) {
                    this.goods.category2Id = '';
                    if (newValue) {
                        this.findItemCatByParentId(newValue, 'itemCatList2');
                    } else {
                        this.itemCatList2 = [];
                    }
                },
                'goods.category2Id': function (newValue, oldValue) {
                    this.goods.category3Id = '';
                    if (newValue) {
                        this.findItemCatByParentId(newValue, 'itemCatList3');
                    } else {
                        this.itemCatList3 = [];
                    }
                },
                'goods.category3Id': function (newValue, oldValue) {
                    this.goods.typeTemplateId = "";
                    if (newValue) {
                        var itemCat = this.itemCatList3.find(function (item) {
                            return item.id = newValue;
                        });
                        if (itemCat) {
                            this.goods.typeTemplateId = itemCat.typeId;
                        }
                    }
                },
                'goods.typeTemplateId': function (newVal, oldVal) {
                    this.goods.brandList = '';
                    if (newVal) {
                        axios.get("/typeTemplate/findOne?id=" + newVal).then(function (response) {
                            vue.brandList = JSON.parse(response.data.brandIds);
                            vue.goods.goodsDesc.customAttributeItems = JSON.parse(response.data.customAttributeItems);
                        });
                    }else{
                        vue.brandList = [];
                        vue.goods.goodsDesc.customAttributeItems = [];
                    }
                }
            }
        })
    ;
});