package mobile.doan.supertodolist.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.model.Like;
import mobile.doan.supertodolist.services.LikeService;
import mobile.doan.supertodolist.util.error.AppException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<Like>> likePost(@PathVariable Long postId) throws AppException {
        Like like = likeService.likePost(postId);

        return ResponseEntity.ok(ApiResponse.<Like>builder()
                .status(HttpStatus.CREATED.value())
                .message("Post liked successfully")
                .data(like)
                .build());
    }

    @DeleteMapping("/posts/{postId}/unlike")
    public ResponseEntity<ApiResponse<String>> unlikePost(@PathVariable Long postId) throws AppException {
        likeService.unlikePost(postId);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Post unliked successfully")
                .build());
    }
}