package mobile.doan.supertodolist.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Value("${nqoctai.jwt.refresh-token-validity-in-seconds}")
    long refreshTokenExpiration;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ResLoginDTO>> login(@RequestBody ReqLoginDTO rqLogin) throws AppException {
        ResLoginDTO res = this.authService.login(rqLogin);
        String refreshToken = this.securityUtil.createRefreshToken(res.getUser().getEmail());
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();
        ApiResponse<ResLoginDTO> response = ApiResponse.<ResLoginDTO>builder()
                .status(200)
                .message("Login successful")
                .data(res)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(response);
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

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ResUserDTO>> getCurrentUser() throws AppException {
        ResUserDTO res = this.authService.getCurrentUser();
        ApiResponse<ResUserDTO> response = ApiResponse.<ResUserDTO>builder()
                .status(200)
                .message("Get current user successful")
                .data(res)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<ResLoginDTO>> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token)
            throws AppException {
        if (refresh_token.equals("abc")) {
            throw new AppException("Bạn không có refresh token ở cookie");
        }

        // check valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        if (email == null) {
            throw new AppException("Refresh token không hợp lệ");
        }

        ResLoginDTO resLoginDTO = this.authService.getRefreshToken(email);

        // create new refresh token
        String new_refresh_token = this.securityUtil.createRefreshToken(email);

        // set new refresh token to cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        ApiResponse<ResLoginDTO> response = ApiResponse.<ResLoginDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Refresh token successful")
                .data(resLoginDTO)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(response);

    }
}
