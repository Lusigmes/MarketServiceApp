package portifolio.market_service.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
// @Component
public class JwtAuthService {
    @Value("${security.jwt.secret-key}")
    private String secretKey;
    
    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    private Key getSignInKey(){
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException  e) {
            return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));        }
    }

    public Claims extractAllClaims(String token){
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolve){
        final Claims claims = extractAllClaims(token);
        return claimsResolve.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public long getExpirationTime(){
        return jwtExpiration;
    }

    public String generateToken(UserDetails user){
        return generateToken(new HashMap<>(), user);
    }
    public String generateToken(Map<String, Object> extraClaims, UserDetails user){
        return buildToken(extraClaims, user, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails user, long expiration){
        return Jwts 
            .builder()
            .setClaims(extraClaims)
            .setSubject(user.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean isValidToken(String token, UserDetails user){
        final String email = extractUsername(token);
        return (email.equals(user.getUsername())) && !isTokenExpired(token); 
    }
    
    public boolean isValidOnlyToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}
