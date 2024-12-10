package vn.hoidanit.jobhunter.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.Collections;
@Component("userDetailsService")
public class UserDetailsCustom implements UserDetailsService {
    private final UserService userService;

    public UserDetailsCustom(UserService userService) {
        this.userService = userService;
    }

    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        vn.hoidanit.jobhunter.domain.User user = this.userService.handleGetUserByUserName(userName);
        if(user == null) {
            throw  new UsernameNotFoundException("UserName/Password không hợp lệ");
        }

        return new User(user.getEmail(),
                        user.getPassword(),
                       Collections.singletonList(new SimpleGrantedAuthority(("ROLE_USER"))));

    }
}
