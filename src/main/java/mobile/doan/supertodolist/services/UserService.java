package mobile.doan.supertodolist.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.dto.response.ResUserWithFriendStatusDTO;
import mobile.doan.supertodolist.mapper.UserMapper;
import mobile.doan.supertodolist.model.Friend;
import mobile.doan.supertodolist.model.Friend.FriendStatus;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.repository.FriendRepository;
import mobile.doan.supertodolist.repository.UserRepository;
import mobile.doan.supertodolist.util.SecurityUtil;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    FriendRepository friendRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public ResUserDTO createUser(ReqUserDTO rqUser) {
        String hashPassword = passwordEncoder.encode(rqUser.getPassword());
        User user = userMapper.toUser(rqUser);
        user.setPassword(hashPassword);
        user = userRepository.save(user);

        ResUserDTO resUserDTO = userMapper.toResUserDTO(user);
        return resUserDTO;
    }

    @Transactional
    public ResPaginationDTO getAllUserWithPagination(Specification<User> spec, Pageable pageable) {
        // Get current logged-in user ID
        Optional<String> currentUserEmailOpt = SecurityUtil.getCurrentUserLogin();
        Long currentUserId = null;

        if (currentUserEmailOpt.isPresent()) {
            User currentUser = userRepository.findByEmail(currentUserEmailOpt.get()).orElse(null);
            if (currentUser != null) {
                currentUserId = currentUser.getId();
            }
        }

        final Long finalCurrentUserId = currentUserId;

        // Find all users with pagination
        Page<User> pageUsers = this.userRepository.findAll(spec, pageable);
        ResPaginationDTO rs = new ResPaginationDTO();
        ResPaginationDTO.Meta meta = new ResPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageUsers.getTotalPages());
        meta.setTotal(pageUsers.getTotalElements());
        rs.setMeta(meta);

        List<User> listUsers = pageUsers.getContent();

        // Map users to DTOs with friendship info
        List<ResUserWithFriendStatusDTO> listResUserDTO = listUsers.stream().map(user -> {
            boolean isFriend = false;
            boolean requestSent = false;
            boolean requestReceived = false;
            Long friendsId = null;

            // If current user is logged in, check friendship and request status
            if (finalCurrentUserId != null && user.getId() != finalCurrentUserId) {
                // Check if users are friends
                isFriend = friendRepository.areFriends(finalCurrentUserId, user.getId());

                // Get any friend record between these two users regardless of status
                User currentUser = userRepository.findById(finalCurrentUserId).orElse(null);
                if (currentUser != null) {
                    // Check if current user is the sender
                    List<Friend> friendAsSender = friendRepository.findBySenderAndReceiver(currentUser, user);
                    if (!friendAsSender.isEmpty()) {
                        friendsId = friendAsSender.get(0).getId();
                    } else {
                        // If not found as sender, check as receiver
                        List<Friend> friendAsReceiver = friendRepository.findBySenderAndReceiver(user, currentUser);
                        if (!friendAsReceiver.isEmpty()) {
                            friendsId = friendAsReceiver.get(0).getId();
                        }
                    }
                }

                // Check friend request status if they're not already friends
                if (!isFriend) {
                    requestSent = friendRepository.hasRequestSent(finalCurrentUserId, user.getId());
                    requestReceived = friendRepository.hasRequestReceived(finalCurrentUserId, user.getId());
                }
            }

            return userMapper.toResUserWithFriendStatusDTO(user, isFriend, requestSent, requestReceived, friendsId);
        }).collect(Collectors.toList());

        rs.setResult(listResUserDTO);
        return rs;
    }
}
