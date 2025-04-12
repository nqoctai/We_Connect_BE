package mobile.doan.supertodolist.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqFriendDTO;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResFriendDTO;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.services.FriendService;
import mobile.doan.supertodolist.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    FriendService friendService;

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

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<ResPaginationDTO>> getAllUserWithPagination(@Filter Specification<User> spec,
            Pageable pageable) {
        ResPaginationDTO res = userService.getAllUserWithPagination(spec, pageable);
        return ResponseEntity.ok(ApiResponse.<ResPaginationDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Get all user successfully")
                .data(res)
                .build());
    }

    @PostMapping("/friends/request")
    public ResponseEntity<ApiResponse<ResFriendDTO>> sendFriendRequest(@RequestBody ReqFriendDTO reqFriendDTO) {
        ResFriendDTO resFriendDTO = friendService.sendFriendRequest(reqFriendDTO);
        return ResponseEntity.ok(ApiResponse.<ResFriendDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Friend request sent successfully")
                .data(resFriendDTO)
                .build());
    }

    @PutMapping("/friends/accept/{friendRequestId}")
    public ResponseEntity<ApiResponse<ResFriendDTO>> acceptFriendRequest(@PathVariable Long friendRequestId) {
        ResFriendDTO resFriendDTO = friendService.acceptFriendRequest(friendRequestId);
        return ResponseEntity.ok(ApiResponse.<ResFriendDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Friend request accepted successfully")
                .data(resFriendDTO)
                .build());
    }

    @PutMapping("/friends/reject/{friendRequestId}")
    public ResponseEntity<ApiResponse<ResFriendDTO>> rejectFriendRequest(@PathVariable Long friendRequestId) {
        ResFriendDTO resFriendDTO = friendService.rejectFriendRequest(friendRequestId);
        return ResponseEntity.ok(ApiResponse.<ResFriendDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Friend request rejected successfully")
                .data(resFriendDTO)
                .build());
    }

    @GetMapping("/friends/requests")
    public ResponseEntity<ApiResponse<List<ResFriendDTO>>> getFriendRequests() {
        List<ResFriendDTO> friendRequests = friendService.getReceivedFriendRequests();
        return ResponseEntity.ok(ApiResponse.<List<ResFriendDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Get friend requests successfully")
                .data(friendRequests)
                .build());
    }

    @GetMapping("/friends")
    public ResponseEntity<ApiResponse<List<ResFriendDTO>>> getFriends() {
        List<ResFriendDTO> friends = friendService.getFriends();
        return ResponseEntity.ok(ApiResponse.<List<ResFriendDTO>>builder()
                .status(HttpStatus.OK.value())
                .message("Get friends successfully")
                .data(friends)
                .build());
    }
}
