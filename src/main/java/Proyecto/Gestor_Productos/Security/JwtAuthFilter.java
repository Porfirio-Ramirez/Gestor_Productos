package Proyecto.Gestor_Productos.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Filtro que se ejecuta una vez por cada petición HTTP (antes de llegar al
 * controller), encargado de leer el JWT del header {@code Authorization},
 * validarlo y, si es válido, autenticar al usuario en el contexto de
 * seguridad de Spring para esa petición.
 * <p>
 * Se registra en {SecurityConfig} justo antes de
 * {@code UsernamePasswordAuthenticationFilter}, para que la autenticación
 * por JWT ocurra antes que cualquier otro mecanismo de autenticación.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailService userDetailService;

    public JwtAuthFilter(JwtService jwtService, UserDetailService userDetailService) {
        this.jwtService = jwtService;
        this.userDetailService = userDetailService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Sin header, o sin el formato "Bearer <token>": se deja pasar sin
        // autenticar; será la regla de autorización de SecurityConfig la
        // que decida si esa ruta requiere estar logueado o no.
        if (header == null || !header.startsWith("Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }
        // substring(7) quita literalmente "Bearer " (7 caracteres, incluido
        // el espacio); depende de que el startsWith de arriba valide ese
        // mismo formato exacto, para no cortar mal el token
        String token = header.substring(7);

        // Solo autentica si el token es válido y todavía no hay una
        // autenticación puesta en el contexto para esta petición.
        if (jwtService.isvalid(token) &&
                SecurityContextHolder.getContext().getAuthentication() == null){
            String email = jwtService.getEmail(token);
            UserDetails userDetail = userDetailService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetail, null, userDetail.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

        }

        filterChain.doFilter(request, response);
    }
}
