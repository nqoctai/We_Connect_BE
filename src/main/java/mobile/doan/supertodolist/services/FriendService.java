package mobile.doan.supertodolist.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqFriendDTO;
import mobile.doan.supertodolist.dto.response.ResFriendDTO;
import mobile.doan.supertodolist.mapper.FriendMapper;
import mobile.doan.supertodolist.model.Friend;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.model.Friend.FriendStatus;
import mobile.doan.supertodolist.repository.FriendRepository;
import mobile.doan.supertodolist.repository.UserRepository;
import mobile.doan.supertodolist.util.SecurityUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class FriendService {

    FriendRepository friendRepository;
    UserRepository userRepository;
    FriendMapper friendMapper;

    public ResFriendDTO sendFriendRequest(ReqFriendDTO reqFriendDTO) {
        // Get current logged-in user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get target user
        User receiverUser = userRepository.findById(reqFriendDTO.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver user not found"));

        // Check if friend request already exists
        if (friendRepository.existsBySenderAndReceiverAndStatus(currentUser, receiverUser, FriendStatus.PENDING) ||
                friendRepository.existsBySenderAndReceiverAndStatus(currentUser, receiverUser, FriendStatus.ACCEPTED)) {
            throw new RuntimeException("Friend request already exists or users are already friends");
        }

        // Create new friend request
        Friend friend = new Friend();
        friend.setSender(currentUser);
        friend.setReceiver(receiverUser);
        friend.setStatus(FriendStatus.PENDING);

        friend = friendRepository.save(friend);

        return friendMapper.toResFriendDTO(friend);
    }

    public ResFriendDTO acceptFriendRequest(Long friendRequestId) {
        // Get current logged-in user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get friend request
        Friend friend = friendRepository.findById(friendRequestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Verify that current user is the receiver
        if (friend.getReceiver().getId() != currentUser.getId()) {
            throw new RuntimeException("Not authorized to accept this friend request");
        }

        // Update friend request status
        friend.setStatus(FriendStatus.ACCEPTED);
        friend = friendRepository.save(friend);

        return friendMapper.toResFriendDTO(friend);
    }

    public ResFriendDTO rejectFriendRequest(Long friendRequestId) {
        // Get current logged-in user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get friend request
        Friend friend = friendRepository.findById(friendRequestId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Verify that current user is the receiver
        if (friend.getReceiver().getId() != currentUser.getId()) {
            throw new RuntimeException("Not authorized to reject this friend request");
        }

        // Update friend request status
        friend.setStatus(FriendStatus.REJECTED);
        friend = friendRepository.save(friend);

        return friendMapper.toResFriendDTO(friend);
    }

    public List<ResFriendDTO> getReceivedFriendRequests() {
        // Get current logged-in user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Friend> pendingRequests = friendRepository.findByReceiverAndStatus(currentUser, FriendStatus.PENDING);
        return pendingRequests.stream()
                .map(friendMapper::toResFriendDTO)
                .collect(Collectors.toList());
    }

    public List<ResFriendDTO> getFriends() {
        // Get current logged-in user
        String currentUserEmail = SecurityUtil.getCurrentUserLogin().orElseThrow(
                () -> new RuntimeException("User not authenticated"));
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get friend requests where user is sender or receiver and status is ACCEPTED
        List<Friend> friends = friendRepository.findBySenderIdOrReceiverIdAndStatus(
                currentUser.getId(), currentUser.getId(), FriendStatus.ACCEPTED);

        return friends.stream()
                .map(friendMapper::toResFriendDTO)
                .collect(Collectors.toList());
    }
}