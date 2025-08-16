package com.project.back_end.services;

import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenService {

    private String SECRET_KEY;
        
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    public TokenService(@Value("${jwt.secret}") String SECRET_KEY, AdminRepository adminRepository, DoctorRepository doctorRepository, PatientRepository patientRepository){
        this.SECRET_KEY = SECRET_KEY;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    private SecretKey getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String identifier){
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000);

        return Jwts.builder()
        .subject(identifier)
        .issuedAt(new Date())
        .expiration(expiration)
        .signWith(getSigningKey(), Jwts.SIG.HS256)
        .compact();
    }

    public String extractEmail(String token){
        return Jwts.parser()
            .verifyWith(getSigningKey()) 
            .build()  
            .parseSignedClaims(token)  
            .getPayload()  
            .getSubject();
    }

    public String extractIdentifier(String token){
        return extractEmail(token);
    }

    public boolean validateToken(String token, String role){
        try {
            String identifier = extractIdentifier(token);
            boolean exists = switch (role.toLowerCase()) {
                case "admin" -> adminRepository.existsByUsername(identifier);
                case "doctor" -> doctorRepository.existsByEmail(identifier);
                case "patient" -> patientRepository.existsByEmail(identifier);
                default -> false;
            };

            if(!exists) return false;

            return true;
        } catch (Exception e) {
            logger.error("Exception in validateToken", e);
            return false;
        }        
    }
}
