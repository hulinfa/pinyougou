package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Seller;

import java.util.List;

/**
 * SellerMapper 数据访问接口
 * @date 2019-06-08 20:18:17
 * @version 1.0
 */
public interface SellerMapper extends Mapper<Seller>{

    List<Seller> findAll(Seller seller);
}