package com.bookstore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Address;

import java.util.List;

public interface AddressService extends IService<Address> {

    List<Address> listByUserId(Long userId);

    void addAddress(Address address);

    void updateAddress(Address address);

    void deleteAddress(Long id);

    void setDefault(Long id);
}
