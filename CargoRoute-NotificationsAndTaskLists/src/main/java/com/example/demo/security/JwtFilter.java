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
 
 
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {
	
	    String auth = request.getHeader("Authorization");
	    System.out.println("Im inside jwtfiler");
	    
	
	    if(auth != null && auth.startsWith("Bearer ")){
	    	System.out.println("Im jwtfilter, checking auth");
	        String token = auth.substring(7);
	
	        JwtUtil util = new JwtUtil();
	
	        String username = util.extractUsername(token);
	        String role = util.extractRole(token);
	
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