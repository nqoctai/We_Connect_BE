package mobile.doan.supertodolist.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mobile.doan.supertodolist.dto.request.ReqCommentDTO;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.model.Comment;
import mobile.doan.supertodolist.services.CommentService;
import mobile.doan.supertodolist.util.error.AppException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {

        private final CommentService commentService;

        @PostMapping("/posts/{postId}/comments")
        public ResponseEntity<ApiResponse<Comment>> addComment(
                        @PathVariable Long postId,
                        @RequestBody ReqCommentDTO commentDTO) throws AppException {

                Comment comment = commentService.addComment(postId, commentDTO);

                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiResponse.<Comment>builder()
                                                .status(HttpStatus.CREATED.value())
                                                .message("Comment added successfully")
                                                .data(comment)
                                                .build());
        }

        @GetMapping("/posts/{postId}/comments")
        public ResponseEntity<ApiResponse<List<Comment>>> getPostComments(
                        @PathVariable Long postId) throws AppException {

                List<Comment> comments = commentService.getPostComments(postId);

                return ResponseEntity.ok(ApiResponse.<List<Comment>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Comments retrieved successfully")
                                .data(comments)
                                .build());
        }

        @GetMapping("/posts/{postId}/comments/paginated")
        public ResponseEntity<ApiResponse<ResPaginationDTO>> getPostCommentsPaginated(
                        @PathVariable Long postId,
                        Pageable pageable) throws AppException {

                ResPaginationDTO rs = commentService.getPostCommentsPaginated(postId, pageable);
                ApiResponse<ResPaginationDTO> response = ApiResponse.<ResPaginationDTO>builder()
                                .status(HttpStatus.OK.value())
                                .message("Comments retrieved successfully")
                                .data(rs)
                                .build();

                return ResponseEntity.ok(response);
        }

        @PutMapping("/comments/{commentId}")
        public ResponseEntity<ApiResponse<Comment>> updateComment(
                        @PathVariable Long commentId,
                        @RequestBody ReqCommentDTO commentDTO) throws AppException {

                Comment comment = commentService.updateComment(commentId, commentDTO);

                return ResponseEntity.ok(ApiResponse.<Comment>builder()
                                .status(HttpStatus.OK.value())
                                .message("Comment updated successfully")
                                .data(comment)
                                .build());
        }

        @DeleteMapping("/comments/{commentId}")
        public ResponseEntity<ApiResponse<String>> deleteComment(
                        @PathVariable Long commentId) throws AppException {

                commentService.deleteComment(commentId);

                return ResponseEntity.ok(ApiResponse.<String>builder()
                                .status(HttpStatus.OK.value())
                                .message("Comment deleted successfully")
                                .build());
        }
}