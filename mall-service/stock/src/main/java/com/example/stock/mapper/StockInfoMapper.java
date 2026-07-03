package com.example.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.stock.entity.StockInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockInfoMapper extends BaseMapper<StockInfo> {

    @Update("UPDATE stock_info SET locked_count = locked_count + #{count}, available_count = available_count - #{count} WHERE product_id = #{productId} AND available_count >= #{count}")
    int lockStock(@Param("productId") Long productId, @Param("count") Integer count);

    @Update("UPDATE stock_info SET locked_count = locked_count - #{count}, available_count = available_count + #{count} WHERE product_id = #{productId} AND locked_count >= #{count}")
    int unlockStock(@Param("productId") Long productId, @Param("count") Integer count);

    @Update("UPDATE stock_info SET stock_count = stock_count + #{count}, available_count = available_count + #{count} WHERE product_id = #{productId}")
    int increaseStock(@Param("productId") Long productId, @Param("count") Integer count);

    @Update("UPDATE stock_info SET stock_count = stock_count - #{count}, locked_count = locked_count - #{count} WHERE product_id = #{productId} AND locked_count >= #{count}")
    int deductStock(@Param("productId") Long productId, @Param("count") Integer count);
}
