package mobile.doan.supertodolist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResUserWithFriendStatusDTO {
    long id;
    String email;
    String name;
    String phone;
    String avatar;
    boolean isFriend;
    boolean requestSent;
    boolean requestReceived;
}