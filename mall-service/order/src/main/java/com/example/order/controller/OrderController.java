package com.example.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.order.entity.OrderInfo;
import com.example.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @GetMapping
    @SentinelResource(value = "orderList", blockHandler = "blockHandler")
    public Result<IPage<OrderInfo>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                         @RequestParam(defaultValue = "10") Integer pageSize,
                                         @RequestParam(required = false) String orderNo) {
        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        IPage<OrderInfo> result = orderService.page(page, orderNo);
        return Result.success(result);
    }

    @GetMapping("/{orderId}")
    public Result<OrderInfo> getById(@PathVariable Long orderId) {
        return Result.success(orderService.getById(orderId));
    }

    @PostMapping
    public Result<OrderInfo> create(@RequestBody OrderInfo order) {
        log.info("创建订单请求, productId={}", order.getProductId());
        OrderInfo created = orderService.createOrder(order);
        return Result.success("订单创建成功", created);
    }

    @PutMapping
    public Result<OrderInfo> update(@RequestBody OrderInfo order) {
        OrderInfo updated = orderService.updateOrder(order);
        return Result.success("订单更新成功", updated);
    }

    @DeleteMapping("/{orderId}")
    public Result<Void> delete(@PathVariable Long orderId) {
        orderService.delete(orderId);
        return Result.success("订单删除成功",null);
    }

    @GetMapping("/all")
    public Result<List<OrderInfo>> list() {
        return Result.success(orderService.list());
    }

    @PutMapping("/{orderId}/pay")
    public Result<OrderInfo> pay(@PathVariable Long orderId) {
        log.info("支付订单, orderId={}", orderId);
        return Result.success("支付成功", orderService.pay(orderId));
    }

    @PutMapping("/{orderId}/ship")
    public Result<OrderInfo> ship(@PathVariable Long orderId) {
        log.info("发货订单, orderId={}", orderId);
        return Result.success("发货成功", orderService.ship(orderId));
    }

    @PutMapping("/{orderId}/receive")
    public Result<OrderInfo> receive(@PathVariable Long orderId) {
        return Result.success("确认收货成功", orderService.receive(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public Result<OrderInfo> cancel(@PathVariable Long orderId) {
        log.info("取消订单, orderId={}", orderId);
        return Result.success("订单已取消", orderService.cancel(orderId));
    }

    /**
     * 导出订单数据
     * 支持传入 ids 导出指定数据，不传则导出所有
     */
    @GetMapping("/export")
    public void export(@RequestParam(required = false) List<Long> ids, HttpServletResponse response) {
        try {
            // 查询数据
            List<OrderInfo> list = (ids != null && !ids.isEmpty())
                    ? orderService.listByIds(ids)
                    : orderService.list();

            // 创建 Excel 工作簿
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("订单数据");

            // 创建表头
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "订单号", "客户ID", "总金额", "状态", "收货地址", "创建时间", "支付时间", "发货时间"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            for (int i = 0; i < list.size(); i++) {
                OrderInfo order = list.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getOrderNo());
                row.createCell(2).setCellValue(order.getCustomerId() != null ? order.getCustomerId() : 0);
                row.createCell(3).setCellValue(order.getTotalAmount() != null ? order.getTotalAmount().doubleValue() : 0.0);
                row.createCell(4).setCellValue(order.getStatus() != null ? order.getStatus() : 0);
                row.createCell(5).setCellValue(order.getAddress() != null ? order.getAddress() : "");
                row.createCell(6).setCellValue(order.getCreateTime() != null ? order.getCreateTime().toString() : "");
                row.createCell(7).setCellValue(order.getPayTime() != null ? order.getPayTime().toString() : "");
                row.createCell(8).setCellValue(order.getShipTime() != null ? order.getShipTime().toString() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            String fileName = URLEncoder.encode("订单数据.xlsx", StandardCharsets.UTF_8.toString());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            // 写入响应
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            log.error("导出订单数据失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }

    public Result<IPage<OrderInfo>> blockHandler(Integer pageNum, Integer pageSize, String orderNo, Throwable e) {
        return Result.error(429, "请求过于频繁，请稍后重试");
    }
}
