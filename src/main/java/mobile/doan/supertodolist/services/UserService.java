package mobile.doan.supertodolist.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ResPaginationDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.dto.response.ResUserWithFriendStatusDTO;
import mobile.doan.supertodolist.mapper.UserMapper;
import mobile.doan.supertodolist.model.Post;
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

            // If current user is logged in, check friendship and request status
            if (finalCurrentUserId != null && user.getId() != finalCurrentUserId) {
                isFriend = friendRepository.areFriends(finalCurrentUserId, user.getId());

                // Only check request status if they're not already friends
                if (!isFriend) {
                    requestSent = friendRepository.hasRequestSent(finalCurrentUserId, user.getId());
                    requestReceived = friendRepository.hasRequestReceived(finalCurrentUserId, user.getId());
                }
            }

            return userMapper.toResUserWithFriendStatusDTO(user, isFriend, requestSent, requestReceived);
        }).collect(Collectors.toList());

        rs.setResult(listResUserDTO);
        return rs;
    }
}
