package com.example.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.cart.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<CartItem> {
}
