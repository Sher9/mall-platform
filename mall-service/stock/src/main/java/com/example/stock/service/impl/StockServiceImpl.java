package com.example.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.stock.entity.StockInfo;
import com.example.stock.entity.StockLog;
import com.example.stock.mapper.StockInfoMapper;
import com.example.stock.mapper.StockLogMapper;
import com.example.stock.service.StockService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockServiceImpl implements StockService {

    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockInfoMapper stockInfoMapper;

    @Autowired
    private StockLogMapper stockLogMapper;

    @Autowired(required = false)
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public IPage<StockInfo> page(Page<StockInfo> page, String productName) {
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        if (productName != null && !productName.isEmpty()) {
            wrapper.like(StockInfo::getProductName, productName);
        }
        return stockInfoMapper.selectPage(page, wrapper);
    }

    @Override
    public StockInfo getById(Long stockId) {
        return stockInfoMapper.selectById(stockId);
    }

    @Override
    public StockInfo getByProductId(Long productId) {
        LambdaQueryWrapper<StockInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StockInfo::getProductId, productId);
        return stockInfoMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public StockInfo save(StockInfo stock) {
        stock.setStockCount(0);
        stock.setLockedCount(0);
        stock.setAvailableCount(0);
        stock.setStatus(0);
        stock.setCreateTime(LocalDateTime.now());
        stock.setUpdateTime(LocalDateTime.now());
        stockInfoMapper.insert(stock);
        log.info("库存创建成功, stockId={}", stock.getStockId());
        return stock;
    }

    @Override
    @Transactional
    public StockInfo update(StockInfo stock) {
        stock.setUpdateTime(LocalDateTime.now());
        stockInfoMapper.updateById(stock);
        return stock;
    }

    @Override
    @Transactional
    public void delete(Long stockId) {
        stockInfoMapper.deleteById(stockId);
    }

    @Override
    public List<StockInfo> list() {
        return stockInfoMapper.selectList(null);
    }

    @Override
    public List<StockInfo> listByIds(List<Long> ids) {
        return stockInfoMapper.selectBatchIds(ids);
    }

    @Override
    @Transactional
    public boolean lockStock(Long productId, Integer count, String orderNo) {
        log.info("锁定库存, productId={}, count={}", productId, count);
        StockInfo stock = getByProductId(productId);
        if (stock == null || stock.getAvailableCount() < count) {
            log.warn("库存不足, productId={}", productId);
            return false;
        }
        int before = stock.getAvailableCount();
        if (stockInfoMapper.lockStock(productId, count) > 0) {
            saveStockLog(productId, stock.getProductName(), 2, count, before, before - count, orderNo, "订单锁定");
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("stock-topic", "库存锁定: productId=" + productId);
            }
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean unlockStock(Long productId, Integer count) {
        StockInfo stock = getByProductId(productId);
        if (stock == null || stock.getLockedCount() < count) return false;
        int before = stock.getLockedCount();
        if (stockInfoMapper.unlockStock(productId, count) > 0) {
            saveStockLog(productId, stock.getProductName(), 3, count, before, before - count, null, "订单取消解锁");
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean increaseStock(Long productId, Integer count, String operator, String remark) {
        StockInfo stock = getByProductId(productId);
        if (stock == null) return false;
        int before = stock.getStockCount();
        if (stockInfoMapper.increaseStock(productId, count) > 0) {
            saveStockLog(productId, stock.getProductName(), 0, count, before, before + count, null, remark);
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("stock-topic", "入库: productId=" + productId + ", count=" + count);
            }
            log.info("入库成功, productId={}, count={}", productId, count);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deductStock(Long productId, Integer count, String orderNo) {
        StockInfo stock = getByProductId(productId);
        if (stock == null || stock.getLockedCount() < count) return false;
        int before = stock.getStockCount();
        if (stockInfoMapper.deductStock(productId, count) > 0) {
            saveStockLog(productId, stock.getProductName(), 1, count, before, before - count, orderNo, "订单出库");
            if (rocketMQTemplate != null) {
                rocketMQTemplate.convertAndSend("stock-topic", "出库: productId=" + productId + ", count=" + count);
            }
            log.info("出库成功, productId={}, count={}", productId, count);
            return true;
        }
        return false;
    }

    @Override
    public List<StockLog> getStockLogs(Long productId) {
        LambdaQueryWrapper<StockLog> wrapper = new LambdaQueryWrapper<>();
        if (productId != null) {
            wrapper.eq(StockLog::getProductId, productId);
        }
        wrapper.orderByDesc(StockLog::getCreateTime);
        return stockLogMapper.selectList(wrapper);
    }

    private void saveStockLog(Long productId, String productName, Integer changeType,
                              Integer count, Integer before, Integer after, String orderNo, String remark) {
        StockLog logEntry = new StockLog();
        logEntry.setProductId(productId);
        logEntry.setProductName(productName);
        logEntry.setChangeType(changeType);
        logEntry.setChangeCount(count);
        logEntry.setBeforeCount(before);
        logEntry.setAfterCount(after);
        logEntry.setOrderNo(orderNo);
        logEntry.setRemark(remark);
        logEntry.setCreateTime(LocalDateTime.now());
        stockLogMapper.insert(logEntry);
    }
}
