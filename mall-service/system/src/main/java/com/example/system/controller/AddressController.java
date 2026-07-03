package com.example.system.controller;

import com.example.common.result.Result;
import com.example.system.entity.Address;
import com.example.system.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收货地址控制器
 */
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 获取用户地址列表
     */
    @GetMapping("/list")
    public Result<List<Address>> getAddressList(@RequestParam Long customerId) {
        List<Address> list = addressService.getAddressList(customerId);
        return Result.success(list);
    }

    /**
     * 获取地址详情
     */
    @GetMapping("/{id}")
    public Result<Address> getAddress(@PathVariable Long id) {
        Address address = addressService.getById(id);
        return Result.success(address);
    }

    /**
     * 添加地址
     */
    @PostMapping("/add")
    public Result<Address> addAddress(@RequestBody Address address) {
        Address result = addressService.addAddress(address);
        return Result.success(result);
    }

    /**
     * 更新地址
     */
    @PutMapping("/update")
    public Result<Void> updateAddress(@RequestBody Address address) {
        boolean success = addressService.updateAddress(address);
        return success ? Result.success() : Result.error("更新失败");
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(
            @PathVariable Long id,
            @RequestParam Long customerId) {
        boolean success = addressService.deleteAddress(id, customerId);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/default/{id}")
    public Result<Void> setDefault(
            @PathVariable Long id,
            @RequestParam Long customerId) {
        boolean success = addressService.setDefault(id, customerId);
        return success ? Result.success() : Result.error("设置失败");
    }

    /**
     * 获取默认地址
     */
    @GetMapping("/default")
    public Result<Address> getDefaultAddress(@RequestParam Long customerId) {
        Address address = addressService.getDefaultAddress(customerId);
        return Result.success(address);
    }
}
