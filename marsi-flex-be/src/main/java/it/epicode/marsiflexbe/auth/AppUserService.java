package it.epicode.marsiflexbe.auth;

import it.epicode.marsiflexbe.users.AppUserMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Validated
public class AppUserService {
    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public UserResponse registerUser(@Valid RegisterRequest registerRequest) {
        Set<Role> roles = Set.of(Role.ROLE_USER);
        return registerUser(registerRequest.getName(),  registerRequest.getEmail(), registerRequest.getPassword(), roles);
    }

    public UserResponse registerUser(String name, String username, String password, Set<Role> roles) {
        if (appUserRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setName(name);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setRoles(roles);

        appUserRepository.save(appUser);
        UserResponse response = appUserMapper.toLoginUserResponse(appUser);

        return  response;
    }


    public List<UserResponse> findAllUsers() {
        List<AppUser> appUsers = appUserRepository.findAll();

        return appUserMapper.toLoginUserResponseList(appUsers);
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public AuthResponse authenticateUser(@Valid LoginRequest loginRequest) {
        return authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
    }

    public AuthResponse authenticateUser(String username, String password)  {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            AppUser appUser = loadUserByUsername(username);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            AuthResponse authResponse = new AuthResponse();
            authResponse.setAccessToken(jwtTokenUtil.generateToken(userDetails) );
            UserResponse userResponse = appUserMapper.toLoginUserResponse(appUser);
            authResponse.setUser(userResponse);

            return authResponse;

        } catch (AuthenticationException e) {
            throw new SecurityException("Credenziali non valide", e);
        }
    }


    public AppUser loadUserByUsername(String username)  {
        AppUser appUser = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));


        return appUser;
    }
}
