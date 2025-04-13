package mobile.doan.supertodolist.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqCommentDTO {

    @NotBlank(message = "Comment content cannot be empty")
    private String content;
}