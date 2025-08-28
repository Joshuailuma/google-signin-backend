package com.googlesignin.Backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.googlesignin.Backend.entity.Token;
import com.googlesignin.Backend.entity.Users;
import com.googlesignin.Backend.models.response.LoginResponseDto;
import com.googlesignin.Backend.repository.TokenRepository;
import com.googlesignin.Backend.repository.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final TokenRepository tokenRepository;
    private final UsersRepository usersRepository;
    private final Long expiration = 400000000L;

    @Value("${secret.jwt.token}")
    private String SECRET_KEY;

    @Value("${google.client.id}")
    String googleClientId;

    public String extractUsername(String jwtToken) {
        return extractClaims(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaims(String jwtToken, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwtToken);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getSignInKey()).build()
                .parseSignedClaims(jwtToken).getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyByte = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public boolean isTokenValidated(String jwtToken, UserDetails userDetails) throws Exception {
        String username = extractUsername(jwtToken);
        Optional<Token> optionalToken = tokenRepository.findByToken(jwtToken);
        Token token;
        if (optionalToken.isPresent()) {
            token = optionalToken.get();
            if (token.isRevoked()) {
                throw new Exception("Token has been revoked");
            }
        } else {
            throw new Exception("Token not found");
        }

        return (userDetails.getUsername().equals(username) && !isExpired(jwtToken));
    }

    private boolean isExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaims(jwtToken, Claims::getExpiration);
    }

    public String generateToken(UserDetails userDetails) {
        Collection<? extends SimpleGrantedAuthority> authorities = (Collection<? extends SimpleGrantedAuthority>) userDetails.getAuthorities();

        List<String> roleNames = new ArrayList<>();

        for (SimpleGrantedAuthority authority : authorities) {
            roleNames.add(authority.getAuthority());
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roleNames);
        return buildToken(claims, userDetails, expiration);
    }

    private String buildToken(Map<String, Object> claims, UserDetails userDetails, Long expiration) {
        return Jwts.builder()
                .claim("roles", claims.get("roles"))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .subject(userDetails.getUsername())
                .signWith(SignatureAlgorithm.HS256, getSignInKey()).compact();
    }

    public Long getJwtExpiration() {
        return expiration;
    }

    public void saveToken(Users user, String jwtToken) {
        Token token = Token.builder()
                .token(jwtToken)
                .user(user)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
    }

    public LoginResponseDto verifyGoogleToken(String idTokenString) {
        log.info("====Verifying Google token with id {}", idTokenString);

        String jwtToken;

        GoogleIdTokenVerifier verifier = new  GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory()).
                setAudience(Collections.singletonList(googleClientId)).build();
        try {
           GoogleIdToken googleIdToken = verifier.verify(idTokenString);
           if(googleIdToken != null){
               GoogleIdToken.Payload payload = googleIdToken.getPayload();
               String email = payload.getEmail();
               String firstName = (String) payload.get("given_name");
               String lastName = (String) payload.get("family_name");

              Optional<Users> optionalUser = usersRepository.findByEmail(email);

              Users user;
              if(optionalUser.isEmpty()){
                  user = Users.builder()
                          .email(email)
                          .firstName(firstName)
                          .lastName(lastName)
                          .username(email)
                          .build();
                  usersRepository.save(user);
              } else {
                  user = optionalUser.get();
              }

            jwtToken = generateToken(user);
              saveToken(user, jwtToken);
               log.info("===Verification successful");
               return LoginResponseDto.builder()
                       .message("Login successful")
                       .accessToken(jwtToken)
                       .expiryTime(getJwtExpiration().toString())
                       .build();
           }
           else {

              throw new RuntimeException("Authentication failed");
           }

        } catch (Exception e) {
            log.info("===Verification failed===");
            throw new RuntimeException(e);
        }
    }

}
