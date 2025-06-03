package com.example.demo.service;

import com.example.demo.Model.Permission;
import com.example.demo.Model.PermissionRepository;
import com.example.demo.Model.Role;
import com.example.demo.Model.RoleRepository;
import com.example.demo.dto.MenuItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    public List<MenuItemDTO> generateMenuForRole(String roleName) {
        Optional<Role> roleOpt = roleRepository.findByName(roleName);
        if (roleOpt.isEmpty()) {
            return Collections.emptyList();
        }

        Role role = roleOpt.get();
        List<Permission> permissions = permissionRepository.findByRolesId(role.getId());

        // Group permissions by module
        Map<String, List<Permission>> modulePermissions = permissions.stream()
                .collect(Collectors.groupingBy(Permission::getModuleCode));

        List<MenuItemDTO> menuItems = new ArrayList<>();

        // Create menu items for each module
        modulePermissions.forEach((moduleCode, modulePerms) -> {
            MenuItemDTO moduleItem = new MenuItemDTO();
            moduleItem.setKey(moduleCode);
            moduleItem.setLabel(getModuleLabel(moduleCode));
            moduleItem.setIcon(getModuleIcon(moduleCode));
            
            List<MenuItemDTO> children = modulePerms.stream()
                    .map(this::permissionToMenuItem)
                    .collect(Collectors.toList());
            
            moduleItem.setChildren(children);
            menuItems.add(moduleItem);
        });

        return menuItems;
    }

    private MenuItemDTO permissionToMenuItem(Permission permission) {
        return new MenuItemDTO(
            permission.getName(),
            permission.getDescription(),
            getPermissionIcon(permission.getName()),
            getPermissionPath(permission.getName())
        );
    }

    private String getModuleLabel(String moduleCode) {
        Map<String, String> moduleLabels = new HashMap<>();
        moduleLabels.put("USER_MANAGEMENT", "Quản lý người dùng");
        moduleLabels.put("ROLE_MANAGEMENT", "Quản lý phân quyền");
        moduleLabels.put("PRODUCT_MANAGEMENT", "Quản lý sản phẩm");
        return moduleLabels.getOrDefault(moduleCode, moduleCode);
    }

    private String getModuleIcon(String moduleCode) {
        Map<String, String> moduleIcons = new HashMap<>();
        moduleIcons.put("USER_MANAGEMENT", "UserOutlined");
        moduleIcons.put("ROLE_MANAGEMENT", "SafetyCertificateOutlined");
        moduleIcons.put("PRODUCT_MANAGEMENT", "ShoppingOutlined");
        return moduleIcons.getOrDefault(moduleCode, "AppstoreOutlined");
    }

    private String getPermissionIcon(String permissionName) {
        Map<String, String> permissionIcons = new HashMap<>();
        // View permissions
        permissionIcons.put("VIEW_USERS", "EyeOutlined");
        permissionIcons.put("VIEW_ROLES", "EyeOutlined");
        permissionIcons.put("VIEW_PRODUCTS", "EyeOutlined");
        // Create permissions
        permissionIcons.put("CREATE_USER", "UserAddOutlined");
        permissionIcons.put("CREATE_ROLE", "PlusCircleOutlined");
        permissionIcons.put("CREATE_PRODUCT", "PlusSquareOutlined");
        // Edit permissions
        permissionIcons.put("EDIT_USER", "EditOutlined");
        permissionIcons.put("EDIT_ROLE", "EditOutlined");
        permissionIcons.put("EDIT_PRODUCT", "EditOutlined");
        // Delete permissions
        permissionIcons.put("DELETE_USER", "DeleteOutlined");
        permissionIcons.put("DELETE_ROLE", "DeleteOutlined");
        permissionIcons.put("DELETE_PRODUCT", "DeleteOutlined");
        
        return permissionIcons.getOrDefault(permissionName, "MenuOutlined");
    }

    private String getPermissionPath(String permissionName) {
        // Convert permission name to URL path
        return "/" + permissionName.toLowerCase().replace("_", "-");
    }
} 