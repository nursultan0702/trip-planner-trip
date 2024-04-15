package com.tripplannertrip.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtTokenFilter extends OncePerRequestFilter {


  private final String secretKey;

  public JwtTokenFilter(String secretKey) {
    this.secretKey = secretKey;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String token = request.getHeader("Authorization");
    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7);

      Key key = Keys.hmacShaKeyFor(secretKey.getBytes());
      Jws<Claims> claimsJws = Jwts.parser()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
      Claims claims = claimsJws.getPayload();
      String subject = claims.getSubject();

      User principal = new User(subject, "", new ArrayList<>());
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
          principal, null, new ArrayList<>()
      );
      auth.setDetails(subject);

      SecurityContextHolder.getContext().setAuthentication(auth);
    }
    filterChain.doFilter(request, response);
  }
}
