package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.entity.Address;
import com.bookstore.security.UserContext;
import com.bookstore.service.AddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@Api(tags = "收货地址接口")
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @ApiOperation("查询当前用户所有地址")
    @GetMapping
    public Result<List<Address>> list() {
        return Result.success(addressService.listByUserId(UserContext.getCurrentUserId()));
    }

    @ApiOperation("新增地址")
    @PostMapping
    public Result<Void> add(@RequestBody Address address) {
        address.setUserId(UserContext.getCurrentUserId());
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        addressService.addAddress(address);
        return Result.success();
    }

    @ApiOperation("修改地址")
    @PutMapping
    public Result<Void> update(@RequestBody Address address) {
        address.setUserId(UserContext.getCurrentUserId());
        addressService.updateAddress(address);
        return Result.success();
    }

    @ApiOperation("删除地址")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return Result.success();
    }

    @ApiOperation("设为默认地址")
    @PutMapping("/{id}/default")
    public Result<Void> setDefault(@PathVariable Long id) {
        addressService.setDefault(id);
        return Result.success();
    }
}
