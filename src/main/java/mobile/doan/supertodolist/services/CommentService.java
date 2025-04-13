package mobile.doan.supertodolist.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mobile.doan.supertodolist.dto.request.ReqCommentDTO;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.model.Comment;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.repository.CommentRepository;
import mobile.doan.supertodolist.util.SecurityUtil;
import mobile.doan.supertodolist.util.error.AppException;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public Comment addComment(Long postId, ReqCommentDTO commentDTO) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User currentUser = userService.getUserByEmail(email);
        if (currentUser == null) {
            throw new AppException("User not found");
        }

        Post post = postService.getPostById(postId);

        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);

        comment = commentRepository.save(comment);

        // Create notification for post owner (if commenter is not the owner)
        if (!currentUser.getId().equals(post.getUser().getId())) {
            notificationService.createPostCommentedNotification(post.getUser(), post, comment, currentUser);
        }

        return comment;
    }

    public Comment getCommentById(Long commentId) throws AppException {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found"));
    }

    public List<Comment> getPostComments(Long postId) throws AppException {
        Post post = postService.getPostById(postId);
        return commentRepository.findByPostOrderByCreatedAtDesc(post);
    }

    public ResPaginationDTO getPostCommentsPaginated(Long postId, Pageable pageable) throws AppException {
        Post post = postService.getPostById(postId);
        Page<Comment> pageComments = commentRepository.findByPostOrderByCreatedAtDesc(post, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageComments.getTotalPages());
        meta.setTotal(pageComments.getTotalElements());
        rs.setMeta(meta);
        List<Comment> listComments = pageComments.getContent();
        rs.setResult(listComments);
        return rs;
    }

    @Transactional
    public Comment updateComment(Long commentId, ReqCommentDTO commentDTO) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User currentUser = userService.getUserByEmail(email);
        if (currentUser == null) {
            throw new AppException("User not found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found"));

        // Check if current user is the author of the comment
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new AppException("You can only update your own comments");
        }

        comment.setContent(commentDTO.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User currentUser = userService.getUserByEmail(email);
        if (currentUser == null) {
            throw new AppException("User not found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found"));

        // Check if current user is the author of the comment or the post owner
        if (!comment.getUser().getId().equals(currentUser.getId()) &&
                !comment.getPost().getUser().getId().equals(currentUser.getId())) {
            throw new AppException("You can only delete your own comments or comments on your posts");
        }

        commentRepository.delete(comment);
    }
}