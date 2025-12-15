

// ============================================
// FILE 4: src/main/java/com/fintech/platform/controller/UserController.java
// ============================================
package com.fintech.platform.controller;

import com.fintech.platform.dto.ApiResponse;
import com.fintech.platform.dto.UserDTO;
import com.fintech.platform.model.User;
import com.fintech.platform.repository.UserRepository;
import com.fintech.platform.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDTO>> getUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            return ResponseEntity.ok(ApiResponse.success("Profile retrieved", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            return ResponseEntity.ok(ApiResponse.success("User retrieved", userDTO));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("User not found"));
        }
    }
}
