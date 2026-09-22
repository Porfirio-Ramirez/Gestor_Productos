package Proyecto.Gestor_Productos.Security;

import Proyecto.Gestor_Productos.Models.AppUser;
import Proyecto.Gestor_Productos.Repositories.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * Implementación de {UserDetailsService} requerida por Spring Security
 * para autenticar usuarios. Adapta {AppUser} (la entidad de dominio)
 * a {UserDetails} (el contrato que Spring Security entiende),
 * usando la clase {@code User} propia del framework en vez de hacer que
 * AppUser implemente UserDetails directamente — así la entidad de base de
 * datos no se mezcla con responsabilidades de seguridad.
 */
@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca al usuario por email y lo adapta a UserDetails. {@code .roles(...)}
     * antepone automáticamente el prefijo "ROLE_" requerido por
     * {@code hasRole(...)} en las reglas de autorización.
     * @throws UsernameNotFoundException si no existe un usuario con ese email.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       AppUser appUser = userRepository.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found"));

       return  User.withUsername(appUser.getEmail())
               .password(appUser.getPassword())
               .roles(appUser.getRole().name())
               .build();
    }
}
