package com.affy.learningManagementSystem.utils;

import com.affy.learningManagementSystem.dtos.student.StudentDetailDto;
import com.affy.learningManagementSystem.dtos.superAdmin.SuperAdminDetailDto;
import com.affy.learningManagementSystem.dtos.teacher.TeacherDetailDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractPhoneNumber(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /* Method generates a JWT token with employeeDetails as claim. */
//    public String generateTokenForEmployee(String email, EmployeeDetailDto employeeDetails) {
//
//        /* Storing claim in a hashmap. */
//        Map<String, Object> claims = new HashMap<>() {{
//            put("claims", employeeDetails);
//        }};
//
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(email)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(SignatureAlgorithm.HS256, secret)
//                .compact();
//    }

    /* Method generates a JWT token with superAdmin and admin as claim. */
    public String generateTokenForSuperAdmin(String email, SuperAdminDetailDto superAdminDetail) {

        /* Storing claim in a hashmap. */
        Map<String, Object> claims = new HashMap<>() {{
            put("claims", superAdminDetail);
        }};

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /* Method generates a JWT token with teachers as claim. */
    public String generateTokenForTeacher(String email, TeacherDetailDto teacherDetailDto) {

        /* Storing claim in a hashmap. */
        Map<String, Object> claims = new HashMap<>() {{
            put("claims", teacherDetailDto);
        }};

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /* Method generates a JWT token with teachers as claim. */
    public String generateTokenForStudent(String email, StudentDetailDto studentDetailDto) {

        /* Storing claim in a hashmap. */
        Map<String, Object> claims = new HashMap<>() {{
            put("claims", studentDetailDto);
        }};

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token, String phoneNumber) {
        final String tokenPhoneNumber = extractPhoneNumber(token);
        return (tokenPhoneNumber.equals(phoneNumber) && !isTokenExpired(token));
    }
}
