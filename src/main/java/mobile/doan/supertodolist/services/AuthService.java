package mobile.doan.supertodolist.services;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.request.ReqLoginDTO;
import mobile.doan.supertodolist.dto.request.ReqUserDTO;
import mobile.doan.supertodolist.dto.response.ResLoginDTO;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.mapper.UserMapper;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.util.SecurityUtil;
import mobile.doan.supertodolist.util.error.AppException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

    AuthenticationManagerBuilder authenticationManagerBuilder;
    UserService userService;
    UserMapper userMapper;
    SecurityUtil securityUtil;
    PasswordEncoder passwordEncoder;

    public ResLoginDTO login(ReqLoginDTO rqLogin) throws AppException {
        UsernamePasswordAuthenticationToken loginToken = new UsernamePasswordAuthenticationToken(rqLogin.getUsername(),
                rqLogin.getPassword());

        Authentication authentication = this.authenticationManagerBuilder.getObject().authenticate(loginToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userDB = userService.getUserByEmail(rqLogin.getUsername());

        if (userDB == null) {
            throw new AppException("User not found");
        }

        ResUserDTO user = userMapper.toResUserDTO(userDB);
        String accessToken = securityUtil.createAccessToken(userDB.getEmail());
        ResLoginDTO res = ResLoginDTO.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
        return res;
    }

    public ResUserDTO register(ReqUserDTO req) throws AppException {
        User user = userService.getUserByEmail(req.getEmail());
        if (user != null) {
            throw new AppException("Email already exists");
        }
        ResUserDTO resUserDTO = userService.createUser(req);
        return resUserDTO;

    }

    public ResUserDTO getCurrentUser() throws AppException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User userDB = userService.getUserByEmail(email);
        if (userDB == null) {
            throw new AppException("User not found");
        }
        ResUserDTO res = userMapper.toResUserDTO(userDB);
        return res;
    }

    public ResLoginDTO getRefreshToken(String email) throws AppException {
        User userDB = userService.getUserByEmail(email);
        if (userDB == null) {
            throw new AppException("User not found");
        }

        ResUserDTO user = userMapper.toResUserDTO(userDB);
        String accessToken = securityUtil.createAccessToken(userDB.getEmail());
        ResLoginDTO res = ResLoginDTO.builder()
                .accessToken(accessToken)
                .user(user)
                .build();
        return res;
    }
}
