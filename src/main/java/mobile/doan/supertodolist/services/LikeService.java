package mobile.doan.supertodolist.services;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mobile.doan.supertodolist.model.Like;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.repository.LikeRepository;
import mobile.doan.supertodolist.services.PostService;
import mobile.doan.supertodolist.util.SecurityUtil;
import mobile.doan.supertodolist.util.error.AppException;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostService postService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Transactional
    public Like likePost(Long postId) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User currentUser = userService.getUserByEmail(email);
        if (currentUser == null) {
            throw new AppException("User not found");
        }

        Post post = postService.getPostById(postId);

        // Check if user already liked this post
        if (likeRepository.existsByUserAndPost(currentUser, post)) {
            throw new AppException("You have already liked this post");
        }

        Like like = new Like();
        like.setUser(currentUser);
        like.setPost(post);
        like = likeRepository.save(like);

        // Create notification for post owner
        notificationService.createPostLikedNotification(post.getUser(), post, currentUser, like);

        return like;
    }

    @Transactional
    public void unlikePost(Long postId) throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User currentUser = userService.getUserByEmail(email);
        if (currentUser == null) {
            throw new AppException("User not found");
        }

        Post post = postService.getPostById(postId);

        Like like = likeRepository.findByUserAndPost(currentUser, post);
        if (like == null) {
            throw new AppException("You have not liked this post");
        }

        likeRepository.delete(like);
    }
}