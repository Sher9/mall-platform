package com.example.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.system.entity.Address;

import java.util.List;

/**
 * 收货地址服务接口
 */
public interface AddressService extends IService<Address> {

    /**
     * 获取用户地址列表
     */
    List<Address> getAddressList(Long customerId);

    /**
     * 添加地址
     */
    Address addAddress(Address address);

    /**
     * 更新地址
     */
    boolean updateAddress(Address address);

    /**
     * 删除地址
     */
    boolean deleteAddress(Long id, Long customerId);

    /**
     * 设置默认地址
     */
    boolean setDefault(Long id, Long customerId);

    /**
     * 获取默认地址
     */
    Address getDefaultAddress(Long customerId);
}
