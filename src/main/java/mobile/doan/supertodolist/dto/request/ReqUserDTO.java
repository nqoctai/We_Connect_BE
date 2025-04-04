package mobile.doan.supertodolist.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ReqUserDTO {
    String name;
    String email;
    String password;
    String phone;
}
