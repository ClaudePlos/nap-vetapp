package pl.kskowronski.security;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.kskowronski.data.Role;
import pl.kskowronski.data.entity.admin.User;
import pl.kskowronski.data.entity.global.NapUser;
import pl.kskowronski.data.service.UserRepository;
import pl.kskowronski.data.service.global.NapUserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NapUserRepo napUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = null;
        try {
            user = Optional.ofNullable(userRepo.findByUsername(username));
            user.get().setPassword(getMd5(user.get().getPassword()));
            if (user.get().getPrcDgKodEk().equals("EK04")) {// check, maybe he is in EK04
                user.get().setPassword("");
            }
        } catch (Exception ex){
            Optional<NapUser> napUser = napUserRepo.findByUsername(username);
            if (napUser.isPresent()) {
                user = userRepo.findById(napUser.get().getPrcId());
                user.get().setPassword(napUser.get().getPassword());
            }
        }

        if (user.get() == null) {
            throw new UsernameNotFoundException("Could not find user with this username and pass");
        }
        user.get().setRoles(Collections.singleton(Role.USER));


        if (user.get().getPrcId() == 115442 || user.get().getPrcId() == 279069  || user.get().getPrcId() == 340372 ) {
            user.get().setRoles(Collections.singleton(Role.ADMIN));
        }



        //return new MyUserDetails(user.get());

        return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(),
                getAuthorities(user.get()));
    }

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());

    }

    private static String getMd5(String input) {
        try {
            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);
            return null;
        }
    }

}
