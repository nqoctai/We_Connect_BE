package mobile.doan.supertodolist.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.mapper.UserMapper;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.repository.UserRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
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
}
