package muiz.demo.abac.security;

import muiz.demo.abac.data.entities.User;
import muiz.demo.abac.data.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("authorizationPolicies")
public class AuthorizationPolicies {

    private UserRepository userRepository;

    @Autowired
    public AuthorizationPolicies(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean hasAccess() {
        List<User> allUsers = userRepository.findAll();
        allUsers.forEach(u -> {
            System.out.println(u.getName());
        });
        return true;
    }

}
