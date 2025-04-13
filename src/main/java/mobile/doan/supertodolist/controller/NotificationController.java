package mobile.doan.supertodolist.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mobile.doan.supertodolist.dto.response.ApiResponse;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.model.Notification;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.services.NotificationService;
import mobile.doan.supertodolist.services.UserService;
import mobile.doan.supertodolist.util.SecurityUtil;
import mobile.doan.supertodolist.util.error.AppException;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<ApiResponse<ResPaginationDTO>> getPaginatedNotifications(Pageable pageable)
            throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }

        ResPaginationDTO notifications = notificationService.getUserNotificationsPaginated(user, pageable);
        ApiResponse<ResPaginationDTO> response = ApiResponse.<ResPaginationDTO>builder()
                .status(HttpStatus.OK.value())
                .message("Notifications retrieved successfully")
                .data(notifications)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Long>> getUnreadNotificationCount() throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }

        Long count = notificationService.countUnreadNotifications(user);

        return ResponseEntity.ok(ApiResponse.<Long>builder()
                .status(HttpStatus.OK.value())
                .message("Unread notification count retrieved successfully")
                .data(count)
                .build());
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markNotificationAsRead(@PathVariable Long id) throws AppException {
        Notification notification = notificationService.markNotificationAsRead(id);

        if (notification == null) {
            throw new AppException("Notification not found");
        }

        return ResponseEntity.ok(ApiResponse.<Notification>builder()
                .status(HttpStatus.OK.value())
                .message("Notification marked as read")
                .data(notification)
                .build());
    }

    @PutMapping("/read-all")
    public ResponseEntity<ApiResponse<String>> markAllNotificationsAsRead() throws AppException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new AppException("User not authenticated"));

        User user = userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException("User not found");
        }

        notificationService.markAllNotificationsAsRead(user);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("All notifications marked as read")
                .build());
    }
}