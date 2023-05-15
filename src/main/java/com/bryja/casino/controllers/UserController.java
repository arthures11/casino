package com.bryja.casino.controllers;


import com.bryja.casino.classes.*;
import com.bryja.casino.repository.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.config.WebSocketMessageBrokerStats;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(maxAge = 3600)
public class UserController {
    private final UserRepository repository;
    private final RoleRepository rolerep;

    private final DiceHistoryRepository diceHistoryRepository;

    private final BonusHistoryRepository bonusHistoryRepository;
    private final BonusesRepository bonusesRepository;
    private final NotificationRepository notificationRepository;

    private final MessageRepository messageRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserController(UserRepository repository, RoleRepository rolerep, DiceHistoryRepository diceHistoryRepository, BonusHistoryRepository bonusHistoryRepository, BonusesRepository bonusesRepository, NotificationRepository notificationRepository, MessageRepository messageRepository) {
        this.repository = repository;
        this.rolerep = rolerep;
        this.diceHistoryRepository = diceHistoryRepository;
        this.bonusHistoryRepository = bonusHistoryRepository;
        this.bonusesRepository = bonusesRepository;
        this.notificationRepository = notificationRepository;
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

    @PostMapping(value ="/user/rawadd", consumes = {"*/*"})
    public String addNewUser(Authentication authentication, @RequestParam(value = "passwordrepeat") String rep, @RequestBody User usr) {

        if(!rep.equals(usr.password)){
            return "hasla nie zgadzaja sie";
        }

        User a = new User(usr.getName(),usr.getEmail(),passwordEncoder.encode(rep));
        a.setRoles(Arrays.asList(rolerep.findByName("ROLE_USER")));
        if (emailExists(a.getEmail())) {
            return "email juz istnieje";
        }
        else{
            repository.save(a);
        }
        return "ok";
    }
    @Autowired
    private WebSocketMessageBrokerStats webSocketStats;




    @GetMapping(value="/usersonline", consumes = {"*/*"})
    public int users(Authentication authentication) {
        Pattern pattern = Pattern.compile("queued tasks = (\\d+)");
        Matcher matcher = pattern.matcher(webSocketStats.getSockJsTaskSchedulerStatsInfo());
        if (matcher.find()) {
            String queuedTasks = matcher.group(1);

            return Integer.parseInt(queuedTasks)-1;
        } else {
            System.out.println("queued tasks not found");
        }
        return Integer.parseInt(null);
    }

    @PostMapping("/upload-avatar")
    @ResponseBody
    public String uploadAvatar(@RequestParam("avatar") String file, Authentication authentication) throws IOException {
        String[] parts = file.split(",");
        String imageString = parts[1]; // extract the base64 string from the parts
        byte[] imageBytes = Base64.getDecoder().decode(imageString);

        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        usr.setAvatar(imageBytes);
        repository.save(usr);
        return "success";
    }



    @GetMapping(value="/user", consumes = {"*/*"})
    public User user(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));


