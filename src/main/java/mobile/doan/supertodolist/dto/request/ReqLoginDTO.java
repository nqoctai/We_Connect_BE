package mobile.doan.supertodolist.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;

import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ReqLoginDTO {

    @NotBlank(message = "Username is required")
    String username;

    @Min(value = 6, message = "Password must be at least 6 characters long")
    @NotBlank(message = "Password is required")
    String password;
}
