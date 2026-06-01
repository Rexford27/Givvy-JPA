package Tfast_Rmoney.Givvy.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userid = null;
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            //locates and select the jwt token 
            token = authHeader.substring(7);
            if(jwtService.isValid(token))
                //if the token is a vlaid token not expired or crated by server 
            	userid = jwtService.getSubject(token);
                //the jwtService will return the id from the token
        }
        //i dont understand this part help me understand it line by line
    // If the JWT gave us a user ID, and Spring Security has not already authenticated
    // a user for this request, then we manually authenticate the user.
    if (userid != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        // Create a UserDetails object using the user ID from the JWT.
        // Spring Security uses UserDetails to represent the current user.
        AuctionUserDetails userDetails = new AuctionUserDetails(userid);

        // Create an Authentication object for Spring Security.
        // userDetails = the authenticated user
        // null = no password is needed because the JWT already proved the user's identity
        // userDetails.getAuthorities() = the user's roles or permissions
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // Add extra request information, such as IP address and session details,
        // to the authentication object.
        authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );

        // Save the authentication object into Spring Security's context.
        // After this line, Spring Security treats this request as authenticated.
    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
}
// Continue the request through the rest of the filter chain.
// This allows the request to move on to the next security filter
// and eventually reach the controller if everything is valid.
filterChain.doFilter(request, response);    }
}