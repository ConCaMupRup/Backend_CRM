package com.example.demo.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.demo.dto.SignupRequest;
import com.example.demo.dto.LoginRequest;
import com.example.demo.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Model.MyAppUser;
import com.example.demo.Model.MyAppUserRepository;
import com.example.demo.Model.Role;
import com.example.demo.Model.RoleRepository;
import com.example.demo.service.EmailService;
import com.example.demo.utils.JwtTokenUtil;

@RestController
@RequestMapping("/req")
@CrossOrigin(origins = "*")
public class RegistrationController {

    @Autowired
    private MyAppUserRepository myAppUserRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MenuService menuService;

    // ĐĂNG KÝ TÀI KHOẢN
    @PostMapping(value = "/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@RequestBody SignupRequest signupRequest) {
        Optional<MyAppUser> existingUserOpt = myAppUserRepository.findByEmail(signupRequest.getEmail());

        if (existingUserOpt.isPresent()) {
            MyAppUser existingUser = existingUserOpt.get();

            if (existingUser.isVerified()) {
                return new ResponseEntity<>("User already exists and is verified.", HttpStatus.BAD_REQUEST);
            } else {
                String token = JwtTokenUtil.generateToken(existingUser.getEmail());
                existingUser.setVerificationToken(token);
                myAppUserRepository.save(existingUser);

                // ✅ IN RA MÃ XÁC THỰC CHO DỄ TEST
                System.out.println("✅ [DEBUG] Mã xác thực gửi lại cho " + existingUser.getEmail() + ": " + token);

                emailService.sendVerificationEmail(existingUser.getEmail(), token);
                return new ResponseEntity<>("Verification email resent. Check your inbox.", HttpStatus.OK);
            }
        }

        MyAppUser user = new MyAppUser();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));

        // Determine role based on username or email
        final String roleName;
        String emailLower = signupRequest.getEmail().toLowerCase();
        String usernameLower = signupRequest.getUsername().toLowerCase();
        
        if (emailLower.contains("admin@") || usernameLower.equals("admin")) {
            roleName = "ADMIN";
        } else if (emailLower.contains("admin1@") || usernameLower.equals("admin1")) {
            roleName = "ADMIN1";
        } else {
            roleName = "USER";
        }

        Role userRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " not found"));
        user.setRole(userRole);
        user.setVerified(false);

        String verificationToken = JwtTokenUtil.generateToken(user.getEmail());
        user.setVerificationToken(verificationToken);

        myAppUserRepository.save(user);

        // ✅ IN RA MÃ XÁC THỰC CHO DỄ TEST
        System.out.println("✅ [DEBUG] Mã xác thực cho " + user.getEmail() + ": " + verificationToken);

        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return new ResponseEntity<>("Registration successful! Please verify your email.", HttpStatus.OK);
    }

    // ✅ XÁC THỰC EMAIL TỪ TOKEN
    @GetMapping("/signup/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("token") String token) {
    Optional<MyAppUser> userOpt = myAppUserRepository.findByVerificationToken(token);

    if (userOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Token không hợp lệ.");
    }

    MyAppUser user = userOpt.get();
    user.setVerified(true);
    user.setVerificationToken(null); // xoá token nếu muốn
    myAppUserRepository.save(user);

    return ResponseEntity.ok("✅ Email verified");
    }



    // ĐĂNG NHẬP
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<MyAppUser> userOpt = myAppUserRepository.findByEmail(loginRequest.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Email không tồn tại");
        }

        MyAppUser user = userOpt.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("❌ Mật khẩu sai");
        }

        if (!user.isVerified()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("❌ Vui lòng xác minh email trước");
        }

        String token = JwtTokenUtil.generateToken(user.getEmail());
        String roleName = user.getRole().getName();

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
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
