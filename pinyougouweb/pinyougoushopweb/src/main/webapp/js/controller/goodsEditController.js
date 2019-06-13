// 窗口加载完
$(function () {
    // 创建Vue对象
    var vue = new Vue({
            el: '#app', // 元素绑定
            data: { // 数据模型
                goods: {
                    goodsDesc: {itemImages: [], customAttributeItems: [], specificationItems: []},
                    category1Id: '',
                    category2Id: '',
                    category3Id: '',
                    typeTemplateId: '',
                    brandId: '',
                    items: [],       //数据封装对象(表单)
                    isEnableSpec: 0
                }, // 数据封装对象(表单)
                picEntity: {url: '', color: ''},  //上传图片
                itemCatList1: [],//一级商品分类
                itemCatList2: [],//二级商品分类
                itemCatList3: [],//三级商品分类
                brandList: [],    //品牌数组
                specList: []   //规格数组
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
                },
                selectSpecAttr: function (e, specName, optionName) {
                    var obj = this.searchJsonByKey(this.goods.goodsDesc.specificationItems, 'attributeName', specName);
                    if (obj) {
                        if (e.target.checked) {
                            obj.attributeValue.push(optionName);
                        } else {
                            obj.attributeValue.splice(obj.attributeValue.indexOf(optionName), 1);
                            if (obj.attributeValue.length == 0) {
                                var specificationItems = this.goods.goodsDesc.specificationItems;
                                specificationItems.splice(specificationItems.indexOf(obj), 1);
                            }
                        }
                    } else {
                        this.goods.goodsDesc.specificationItems.push(
                            {"attributeName": specName, "attributeValue": [optionName]})
                    }
                },
                searchJsonByKey: function (jsonArr, key, value) {
                    for (var i = 0; i < jsonArr.length; i++) {
                        if (jsonArr[i][key] == value) {
                            return jsonArr[i];
                        }
                    }
                },
                createItems: function () {
                    this.goods.items = [{
                        spec: {}, price: 0, num: 9999,
                        status: '0', isDefault: '0'
                    }];

                    var specItems = this.goods.goodsDesc.specificationItems;

                    for (var i = 0; i < specItems.length; i++) {
                        this.goods.items = this.swapItems(this.goods.items,
                            specItems[i].attributeName, specItems[i].attributeValue);
                    }
                },
                swapItems: function (items, attributeName, attributeValue) {
                    //创建新的sku数组
                    var newItems = new Array();
                    for (var i = 0; i < items.length; i++) {
                        var item = items[i];
                        for (var j = 0; j < attributeValue.length; j++) {
                            var newItem = JSON.parse(JSON.stringify(item));
                            newItem.spec[attributeName] = attributeValue[j];
                            newItems.push(newItem);
                        }
                    }
                    return newItems;
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
                        axios.get("/typeTemplate/findSpecByTemplateId?id=" + newVal)
                            .then(function (response) {
                                vue.specList = response.data;
                            });
                    } else {
                        vue.brandList = [];
                        vue.goods.goodsDesc.customAttributeItems = [];
                        vue.specList = [];
                    }
                }
            }
        })
    ;
});