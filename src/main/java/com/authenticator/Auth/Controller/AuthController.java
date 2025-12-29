package com.authenticator.Auth.Controller;

import com.authenticator.Auth.Service.PasswordResetService;
import com.authenticator.Auth.Service.AuthService;
import com.authenticator.Auth.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;


    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> Signup(@RequestBody SignupRequest signupRequest){
        String result = authService.signup(signupRequest);
        return ResponseEntity.ok(Map.of("message", result));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        passwordResetService.forgotpasswordservice(forgotPasswordRequest.getIdentifier());

        return ResponseEntity.ok(Map.of("message", "If the account exists, a password reset link has been sent to the registered email.")
        );
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        passwordResetService.resetpasswordservice(resetPasswordRequest.getToken(),resetPasswordRequest.getNewPassword());

        return ResponseEntity.ok(Map.of("Message","Password reset succcessfully"));
    }

}
