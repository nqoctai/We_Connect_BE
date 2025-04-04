package mobile.doan.supertodolist.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ApiResponse<T> {
    int status;
    Object message;
    T data;
    String error;
}
