package com.example.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.system.entity.Address;
import com.example.system.mapper.AddressMapper;
import com.example.system.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址服务实现
 */
@Slf4j
@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Override
    public List<Address> getAddressList(Long customerId) {
        return list(new LambdaQueryWrapper<Address>()
                .eq(Address::getCustomerId, customerId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreatedAt));
    }

    @Override
    public Address addAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            update(new LambdaUpdateWrapper<Address>()
                    .eq(Address::getCustomerId, address.getCustomerId())
                    .set(Address::getIsDefault, false));
        }
        save(address);
        return address;
    }

    @Override
    public boolean updateAddress(Address address) {
        // 如果设置为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            update(new LambdaUpdateWrapper<Address>()
                    .eq(Address::getCustomerId, address.getCustomerId())
                    .set(Address::getIsDefault, false));
        }
        return updateById(address);
    }

    @Override
    public boolean deleteAddress(Long id, Long customerId) {
        return remove(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, id)
                .eq(Address::getCustomerId, customerId));
    }

    @Override
    public boolean setDefault(Long id, Long customerId) {
        // 先取消其他默认地址
        update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getCustomerId, customerId)
                .set(Address::getIsDefault, false));
        
        // 设置新的默认地址
        return update(new LambdaUpdateWrapper<Address>()
                .eq(Address::getId, id)
                .eq(Address::getCustomerId, customerId)
                .set(Address::getIsDefault, true));
    }

    @Override
    public Address getDefaultAddress(Long customerId) {
        return getOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getCustomerId, customerId)
                .eq(Address::getIsDefault, true));
    }
}
