package Tfast_Rmoney.Givvy.interfaces;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import Tfast_Rmoney.Givvy.core.User;
import Tfast_Rmoney.Givvy.core.UserDao;
import Tfast_Rmoney.Givvy.entities.Interest;
import Tfast_Rmoney.Givvy.core.InterestDAO;
import Tfast_Rmoney.Givvy.core.Item;
import Tfast_Rmoney.Givvy.core.ItemDAO;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private UserDao dao;
    private InterestDAO interestDao;
    private ItemDAO itemDao;

    public UserController(UserDao dao, InterestDAO interestDao,ItemDAO itemDao) {
        this.dao = dao;
        this.interestDao = interestDao;
        this.itemDao = itemDao;
    }

    // GET /users/login?email=test@example.com&password=pass123
    @GetMapping("/login")
    public ResponseEntity<String> checkLogin(
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password) {

        User result = dao.findByEmailAndPassword(email, password);

        if (result == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid email or password");
        }

        return ResponseEntity.ok().body(result.getUserId());
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

        String key = dao.save(user);

        if (key.equals("Duplicate")) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("User with this email already exists");
        } else if (key.equals("Error")) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Can not generate key");
        }

        return ResponseEntity.ok().body(key);
    }


    // GET /users/{userId}/items
    @GetMapping("/{userId}/items")
    public ResponseEntity<List<Item>> getItemsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(itemDao.findByUser(userId));
    }

    @GetMapping("/{id}/interests")
    public ResponseEntity<List<Interest>> findInterestsForUser(@PathVariable String id) {
        List<Interest> results = interestDao.findByRecipientId(id);
        return ResponseEntity.ok().body(results);
    }
    
}