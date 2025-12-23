package com.authenticator.Controller;

import com.authenticator.Service.PasswordResetService;
import com.authenticator.Service.UserService;
import com.authenticator.dto.*;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @GetMapping("/check")
    public String check() {
        return "checking port";
    }

    @PostMapping("/signup")
    public String Signup(@RequestBody SignupRequest signupRequest){
        return userService.signup(signupRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
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
