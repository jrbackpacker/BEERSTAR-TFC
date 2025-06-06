package beerstar.config;

import beerstar.security.JwtAuthenticationFilter;
import beerstar.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // *** AÑADE ESTA LÍNEA TEMPORALMENTE PARA DEPURAR CORS ***
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permitir todas las solicitudes OPTIONS
                        // ******************************************************

                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/public/providers/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/beers").permitAll()
                        .requestMatchers(HttpMethod.GET, "/beers/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/batches").permitAll()
                        .requestMatchers(HttpMethod.GET, "/batches/{id}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasAnyAuthority("CLIENT", "PROVIDER")
                        .requestMatchers(HttpMethod.POST, "/orders").hasAnyAuthority("CLIENT", "PROVIDER, ADMIN")
                        .requestMatchers(HttpMethod.GET, "/orders/user/{userId}").hasAnyAuthority("CLIENT", "PROVIDER")
                        .requestMatchers(HttpMethod.POST, "/beers/**").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.PUT, "/beers/**").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/beers/**").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.GET, "/beers/my-beers").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.POST, "/batches/**").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.PUT, "/batches/**").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.DELETE, "/batches/**").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.GET, "/batches/my-batches").hasAuthority("PROVIDER")
                        .requestMatchers(HttpMethod.GET, "/orders/my-orders-as-provider").hasAuthority("PROVIDER")
                        .requestMatchers("/users/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}