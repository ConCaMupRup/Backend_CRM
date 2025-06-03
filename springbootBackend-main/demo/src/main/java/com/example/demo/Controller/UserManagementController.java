package com.example.demo.Controller;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;
import com.example.demo.Model.Role;
import com.example.demo.Model.RoleRepository;
import com.example.demo.dto.AssignRoleRequest;
import com.example.demo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserManagementController {

    @Autowired
    private MyAppUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuService menuService;

    // Lấy danh sách tất cả user (chỉ SUPERADMIN mới có quyền)
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
            .map(user -> {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("email", user.getEmail());
                userMap.put("role", user.getRole().getName());
                userMap.put("verified", user.isVerified());
                return userMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(users);
    }

    // Lấy danh sách tất cả role có sẵn (chỉ SUPERADMIN mới có quyền)
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Map<String, Object>> roles = roleRepository.findAll().stream()
            .map(role -> {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("id", role.getId());
                roleMap.put("name", role.getName());
                roleMap.put("description", role.getDescription());
                return roleMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(roles);
    }

    // Gán role cho user (chỉ SUPERADMIN mới có quyền)
    @PostMapping("/assign-role")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<?> assignRole(@RequestBody AssignRoleRequest request) {
        MyAppUser user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByName(request.getRoleName())
            .orElseThrow(() -> new RuntimeException("Role not found"));

        // Kiểm tra nếu user hiện tại là SUPERADMIN và đang cố gắng thay đổi role của chính mình
        if (user.getRole().getName().equals("SUPERADMIN") && !request.getRoleName().equals("SUPERADMIN")) {
            return ResponseEntity.badRequest()
                .body("Cannot change role of an SUPERADMIN user");
        }

        user.setRole(role);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Role assigned successfully");
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("newRole", role.getName());

        return ResponseEntity.ok(response);
    }

    // Lấy thông tin chi tiết của một user (chỉ SUPERADMIN mới có quyền)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ResponseEntity<?> getUserDetails(@PathVariable Long id) {
        MyAppUser user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("id", user.getId());
        userDetails.put("username", user.getUsername());
        userDetails.put("email", user.getEmail());
        userDetails.put("role", user.getRole().getName());
        userDetails.put("verified", user.isVerified());

        return ResponseEntity.ok(userDetails);
    }

    // Get current user's information including role and menu
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        MyAppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String roleName = user.getRole().getName();

        Map<String, Object> response = new HashMap<>();
        response.put("role", roleName);
        response.put("menu", menuService.generateMenuForRole(roleName));
        response.put("user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail()
        ));

        return ResponseEntity.ok(response);
    }
} 