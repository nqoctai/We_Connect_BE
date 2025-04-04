package mobile.doan.supertodolist.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import mobile.doan.supertodolist.dto.response.ResUserDTO;
import mobile.doan.supertodolist.mapper.UserMapper;
import mobile.doan.supertodolist.model.User;
import mobile.doan.supertodolist.services.UserService;
import mobile.doan.supertodolist.util.error.AppException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SecurityUtil {
    final JwtEncoder jwtEncoder;
    final UserService userService;
    final UserMapper userMapper;

    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

    @Value("${nqoctai.jwt.base64-secret}")
    String jwtKey;

    @Value("${nqoctai.jwt.access-token-validity-in-seconds}")
    long accessTokenExpiration;

    @Value("${nqoctai.jwt.refresh-token-validity-in-seconds}")
    long refreshTokenExpiration;

    public String createAccessToken(String email) throws AppException {
        User user = this.userService.getUserByEmail(email);
        if (user == null) {
            throw new AppException(email + " not found");
        }
        ResUserDTO resUserDTO = userMapper.toResUserDTO(user);

        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // hardcode permission (for testing)
        List<String> listAuthority = new ArrayList<String>();
        listAuthority.add("ROLE_USER_CREATE");
        listAuthority.add("ROLE_USER_UPDATE");

    // @formatter:off
    JwtClaimsSet claims = JwtClaimsSet.builder()
    .issuedAt(now)
    .expiresAt(validity)
    .subject(email)
    .claim("user", resUserDTO)
    .claim("permission", listAuthority)
    .build();

    JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
}
