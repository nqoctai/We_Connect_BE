package mobile.doan.supertodolist.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mobile.doan.supertodolist.model.Friend.FriendStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResFriendDTO {
    private long id;
    private ResUserDTO sender;
    private ResUserDTO receiver;
    private FriendStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}