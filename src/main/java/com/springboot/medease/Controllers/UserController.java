package com.springboot.medease.Controllers;
import com.springboot.medease.Models.RegisterResponse;
import com.springboot.medease.Models.User;
import com.springboot.medease.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> registerUser(@RequestBody User user) {

        RegisterResponse response = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}


