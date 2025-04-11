package mobile.doan.supertodolist.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqCreatePostDTO;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.services.PostService;
import mobile.doan.supertodolist.services.UserService;
import mobile.doan.supertodolist.util.error.AppException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class PostController {
    final PostService postService;
    final UserService userService;

    @PostMapping("/post")
    public ResponseEntity<ApiResponse<Post>> createPost(@RequestBody ReqCreatePostDTO req) throws AppException {
        Post post = postService.createPost(req);
        return ResponseEntity.ok(ApiResponse.<Post>builder()
                .status(200)
                .message("Create post successfully")
                .data(post)
                .build());
    }

    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<ResPaginationDTO>> getAllPostWithPagination(@Filter Specification<Post> spec,
            Pageable pageable) {
        ResPaginationDTO res = postService.getAllPostWithPagination(spec, pageable);
        return ResponseEntity.ok(ApiResponse.<ResPaginationDTO>builder()
                .status(200)
                .message("Get all post successfully")
                .data(res)
                .build());
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable("id") Long id) throws AppException {
        Post post = postService.getPostById(id);
        return ResponseEntity.ok(ApiResponse.<Post>builder()
                .status(200)
                .message("Get post successfully")
                .data(post)
                .build());
    }

}
