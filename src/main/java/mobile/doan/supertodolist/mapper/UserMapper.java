package mobile.doan.supertodolist.mapper;

import org.mapstruct.Mapper;

import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(ReqUserDTO reqUserDTO);

    ResUserDTO toResUserDTO(User user);
}
