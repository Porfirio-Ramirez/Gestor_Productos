package Proyecto.Gestor_Productos.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
/**
 * Configuración central de Spring Security: define qué rutas son públicas,
 * cuáles requieren rol ADMIN, la política de sesión (stateless, ya que la
 * autenticación se maneja por JWT en cada request), CORS, y los beans de
 * cifrado de contraseñas y autenticación.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Define la cadena de filtros de seguridad: reglas de autorización por
     * ruta/método HTTP, y el filtro JWT insertado antes del filtro estándar
     * de Spring Security.
     * <p>
     * El orden de las reglas importa: Spring Security aplica la primera que
     * coincida, así que la excepción específica de DELETE sobre
     * {@code /api/auth/users/**} va antes de la regla genérica
     * {@code /api/auth/**} — si estuviera después, quedaría sin efecto,
     * porque la regla genérica ya habría capturado la petición primero.
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // public routes
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/auth/**").permitAll()


                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/brand/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/v3/api-docs").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/category/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/category/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/brand/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/brand/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/brand/**").hasRole("ADMIN")
                        .anyRequest().authenticated()

                )
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    /**
     * Configura CORS para permitir peticiones desde el frontend (orígenes
     * de desarrollo local). {@code allowCredentials = true} exige listar
     * orígenes exactos en vez de un comodín "*" (Spring lo rechaza en
     * runtime si se combinan, y sería además un riesgo de seguridad real).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /** Bean de cifrado de contraseñas usado en registro (encode) y login
     * (matches, vía AuthenticationManager). */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /** Expone el AuthenticationManager de Spring Security como bean, para
     *  poder inyectarlo en AuthService. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration confi)
            throws Exception{
        return confi.getAuthenticationManager();
    }
}
