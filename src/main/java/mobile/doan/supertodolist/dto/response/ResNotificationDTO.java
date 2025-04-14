package mobile.doan.supertodolist.dto.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import mobile.doan.supertodolist.model.Notification.NotificationType;

@Getter
@Setter
public class ResNotificationDTO {
    private Long id;
    private String content;
    private NotificationType type;
    private boolean isRead;
    private Instant createdAt;
    private Instant updatedAt;
    private Long userId;
    private Long postId; // Assuming the notification is related to a post
    private Long commentId; // Assuming the notification is related to a comment
    private Long friendId; // Assuming the notification is related to a friend request
    private Long likeId;
    private String userName; // Name of the user who triggered the notification

}
