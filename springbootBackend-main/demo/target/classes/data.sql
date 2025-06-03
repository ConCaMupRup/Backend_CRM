-- Insert roles if not exists
INSERT INTO roles (name, description) 
SELECT 'ADMIN', 'Administrator with full access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name, description)
SELECT 'USER', 'Regular user with limited access'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

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

-- Assign all permissions to ADMIN role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN';

-- Assign limited permissions to USER role
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'USER'
AND p.name IN ('VIEW_USERS', 'VIEW_PRODUCTS');

SELECT * FROM roles;
SELECT * FROM permissions;
SELECT * FROM role_permissions; 