package mobile.doan.supertodolist.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ResLoginDTO {
    String accessToken;

    ResUserDTO user;
}
