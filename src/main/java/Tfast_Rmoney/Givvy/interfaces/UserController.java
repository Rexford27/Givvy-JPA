package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Tfast_Rmoney.Givvy.core.InterestDAO;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.entities.Item;
import Tfast_Rmoney.Givvy.entities.User;
import Tfast_Rmoney.Givvy.repositories.ItemRepository;
import Tfast_Rmoney.Givvy.services.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final InterestDAO interestDao;
    private final ItemRepository itemDao;

    public UserController(UserService userService, InterestDAO interestDao, ItemRepository itemDao) {
        this.userService = userService;
        this.interestDao = interestDao;
        this.itemDao = itemDao;
    }

    // POST /users/login
    @PostMapping("/login")
    public ResponseEntity<String> checkLogin(@RequestBody LoginRequest loginRequest) {

        if (loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                || loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Empty email or password");
        }

        Optional<User> result = userService.loginAndReturnUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (result.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        return ResponseEntity.ok().body(result.get().getUserId().toString());
    }

    // POST /users
    @PostMapping
    public ResponseEntity<String> save(@RequestBody User user) {

        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPasswordHash() == null || user.getPasswordHash().isBlank()) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Empty name, email, or password");
        }

        String key = userService.registerUser(user);

        if (key.equals("user exists, duplicate")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with this email already exists");
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(key);
    }

    // GET /users/{userId}/items
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<Item>> getItemsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(itemDao.findByUser(userId));
    }

    // GET /users/{id}/interests
    @GetMapping("/{id}/interests")
    public ResponseEntity<List<Interest>> findInterestsForUser(@PathVariable String id) {
        List<Interest> results = interestDao.findByRecipientId(id);
        return ResponseEntity.ok().body(results);
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}