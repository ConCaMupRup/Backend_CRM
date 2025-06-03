-- Insert roles if not exists
INSERT INTO roles (name, description) 
SELECT 'SUPERADMIN', 'Super Administrator with all access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SUPERADMIN');

INSERT INTO roles (name, description) 
SELECT 'ADMIN', 'Administrator with limited access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, description)
SELECT 'USER', 'Regular user with limited access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

INSERT INTO roles (name, description)
SELECT 'SALE', 'Sales staff with product and contract management access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'SALE');

-- Insert permissions for User Management
INSERT INTO permissions (name, description, module_code) VALUES
('VIEW_USERS', 'Xem danh sách người dùng', 'USER_MANAGEMENT'),
('CREATE_USER', 'Tạo người dùng mới', 'USER_MANAGEMENT'),
('EDIT_USER', 'Chỉnh sửa thông tin người dùng', 'USER_MANAGEMENT'),
('DELETE_USER', 'Xóa người dùng', 'USER_MANAGEMENT');

-- Insert permissions for Role Management
INSERT INTO permissions (name, description, module_code) VALUES
('VIEW_ROLES', 'Xem danh sách vai trò', 'ROLE_MANAGEMENT'),
('CREATE_ROLE', 'Tạo vai trò mới', 'ROLE_MANAGEMENT'),
('EDIT_ROLE', 'Chỉnh sửa vai trò', 'ROLE_MANAGEMENT'),
('DELETE_ROLE', 'Xóa vai trò', 'ROLE_MANAGEMENT');

-- Insert permissions for Product Management
INSERT INTO permissions (name, description, module_code) VALUES
('VIEW_PRODUCTS', 'Xem danh sách sản phẩm', 'PRODUCT_MANAGEMENT'),
('CREATE_PRODUCT', 'Tạo sản phẩm mới', 'PRODUCT_MANAGEMENT'),
('EDIT_PRODUCT', 'Chỉnh sửa sản phẩm', 'PRODUCT_MANAGEMENT'),
('DELETE_PRODUCT', 'Xóa sản phẩm', 'PRODUCT_MANAGEMENT');

-- Insert permissions for Contract Management
INSERT INTO permissions (name, description, module_code) VALUES
('VIEW_CONTRACTS', 'Xem danh sách hợp đồng', 'CONTRACT_MANAGEMENT'),
('CREATE_CONTRACT', 'Tạo hợp đồng mới', 'CONTRACT_MANAGEMENT'),
('EDIT_CONTRACT', 'Chỉnh sửa hợp đồng', 'CONTRACT_MANAGEMENT'),
('DELETE_CONTRACT', 'Xóa hợp đồng', 'CONTRACT_MANAGEMENT'),
('APPROVE_CONTRACT', 'Phê duyệt hợp đồng', 'CONTRACT_MANAGEMENT');

-- Assign all permissions to SUPERADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SUPERADMIN';

-- Assign permissions to ADMIN role (all except EDIT_CONTRACT)
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN'
AND p.name NOT IN ('EDIT_CONTRACT');

-- Assign permissions to SALE role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SALE'
AND (
    p.name IN ('VIEW_PRODUCTS', 'CREATE_PRODUCT', 'EDIT_PRODUCT', 'DELETE_PRODUCT', 'VIEW_USERS') -- Product permissions
    OR p.name IN ('VIEW_CONTRACTS', 'EDIT_CONTRACT') -- Contract permissions
);

-- Assign limited permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'USER'
AND p.name IN ('VIEW_USERS', 'VIEW_PRODUCTS', 'VIEW_CONTRACTS');

SELECT * FROM roles;
SELECT * FROM permissions;
SELECT * FROM role_permissions; 