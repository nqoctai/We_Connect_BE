package mobile.doan.supertodolist.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import mobile.doan.supertodolist.dto.response.ResFriendDTO;
import mobile.doan.supertodolist.model.Friend;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface FriendMapper {

    @Mapping(source = "sender", target = "sender")
    @Mapping(source = "receiver", target = "receiver")
    ResFriendDTO toResFriendDTO(Friend friend);
}