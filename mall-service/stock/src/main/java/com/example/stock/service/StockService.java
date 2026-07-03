package com.example.stock.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.stock.entity.StockInfo;
import com.example.stock.entity.StockLog;

import java.util.List;

public interface StockService {
    IPage<StockInfo> page(Page<StockInfo> page, String productName);
    StockInfo getById(Long stockId);
    StockInfo getByProductId(Long productId);
    StockInfo save(StockInfo stock);
    StockInfo update(StockInfo stock);
    void delete(Long stockId);
    List<StockInfo> list();
    List<StockInfo> listByIds(List<Long> ids);
    boolean lockStock(Long productId, Integer count, String orderNo);
    boolean unlockStock(Long productId, Integer count);
    boolean increaseStock(Long productId, Integer count, String operator, String remark);
    boolean deductStock(Long productId, Integer count, String orderNo);
    List<StockLog> getStockLogs(Long productId);
}
