package com.bhumi.eventscoring_backend;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
// create one instance of this class, and let anyone @Autowired it in
public class JwtUtil {

    private final SecretKey key = Keys.hmacShaKeyFor(
            "this-is-a-very-long-secret-key-change-it-later-123456".getBytes()
    );
    /* This is the most conceptually important line. Think of key as a secret stamp only your server owns.
    Every token you create gets "stamped" with this key. Later, when someone sends a token back to you,
    you check: "does this token's stamp match my secret key?" If yes, the token is genuine — it
     could only have been created by your server, since no one else has this exact key.
    If someone tries to fake a token without knowing this key, the stamp won't match, and you'll reject it.
    .getBytes() just converts your secret text into the raw byte format the signing algorithm needs internally */

    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours
    // This defines how long a token stays valid before it "expires" and the user has to log in again

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)        // organizer , user etc.
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();         // packages everything into the final compact string format
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)     // unpacks/decodes the token's contents
                .getPayload()
                .getSubject();                // pulls out the email specifically
    }

    public String extractRole(String token) {
        return (String) Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role");
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
// The JWT contains a digital signature.
//That signature is created using the secret key.
//The server uses the same secret key later to verify that signature.