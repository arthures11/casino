package com.bryja.casino.controllers;


import com.bryja.casino.classes.Message;
import com.bryja.casino.classes.Role;
import com.bryja.casino.classes.User;
import com.bryja.casino.repository.MessageRepository;
import com.bryja.casino.repository.RoleRepository;
import com.bryja.casino.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {
    private final UserRepository repository;
    private final RoleRepository rolerep;

    private final MessageRepository messageRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository, RoleRepository rolerep, MessageRepository messageRepository) {
        this.repository = repository;
        this.rolerep = rolerep;
        this.messageRepository = messageRepository;
    }

    @GetMapping(value ="/user/add", consumes = {"*/*"})
    public void registerUser(@AuthenticationPrincipal OAuth2User principal, HttpServletRequest req, HttpServletResponse resp) {
        String n = principal.getAttribute("name");
        String n2 = principal.getAttribute("email");
        if(n2==null){
            // SecurityContextHolder.getContext().setAuthentication(null);
            ///  throw new EmailNullException(n, req, resp);
        }
        User a = new User(n,n2,passwordEncoder.encode("123"), 500);
        a.setRoles(Arrays.asList(rolerep.findByName("ROLE_USER")));
        if (emailExists(a.getEmail())) {
            try {
                Role role = rolerep.findByName("ROLE_USER");
                // String token = generateToken(n2,Collections.singletonList(role.getName()));
                // return new ResponseEntity<>(new BearerToken(token , "Bearer "), HttpStatus.OK);
                resp.sendRedirect(req.getContextPath()+"/");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            repository.save(a);
            Role role = rolerep.findByName("ROLE_USER");
            //String token = generateToken(n2,Collections.singletonList(role.getName()));
            //model.addAttribute("attribute", "forwardWithForwardPrefix");
            //  return new ResponseEntity<>(new BearerToken(token , "Bearer "), HttpStatus.OK);
            try {
                resp.sendRedirect(req.getContextPath()+"/");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    @GetMapping(value="/user", consumes = {"*/*"})
    public User user(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));

        return usr;
    }
    @GetMapping(value="/chathistory", consumes = {"*/*"})
    public List<Message> getChatMessages(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Message> msgs = messageRepository.findFirst20ByOrderByIdDesc();

        for(int i=0;i<msgs.size();i++){
            Optional<User> g = repository.findById(msgs.get(i).getUser().getId());
            msgs.get(i).setAvatar(g.get().getAvatar());
            msgs.get(i).setAuthor_name(g.get().getName());
        }
         return msgs;
    }

    public String checkmail(Object authentication){
        if (authentication instanceof DefaultOidcUser) {       //klasa która powstaje przy social loginie
            DefaultOidcUser oauth2User = (DefaultOidcUser) authentication;
            return oauth2User.getAttribute("email");
        } else if (authentication instanceof UserDetails) {    //zwykla klasa posiadająca dane z bazy
            UserDetails userDetails = (UserDetails) authentication;
            return userDetails.getUsername();
        }
        else if (authentication instanceof OAuth2AuthenticationToken) {    //zwykla klasa posiadająca dane z bazy
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String email = oauthToken.getPrincipal().getAttribute("email");
            return email;
        }
        else if (authentication instanceof UsernamePasswordAuthenticationToken) {    //zwykla klasa posiadająca dane z bazy
            UsernamePasswordAuthenticationToken oauthToken = (UsernamePasswordAuthenticationToken) authentication;
            String email = oauthToken.getName();
            return email;
        }
        else {
            return "notfound";
        }
    }




    private boolean emailExists(String email) {
        return repository.findByEmail(email) != null;
    }

}
