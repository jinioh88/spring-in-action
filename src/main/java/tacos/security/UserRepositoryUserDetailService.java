package tacos.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tacos.User;
import tacos.data.UserRepository;

@Service
@RequiredArgsConstructor
public class UserRepositoryUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernmae) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernmae);
        if(user != null) {
            return user;
        }
        throw new UsernameNotFoundException("User '" + usernmae + "' not found");
    }
}
