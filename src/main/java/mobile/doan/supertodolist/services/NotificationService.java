package mobile.doan.supertodolist.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobile.doan.supertodolist.dto.response.ResFriendDTO;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.dto.websocket.WebSocketMessage;
import mobile.doan.supertodolist.model.Comment;
import mobile.doan.supertodolist.model.Friend;
import mobile.doan.supertodolist.model.Notification;
import mobile.doan.supertodolist.model.Post;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.model.Notification.NotificationType;
import mobile.doan.supertodolist.repository.NotificationRepository;
import mobile.doan.supertodolist.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    /**
     * Gửi thông báo lời mời kết bạn qua WebSocket
     */
    public void sendFriendRequestNotification(Long userId, ResFriendDTO friendRequestDTO) {
        try {
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("NEW_FRIEND_REQUEST")
                    .content(friendRequestDTO)
                    .build();

            // Gửi tới topic cụ thể của user
            String topic = "/topic/friend-requests/" + userId;
            log.info("Sending friend request notification to topic: {}", topic);
            messagingTemplate.convertAndSend(topic, message);

            // Gửi thêm một bản sao tới topic debug để dễ theo dõi
            messagingTemplate.convertAndSend("/topic/debug", message);

            log.info("WebSocket notification for friend request sent successfully");
        } catch (Exception e) {
            log.error("Error sending friend request notification via WebSocket: {}", e.getMessage(), e);
        }
    }

    /**
     * Create a notification for a friend request
     */
    public Notification createFriendRequestNotification(User receiver, Friend friendRequest) {
        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setType(NotificationType.FRIEND_REQUEST);
        notification.setFriend(friendRequest);
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        log.info("Created friend request notification ID: {} for user ID: {}", notification.getId(), receiver.getId());

        // Send WebSocket notification for real-time updates
        // sendNotificationToUser(receiver.getId(), notification);

        return notification;
    }

    /**
     * Create a notification when a friend request is accepted
     */
    public Notification createFriendAcceptedNotification(User receiver, Friend friendRequest) {
        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setType(NotificationType.FRIEND_REQUEST_ACCEPTED);
        notification.setFriend(friendRequest);
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        log.info("Created friend request accepted notification ID: {} for user ID: {}", notification.getId(),
                receiver.getId());

        // Send WebSocket notification (in real-time implementation)
        // sendNotificationToUser(receiver.getId(), notification);

        return notification;
    }

    /**
     * Create a notification when a post is liked
     */
    public Notification createPostLikedNotification(User postOwner, Post likedPost, User liker) {
        // Don't create notification if user likes their own post
        if (postOwner.getId() == liker.getId()) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(postOwner);
        notification.setType(NotificationType.POST_LIKED);
        notification.setPost(likedPost);
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        log.info("Created post liked notification ID: {} for user ID: {}", notification.getId(), postOwner.getId());

        // Send WebSocket notification (in real-time implementation)
        // sendNotificationToUser(postOwner.getId(), notification);

        return notification;
    }

    /**
     * Create a notification when a post is commented
     */
    public Notification createPostCommentedNotification(User postOwner, Post commentedPost, Comment comment,
            User commenter) {
        // Don't create notification if user comments on their own post
        if (postOwner.getId() == commenter.getId()) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(postOwner);
        notification.setType(NotificationType.POST_COMMENTED);
        notification.setPost(commentedPost);
        notification.setComment(comment);
        notification.setRead(false);

        notification = notificationRepository.save(notification);
        log.info("Created post comment notification ID: {} for user ID: {}", notification.getId(), postOwner.getId());

        // Send WebSocket notification (in real-time implementation)
        // sendNotificationToUser(postOwner.getId(), notification);

        return notification;
    }

    /**
     * Get all notifications for a user
     */
    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get paginated notifications for a user
     */
    public ResPaginationDTO getUserNotificationsPaginated(User user, Pageable pageable) {
        Page<Notification> pageNoti = notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageNoti.getTotalPages());
        meta.setTotal(pageNoti.getTotalElements());
        rs.setMeta(meta);
        List<Notification> listNoti = pageNoti.getContent();
        rs.setResult(listNoti);
        return rs;
    }

    /**
     * Mark a notification as read
     */
    public Notification markNotificationAsRead(long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            notification = notificationRepository.save(notification);
        }
        return notification;
    }

    /**
     * Mark all user notifications as read
     */
    public void markAllNotificationsAsRead(User user) {
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        for (Notification notification : notifications) {
            notification.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    /**
     * Count unread notifications for a user
     */
    public long countUnreadNotifications(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Method to send a WebSocket notification to a user
     */
    private void sendNotificationToUser(Long userId, Notification notification) {
        try {
            WebSocketMessage message = WebSocketMessage.builder()
                    .type("NEW_NOTIFICATION")
                    .content(notification)
                    .build();

            String topic = "/topic/notifications/" + userId;
            messagingTemplate.convertAndSend(topic, message);

            log.info("Sent notification via WebSocket to user {}", userId);
        } catch (Exception e) {
            log.error("Error sending notification via WebSocket: {}", e.getMessage(), e);
        }
    }
}