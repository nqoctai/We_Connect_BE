package mobile.doan.supertodolist.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqLoginDTO;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResLoginDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.services.AuthService;
import mobile.doan.supertodolist.util.SecurityUtil;
import mobile.doan.supertodolist.util.error.AppException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AuthController {

    final AuthService authService;
    final SecurityUtil securityUtil;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ResLoginDTO>> login(@RequestBody ReqLoginDTO rqLogin) throws AppException {
        ResLoginDTO res = this.authService.login(rqLogin);
        ApiResponse<ResLoginDTO> response = ApiResponse.<ResLoginDTO>builder()
                .status(200)
                .message("Login successful")
                .data(res)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ResUserDTO>> register(@RequestBody ReqUserDTO rqUser) throws AppException {
        ResUserDTO res = this.authService.register(rqUser);
        ApiResponse<ResUserDTO> response = ApiResponse.<ResUserDTO>builder()
                .status(200)
                .message("Register successful")
                .data(res)
                .build();

        return ResponseEntity.ok(response);
    }
}
