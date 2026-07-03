package com.example.system.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.result.Result;
import com.example.system.entity.Customer;
import com.example.system.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    @GetMapping
    @SentinelResource(value = "customerList", blockHandler = "blockHandler")
    public Result<IPage<Customer>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                        @RequestParam(defaultValue = "10") Integer pageSize,
                                        @RequestParam(required = false) String name) {
        log.info("查询客户列表");
        Page<Customer> page = new Page<>(pageNum, pageSize);
        IPage<Customer> result = customerService.page(page, name);
        return Result.success(result);
    }

    @GetMapping("/{customerId:\\d+}")
    public Result<Customer> getById(@PathVariable Long customerId) {
        log.info("查询客户详情, customerId={}", customerId);
        return Result.success(customerService.getById(customerId));
    }

    @PostMapping
    public Result<Customer> save(@RequestBody Customer customer) {
        log.info("创建客户, name={}", customer.getName());
        Customer saved = customerService.save(customer);
        return Result.success("客户创建成功", saved);
    }

    @PutMapping
    public Result<Customer> update(@RequestBody Customer customer) {
        log.info("更新客户, customerId={}", customer.getCustomerId());
        Customer updated = customerService.update(customer);
        return Result.success("客户更新成功", updated);
    }

    @DeleteMapping("/{customerId:\\d+}")
    public Result<String> delete(@PathVariable Long customerId) {
        log.info("删除客户, customerId={}", customerId);
        customerService.delete(customerId);
        return Result.success("客户删除成功",null);
    }

    @GetMapping("/all")
    public Result<List<Customer>> list() {
        return Result.success(customerService.list());
    }

    /**
     * 导出客户数据
     * 支持传入 ids 导出指定数据，不传则导出所有
     */
    @GetMapping("/export")
    public void export(@RequestParam(required = false) List<Long> ids, HttpServletResponse response) {
        try {
            // 查询数据
            List<Customer> list = (ids != null && !ids.isEmpty())
                    ? customerService.listByIds(ids)
                    : customerService.list();

            // 创建 Excel 工作簿
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("客户数据");

            // 创建表头
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "客户编号", "客户名称", "联系人", "手机号", "邮箱", "地址", "公司", "状态", "创建时间"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 填充数据
            for (int i = 0; i < list.size(); i++) {
                Customer customer = list.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(customer.getCustomerId() != null ? customer.getCustomerId().doubleValue() : 0);
                row.createCell(1).setCellValue(customer.getCustomerNo() != null ? customer.getCustomerNo() : "");
                row.createCell(2).setCellValue(customer.getName() != null ? customer.getName() : "");
                row.createCell(3).setCellValue(customer.getContactName() != null ? customer.getContactName() : "");
                row.createCell(4).setCellValue(customer.getPhone() != null ? customer.getPhone() : "");
                row.createCell(5).setCellValue(customer.getEmail() != null ? customer.getEmail() : "");
                row.createCell(6).setCellValue(customer.getAddress() != null ? customer.getAddress() : "");
                row.createCell(7).setCellValue(customer.getCompany() != null ? customer.getCompany() : "");
                row.createCell(8).setCellValue(customer.getStatus() != null ? customer.getStatus().doubleValue() : 0);
                row.createCell(9).setCellValue(customer.getCreateTime() != null ? customer.getCreateTime().toString() : "");
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 设置响应头
            String fileName = URLEncoder.encode("客户数据.xlsx", StandardCharsets.UTF_8.toString());
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

            // 写入响应
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            log.error("导出客户数据失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }

    public Result<IPage<Customer>> blockHandler(Integer pageNum, Integer pageSize, String name, Throwable e) {
        return Result.error(429, "请求过于频繁，请稍后重试");
    }
}
