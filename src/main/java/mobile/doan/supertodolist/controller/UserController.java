package mobile.doan.supertodolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.services.UserService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<ResUserDTO>> createUser(@RequestBody ReqUserDTO reqUserDTO) {
        ResUserDTO response = userService.createUser(reqUserDTO);
        if (response == null) {
            throw new RuntimeException("User already exists");
        }
        ApiResponse<ResUserDTO> apiResponse = ApiResponse.<ResUserDTO>builder()
                .status(HttpStatus.CREATED.value())
                .message("User created successfully")
                .data(response)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);

    }
}
