package Tfast_Rmoney.Givvy.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Disables CSRF protection.
            //
            // CSRF protection is useful for apps that use browser sessions/cookies.
            // But this app uses JWT tokens instead of server sessions.
            // Since every request must send its own JWT, CSRF is usually disabled.
            .csrf(csrf -> csrf.disable())

            // Controls how Spring Security handles sessions.
            .sessionManagement(management -> management

                    // STATELESS means Spring will not store login information
                    // in a server-side session.
                    //
                    // In other words:
                    // Spring will not "remember" the user after login.
                    //
                    // Instead, every request must send a valid JWT token again.
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // This section controls which routes are public
            // and which routes require authentication.
            .authorizeHttpRequests(authorize -> authorize

                    // These POST routes are public.
                    //
                    // POST /users:
                    // Allows a new user to create an account.
                    //
                    // POST /users/login:
                    // Allows a user to log in and receive a JWT token.
                    //
                    // These must be public because the user does not have a token yet.
                    .requestMatchers(
                            HttpMethod.POST,
                            "/users",
                            "/users/login"
                    ).permitAll()

                    // This GET route is public.
                    //
                    // /interests/{itemid}
                    //
                    // {itemid} means the value can change.
                    // Example:
                    // /interests/1
                    // /interests/25
                    // /interests/100
                    .requestMatchers(
                            HttpMethod.GET,
                            "/interests/{itemid}"
                    ).permitAll()

                    // This GET route is public.
                    //
                    // Allows anyone to view available appointment times.
                    .requestMatchers(
                            HttpMethod.GET,
                            "/appointments/available-times"
                    ).permitAll()

                    // These GET routes are public.
                    //
                    // They are used for Swagger documentation.
                    // Swagger lets you view and test your API in the browser.
                    .requestMatchers(
                            HttpMethod.GET,
                            "/swagger-ui/*",
                            "/v3/api-docs",
                            "/v3/api-docs/*"
                    ).permitAll()

                    // Any request that was not listed above must be authenticated.
                    //
                    // This means the user must send a valid JWT token.
                    //
                    // Example:
                    // If you have GET /users/profile and it is not listed as permitAll(),
                    // then Spring will require a valid JWT before allowing access.
                    .anyRequest().authenticated()
            )

            // Adds our custom JWT filter before Spring Security's default
            // username/password authentication filter.
            //
            // This means:
            // Before Spring tries its normal login process,
            // our JwtAuthFilter gets a chance to check the JWT token first.
            //
            // If the JWT is valid, JwtAuthFilter saves the user into
            // Spring Security's context.
            .addFilterBefore(
                    jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        // Builds the security rules above and gives them to Spring Security.
        return http.build();
    }
}