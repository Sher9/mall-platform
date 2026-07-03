package com.example.stock.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.stock.entity.StockInfo;
import com.example.stock.entity.StockLog;
import com.example.stock.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stock")
public class StockController {

    private static final Logger log = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockService stockService;

    @GetMapping
    @SentinelResource(value = "stockList", blockHandler = "blockHandler")
    public Result<IPage<StockInfo>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String productName) {
        Page<StockInfo> page = new Page<>(pageNum, pageSize);
        IPage<StockInfo> result = stockService.page(page, productName);
        return Result.success(result);
    }

    @GetMapping("/{stockId}")
    public Result<StockInfo> getById(@PathVariable Long stockId) {
        return Result.success(stockService.getById(stockId));
    }

    @GetMapping("/product/{productId}")
    public Result<StockInfo> getByProductId(@PathVariable Long productId) {
        return Result.success(stockService.getByProductId(productId));
    }

    @PostMapping
    public Result<StockInfo> save(@RequestBody StockInfo stock) {
        StockInfo saved = stockService.save(stock);
        return Result.success("库存创建成功", saved);
    }

    @PutMapping
    public Result<StockInfo> update(@RequestBody StockInfo stock) {
        StockInfo updated = stockService.update(stock);
        return Result.success("库存更新成功", updated);
    }

    @DeleteMapping("/{stockId}")
    public Result<Void> delete(@PathVariable Long stockId) {
        stockService.delete(stockId);
        return Result.success("库存删除成功", null);
    }

    @GetMapping("/all")
    public Result<List<StockInfo>> list() {
        return Result.success(stockService.list());
    }

    @PostMapping("/increase")
    public Result<Boolean> increase(@RequestBody Map<String, Object> params) {
        Long productId = Long.parseLong(params.get("productId").toString());
        Integer count = Integer.parseInt(params.get("count").toString());
        String operator = (String) params.getOrDefault("operator", "system");
        String remark = (String) params.getOrDefault("remark", "入库");
        log.info("入库请求, productId={}, count={}", productId, count);
        boolean result = stockService.increaseStock(productId, count, operator, remark);
        return result ? Result.success("入库成功", true) : Result.error("入库失败");
    }

    @GetMapping("/logs")
    public Result<List<StockLog>> getLogs(@RequestParam(required = false) Long productId) {
        return Result.success(stockService.getStockLogs(productId));
    }

    /**
     * 导出库存数据
     * 支持传入 ids 导出指定数据，不传则导出所有
     */
    @GetMapping("/export")
    public void export(@RequestParam(required = false) List<Long> ids, HttpServletResponse response) {
        try {
            // 查询数据
            List<StockInfo> list = (ids != null && !ids.isEmpty())
                    ? stockService.listByIds(ids)
                    : stockService.list();

            // 创建 Excel 工作簿
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("库存数据");

            // 创建表头
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "商品ID", "商品名称", "总库存", "可用库存", "锁定库存", "状态", "创建时间", "更新时间"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            for (int i = 0; i < list.size(); i++) {
                StockInfo stock = list.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(stock.getStockId());
                row.createCell(1).setCellValue(stock.getProductId());
                row.createCell(2).setCellValue(stock.getProductName() != null ? stock.getProductName() : "");
                row.createCell(3).setCellValue(stock.getStockCount() != null ? stock.getStockCount() : 0);
                row.createCell(4).setCellValue(stock.getAvailableCount() != null ? stock.getAvailableCount() : 0);
                row.createCell(5).setCellValue(stock.getLockedCount() != null ? stock.getLockedCount() : 0);
                row.createCell(6).setCellValue(stock.getStatus() != null ? stock.getStatus() : 0);
                row.createCell(7).setCellValue(stock.getCreateTime() != null ? stock.getCreateTime().toString() : "");
                row.createCell(8).setCellValue(stock.getUpdateTime() != null ? stock.getUpdateTime().toString() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            String fileName = URLEncoder.encode("库存数据.xlsx", StandardCharsets.UTF_8.toString());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            // 写入响应
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            log.error("导出库存数据失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }

    public Result<IPage<StockInfo>> blockHandler(Integer pageNum, Integer pageSize, String productName, Throwable e) {
        return Result.error(429, "请求过于频繁，请稍后重试");
    }
}