        return usr;
    }

    @PostMapping(value="/dice/play", consumes = {"*/*"})
    public ResponseEntity<String> diceHistory(Authentication authentication,
                                              @RequestParam("bet-amount-input") String betAmountInput,
                                              @RequestParam("chance-input") String chanceInput,
                                              @RequestParam("options") String options) {

        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        double bet = Double.parseDouble(betAmountInput);
        if(bet<0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong bet amount");
        }
        Random random = new Random();
        chanceInput = chanceInput.replaceAll("[^\\d]", "");
        int num = Integer.parseInt(chanceInput.replace("%", ""));
        int randomNumber = random.nextInt(10001);
        if(num<0.01||num>99.99){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong settings of the bet");
        }
        double sto = 100;
        double chance = num / sto;
        num = num *100;
        double profit = (bet / chance) - bet;  //20 - 10
        String formattedValue = String.format("%.2f", profit);

        if(options.equals("under")){
            if(num > randomNumber){
                usr.balance+= profit;
                usr.getDice_history().add(new DiceHistory(LocalDateTime.now(),bet, chanceInput, randomNumber, "+"+formattedValue, usr));
            }
            else{
                usr.balance-= bet;
                usr.getDice_history().add(new DiceHistory(LocalDateTime.now(),bet, chanceInput, randomNumber, "-"+bet, usr));
            }
        }
        else if(options.equals("over")){
            num = 10000-num;
            if(num < randomNumber){
                usr.balance+= profit;
                usr.getDice_history().add(new DiceHistory(LocalDateTime.now(),bet, chanceInput, randomNumber, "+"+formattedValue, usr));
            }
            else{
                usr.balance-= bet;
                usr.getDice_history().add(new DiceHistory(LocalDateTime.now(),bet, chanceInput, randomNumber, "-"+bet, usr));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("wrong under/over settings");
        }
        repository.save(usr);

        return ResponseEntity.status(HttpStatus.OK).body(String.valueOf(randomNumber));
        //return String.valueOf(randomNumber);
    }

    @GetMapping(value="/dice/history", consumes = {"*/*"})
    public List<DiceHistory> getDiceHistory(Authentication authentication) {
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));

        List<DiceHistory> msgs = diceHistoryRepository.findFirst20ByUserIdOrderByIdDesc(usr.getId());

        return msgs;
    }

    @GetMapping(value="/bonuses/history", consumes = {"*/*"})
    public List<BonusHistory> getBonusesHistory(Authentication authentication) {
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));

        List<BonusHistory> msgs = bonusHistoryRepository.findFirst20ByUserIdOrderByIdDesc(usr.getId());

        return msgs;
    }


    @PostMapping(value="/bonuses/claim", consumes = {"*/*"})
    @Transactional
    public String claimBonuses(Authentication authentication) {
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        usr.setBalance(usr.getBalance()+usr.getBonuses());
        usr.setBonuses(0);
        return "ok";
    }

    @PostMapping(value="/bonuses/addnew", consumes = {"*/*"})
    public String addNewBonus(Authentication authentication, @RequestBody Bonuses bns) {


        Bonuses newbn = new Bonuses(bns.getName(),bns.getAmount(),bns.getEvery_hours());

        bonusesRepository.save(newbn);

        return "dodano bonus";
    }

    @DeleteMapping(value="/user/delete/{id}", consumes = {"*/*"})
    public String deleteUser(Authentication authentication, @PathVariable Long id) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {

            return"nie znaleziono usera";
        }

        User usr = user.get();
        repository.delete(usr);

        return "success";
    }

    @Transactional
    @DeleteMapping(value="/alerts/removeall", consumes = {"*/*"})
    public String removeAlerts(Authentication authentication) {
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        usr.getNotyfikacje().clear();
        notificationRepository.deleteAllByUserId(usr.getId());
        repository.save(usr);

        return "success";
    }

    @DeleteMapping(value="/bonus/delete/{id}", consumes = {"*/*"})
    public String deleteBonus(Authentication authentication, @PathVariable Long id) {
        Optional<Bonuses> bonusik = bonusesRepository.findById(id);
        if (bonusik.isEmpty()) {
            return"nie znaleziono usera";
        }
        Bonuses bn = bonusik.get();
        bonusesRepository.delete(bn);

        return "success";
    }

    @PostMapping(value="/users/{id}", consumes = {"*/*"})
    public String editUser(Authentication authentication, @PathVariable Long id,@RequestBody User usera) {
        Optional<User> user = repository.findById(id);
        if (user.isEmpty()) {

            return"nie znaleziono usera";
        }
        Privilege readPrivilege
                = findPrivilege("READ_PRIVILEGE");
        Role rk = roleRepository.findByName(usera.chip);
        if(rk==null){
            return "nie znaleziono roli";
        }
        rk.setPrivileges(Arrays.asList(readPrivilege));
        List<Role> roles = new ArrayList<>();
        roles.add(rk);
        User userka = user.get();
        userka.setName(usera.getName());
        userka.setEmail(usera.getEmail());
        userka.setBalance(usera.getBalance());
        userka.setRoles(roles);
        //existingUser.setRoles(updatedUser.getRoles());

        repository.save(userka);
        //return ResponseEntity.ok(savedUser);
        return "success";
    }

    @PostMapping(value="/bonuses/edit/{id}", consumes = {"*/*"})
    public String editUser(Authentication authentication, @PathVariable Long id,@RequestBody Bonuses bonus) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Bonuses> bonu  = bonusesRepository.findById(id);
        if (bonu.isEmpty()) {

            return"nie znaleziono bonusu";
        }
        Bonuses bonusik = bonu.get();
        bonusik.setAmount(bonus.getAmount());
        bonusik.setName(bonus.getName());
        bonusik.setEvery_hours(bonus.getEvery_hours());

        bonusesRepository.save(bonusik);
        return "success";
    }

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PrivilegeRepository privilegeRepository;


    Role findRole(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            return null;
        }
        return role;
    }
    Privilege findPrivilege(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            return null;
        }
        return privilege;
    }
    @GetMapping(value="/admin/bonuses", consumes = {"*/*"})
    public List<Bonuses> AdmingetBonuses(Authentication authentication) {

        List<Bonuses> msgs = bonusesRepository.findAll();

        return msgs;
    }

    @GetMapping(value="/admin/users", consumes = {"*/*"})
    public List<UserProjection>  adminGetUsers(Authentication authentication) {

       // List<UserRepository.UserProjection> msgs = repository.findAllUsers();
        List<User> msgs = repository.findAll();

        List<User> users = repository.findAll();
        List<UserProjection> projections = users.stream()
                .map(user -> new UserProjection() {

                    @Override
                    public Long getId(){
                        return user.getId();
                    }
                    @Override
                    public String getName() {
                        return user.getName();
                    }

                    @Override
                    public String getEmail() {
                        return user.getEmail();
                    }

                    @Override
                    public double getBalance() {
                        return user.getBalance();
                    }

                    @Override
                    public List<String> getRoleNames() {
                        return user.getRoles().stream().map(Role::getName).collect(Collectors.toList());
                    }
                })
                .collect(Collectors.toList());

        return projections;
    }


    @GetMapping(value="/chathistory", consumes = {"*/*"})
    public List<Message> getChatMessages(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Message> msgs = messageRepository.findFirst20ByChatroomOrderByIdDesc("EN");

        for(int i=0;i<msgs.size();i++){
            Optional<User> g = repository.findById(msgs.get(i).getUser().getId());
            msgs.get(i).setAvatar(g.get().getAvatar());
            msgs.get(i).setAuthor_name(g.get().getName());
        }
         return msgs;
    }

    @GetMapping(value="/chathistory/germany", consumes = {"*/*"})
    public List<Message> getChatMessagesGer(Authentication authentication) {
        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Message> msgs = messageRepository.findFirst20ByChatroomOrderByIdDesc("GER");

        for(int i=0;i<msgs.size();i++){
            Optional<User> g = repository.findById(msgs.get(i).getUser().getId());
            msgs.get(i).setAvatar(g.get().getAvatar());
            msgs.get(i).setAuthor_name(g.get().getName());
        }
        return msgs;
    }
    @PostMapping(value="/settings/apply", consumes = {"*/*"})
    public String changeProfile(Authentication authentication,
                                              @RequestParam("username") String new_name,
                                              @RequestParam("pass") String new_pass,
                                              @RequestParam("repeat") String new_pass_repeat) {

        if(!new_pass.equals(new_pass_repeat)){
            return "hasla sie nie zgadzaja";
        }
        User usr = repository.findByEmail(checkmail(authentication.getPrincipal()));
        if(!new_pass.isEmpty()){
            usr.setPassword(passwordEncoder.encode(new_pass));
        }
        if(!new_name.isEmpty()){
            usr.setName(new_name);
        }
        repository.save(usr);
        return "zapisano pomyslnie";
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

    @Transactional
    public void updateBalanceForAllUsers(double balanceToAdd, String type) {
        List<User> users = repository.findAll();
        for (User user : users) {
            user.setBonuses(user.getBonuses() + balanceToAdd);

            List<Notification> a = user.getNotyfikacje();
            a.add(new Notification("nowy bonus został dodany: "+type+" +"+balanceToAdd+"żetonów", new Date(),user));
            user.setNotyfikacje(a);
            List<BonusHistory> b = user.getBonus_history();
            b.add(new BonusHistory(LocalDateTime.now(),type, balanceToAdd, user));
            user.setBonus_history(b);

          //  user.getBonus_history().add(new BonusHistory(LocalDateTime.now(),type, balanceToAdd, user));
           // user.getNotyfikacje().add(new Notification("nowy bonus został dodany: "+type+" +"+balanceToAdd+"żetonów", new Date(),user));
            repository.save(user);
        }
    }

    private boolean emailExists(String email) {
        return repository.findByEmail(email) != null;
    }

}
