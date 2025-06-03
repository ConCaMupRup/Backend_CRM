// package com.example.demo.Controller;

// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.demo.Model.MyAppUser;
// import com.example.demo.Model.MyAppUserRepository;
// import com.example.demo.utils.JwtTokenUtil;

// @RestController
// public class VerificationController {

//     @Autowired
//     private MyAppUserRepository myAppUserRepository;

//     @Autowired
//     private JwtTokenUtil jwtUtil;

//     @GetMapping("/req/signup/verify")
//     public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
//         try {
//             String email = jwtUtil.extractEmail(token);
//             Optional<MyAppUser> optionalUser = myAppUserRepository.findByEmail(email);

//             if (optionalUser.isEmpty() || optionalUser.get().getVerificationToken() == null) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token.");
//             }

//             MyAppUser user = optionalUser.get();

//             if (!jwtUtil.validateToken(token) || !user.getVerificationToken().equals(token)) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token.");
//             }

//             user.setVerificationToken(null);
//             user.setVerified(true);
//             myAppUserRepository.save(user);

//             return ResponseEntity.status(HttpStatus.CREATED).body("Email successfully verified!");

//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed: " + e.getMessage());
//         }
//     }
// }
