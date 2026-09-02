package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Address;
import com.bookstore.mapper.AddressMapper;
import com.bookstore.service.AddressService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> listByUserId(Long userId) {
        return list(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getUpdateTime));
    }

    @Override
    public void addAddress(Address address) {
        // 如果是默认地址, 先取消其他默认
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            cancelOtherDefault(address.getUserId());
        }
        save(address);
    }

    @Override
    public void updateAddress(Address address) {
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            cancelOtherDefault(address.getUserId());
        }
        updateById(address);
    }

    @Override
    public void deleteAddress(Long id) {
        removeById(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        Address address = getById(id);
        if (address == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "地址不存在");
        }
        // 取消其他默认
        cancelOtherDefault(address.getUserId());
        // 设置当前为默认
        Address update = new Address();
        update.setId(id);
        update.setIsDefault(1);
        updateById(update);
    }

    private void cancelOtherDefault(Long userId) {
        update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1)
                .set(Address::getIsDefault, 0));
    }
}
