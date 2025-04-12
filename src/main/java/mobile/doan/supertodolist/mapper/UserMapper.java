package mobile.doan.supertodolist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.dto.response.ResUserWithFriendStatusDTO;
import mobile.doan.supertodolist.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(ReqUserDTO reqUserDTO);

    ResUserDTO toResUserDTO(User user);

    @Mapping(target = "isFriend", source = "isFriend")
    @Mapping(target = "requestSent", source = "requestSent")
    @Mapping(target = "requestReceived", source = "requestReceived")
    ResUserWithFriendStatusDTO toResUserWithFriendStatusDTO(User user, boolean isFriend,
            boolean requestSent, boolean requestReceived);
}
