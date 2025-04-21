package com.xxx.user.service.rest;

import com.nimbusds.jose.JOSEException;
import com.xxx.user.service.data.user.LoginRequest;
import com.xxx.user.service.data.user.User;
import com.xxx.user.service.services.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {
    private final UserService userRestService;

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody User user) throws JOSEException {
        return ResponseEntity.ok(userRestService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest login) throws JOSEException {
        return ResponseEntity.ok(userRestService.login(login));
    }
}
