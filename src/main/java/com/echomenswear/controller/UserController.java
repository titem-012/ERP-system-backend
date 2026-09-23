package com.echomenswear.controller;

import com.echomenswear.dto.RegisterRequest;
import com.echomenswear.model.User;
import com.echomenswear.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "https://erp-system-uxm2.vercel.app")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/cashiers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllCashiers() {
        return ResponseEntity.ok(userService.getAllCashiers());
    }

    @PostMapping("/cashier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createCashier(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(userService.createCashier(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
