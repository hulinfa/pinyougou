package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.AddressMapper;
import com.pinyougou.pojo.Address;
import com.pinyougou.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.AddressService")
@Transactional(rollbackFor = RuntimeException.class)
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public void save(Address address) {

    }

    @Override
    public void update(Address address) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Address findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Address> findAll() {
        return null;
    }

    @Override
    public List<Address> findByPage(Address address, int page, int rows) {
        return null;
    }

    @Override
    public List<Address> findAddressByUser(String userId) {
        try {
            Example example = new Example(Address.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("userId", userId);
            example.orderBy("isDefault").desc();
            return addressMapper.selectByExample(example);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
