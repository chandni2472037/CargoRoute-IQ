package com.example.demo.security;
 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
 
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Collections;
 
@Component	
public class JwtFilter extends OncePerRequestFilter{
 

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	
	    String auth = request.getHeader("Authorization");
	    System.out.println("Im inside jwtfiler");
	    
	    
	     String path = request.getRequestURI();
	     // Skip JWT for auth APIs & preflight
	     if (request.getMethod().equalsIgnoreCase("OPTIONS")
	         || path.startsWith("/cargoRoute/auth")) {

	         filterChain.doFilter(request, response);
	         return;
	     }

	    
	
	    if(auth != null && auth.startsWith("Bearer ")){
	        String token = auth.substring(7);
	

	        String username = jwtUtil.extractUsername(token);
	        String role = jwtUtil.extractRole(token);
	        Long userId = jwtUtil.extractUserId(token); 

	        request.setAttribute("userId", userId); 
	
	        //Create AUTH object
	        UsernamePasswordAuthenticationToken authentication =
	                new UsernamePasswordAuthenticationToken(
	                        username,
	                        null,
	                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
	                );
	
	        //SET into security context
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	    }
	    
	    System.out.println("auth check done");
	
	    
	    filterChain.doFilter(request, response); // allow request to continue to controller
	}
}