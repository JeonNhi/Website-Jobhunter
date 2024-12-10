package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1/")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService,PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @ApiMessage("Create a user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User user) throws IdInvalidException {
        boolean isExistUser = this.userService.isEmailExist(user.getEmail());
        if(isExistUser) throw new IdInvalidException("User" + user.getEmail() + " đã tồn tại,vui lòng nhập email khác!");
        String hashPassword = this.passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));

    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.getUserById(id);
        if(currentUser == null) throw new IdInvalidException("User không tồn tại!");
        this.userService.deleteUser(id);
        return ResponseEntity.ok(null);

    }

    @GetMapping("/users")
    @ApiMessage("Fetch user")
    public ResponseEntity<ResultPaginationDTO>  fetchUsers(@Filter Specification<User> spec, Pageable pageable) {
        ResultPaginationDTO result = this.userService.getAllUsers(spec,pageable);

        return ResponseEntity.status(HttpStatus.OK).body(result);

    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) {
        User user = this.userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUserById(@RequestBody User user) throws IdInvalidException {
        User userUpdate = this.userService.updateUser(user);
        if(userUpdate == null) throw new IdInvalidException("User với Id ="+ user.getId()+" không tồn tại!");
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userUpdate));

    }
    @PutMapping("/user-by-admin")
    @ApiMessage("Update a user by admin")
    public ResponseEntity<ResUpdateUserDTO> updateUserByAdmin(@RequestBody User user) throws IdInvalidException {
        User userUpdate = this.userService.updateUserByAdmin(user);
        if(userUpdate == null) throw new IdInvalidException("User với Id ="+ user.getId()+" không tồn tại!");
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(userUpdate));

    }
}
