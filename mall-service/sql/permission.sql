-- ==================== 权限表 ====================
-- 存储细粒度的接口权限定义
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `permission_id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '权限ID',
    `permission_code` VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码（如：system:user:list）',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称（如：用户列表）',
    `module` VARCHAR(50) COMMENT '所属模块（如：system）',
    `path` VARCHAR(200) COMMENT '接口路径',
    `method` VARCHAR(10) COMMENT 'HTTP方法（GET/POST/PUT/DELETE）',
    `description` VARCHAR(500) COMMENT '权限描述',
    `status` TINYINT(4) DEFAULT 0 COMMENT '状态（0正常 1禁用）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_permission_code` (`permission_code`),
    INDEX `idx_module` (`module`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ==================== 角色权限关联表 ====================
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    INDEX `idx_role_id` (`role_id`),
    INDEX `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ==================== 用户权限关联表（直接授权） ====================
CREATE TABLE IF NOT EXISTS `sys_user_permission` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY `uk_user_permission` (`user_id`, `permission_id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户权限关联表';

-- ==================== 初始化权限数据 ====================
INSERT INTO `sys_permission` (`permission_code`, `permission_name`, `module`, `path`, `method`, `description`) VALUES
-- 系统管理模块
('system:user:list', '用户列表', 'system', '/api/system/users', 'GET', '查看用户列表'),
('system:user:create', '创建用户', 'system', '/api/system/users', 'POST', '创建新用户'),
('system:user:update', '修改用户', 'system', '/api/system/users/{id}', 'PUT', '修改用户信息'),
('system:user:delete', '删除用户', 'system', '/api/system/users/{id}', 'DELETE', '删除用户'),
('system:user:resetPwd', '重置密码', 'system', '/api/system/users/{id}/resetPassword', 'POST', '重置用户密码'),

('system:role:list', '角色列表', 'system', '/api/system/roles', 'GET', '查看角色列表'),
('system:role:create', '创建角色', 'system', '/api/system/roles', 'POST', '创建新角色'),
('system:role:update', '修改角色', 'system', '/api/system/roles/{id}', 'PUT', '修改角色信息'),
('system:role:delete', '删除角色', 'system', '/api/system/roles/{id}', 'DELETE', '删除角色'),
('system:role:assignPerm', '分配权限', 'system', '/api/system/roles/{id}/permissions', 'POST', '为角色分配权限'),

('system:menu:list', '菜单列表', 'system', '/api/system/menus', 'GET', '查看菜单列表'),
('system:menu:create', '创建菜单', 'system', '/api/system/menus', 'POST', '创建新菜单'),
('system:menu:update', '修改菜单', 'system', '/api/system/menus/{id}', 'PUT', '修改菜单信息'),
('system:menu:delete', '删除菜单', 'system', '/api/system/menus/{id}', 'DELETE', '删除菜单'),

-- 客户管理模块
('business:customer:list', '客户列表', 'business', '/api/system/customers', 'GET', '查看客户列表'),
('business:customer:create', '创建客户', 'business', '/api/system/customers', 'POST', '创建新客户'),
('business:customer:update', '修改客户', 'business', '/api/system/customers/{id}', 'PUT', '修改客户信息'),
('business:customer:delete', '删除客户', 'business', '/api/system/customers/{id}', 'DELETE', '删除客户'),

-- 产品管理模块
('business:product:list', '产品列表', 'business', '/api/system/products', 'GET', '查看产品列表'),
('business:product:create', '创建产品', 'business', '/api/system/products', 'POST', '创建新产品'),
('business:product:update', '修改产品', 'business', '/api/system/products/{id}', 'PUT', '修改产品信息'),
('business:product:delete', '删除产品', 'business', '/api/system/products/{id}', 'DELETE', '删除产品'),

-- 订单管理模块
('business:order:list', '订单列表', 'business', '/api/order/orders', 'GET', '查看订单列表'),
('business:order:detail', '订单详情', 'business', '/api/order/orders/{id}', 'GET', '查看订单详情'),
('business:order:update', '修改订单', 'business', '/api/order/orders/{id}', 'PUT', '修改订单状态'),
('business:order:delete', '删除订单', 'business', '/api/order/orders/{id}', 'DELETE', '删除订单'),

-- 库存管理模块
('business:stock:list', '库存列表', 'business', '/api/stock/stocks', 'GET', '查看库存列表'),
('business:stock:update', '库存调整', 'business', '/api/stock/stocks/{id}', 'PUT', '调整库存数量'),
('business:stock:log', '库存日志', 'business', '/api/stock/logs', 'GET', '查看库存变动日志'),

-- 系统监控模块
('monitor:log:list', '操作日志', 'monitor', '/api/system/logs', 'GET', '查看操作日志'),
('monitor:system:info', '系统信息', 'monitor', '/api/monitor/system', 'GET', '查看系统信息');

-- 为超级管理员角色分配所有权限
INSERT INTO `sys_role_permission` (`role_id`, `permission_id`)
SELECT r.role_id, p.permission_id
FROM `sys_role` r, `sys_permission` p
WHERE r.role_code = 'admin';
