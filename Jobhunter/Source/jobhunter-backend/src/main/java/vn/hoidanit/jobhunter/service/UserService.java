package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;
import vn.hoidanit.jobhunter.util.SercurityUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final CompanyRepository companyRepository;

    private final RoleService roleService;

    public UserService(UserRepository userRepository,
                       CompanyRepository companyRepository,RoleService roleService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        if(user.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(user.getCompany().getId());
            user.setCompany(company.isPresent() ? company.get() : null);

        }

        if(user.getRole() != null) {
            Optional<Role> role = this.roleService.getARole(user.getRole().getId());
            user.setRole(role.isPresent() ? role.get() : null);
        }

       return this.userRepository.save(user);
    }

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public void deleteUser(long id) {
        this.userRepository.deleteById(id);

    }
    public boolean isEmailExist(String email) {
       return this.userRepository.existsByEmail(email);
    }

    public User handleGetUserByUserName(String userName) {
       return this.userRepository.findByEmail(userName);
    }

    public User getUserById(long id) {
        Optional<User> user = this.userRepository.findById(id);
        if(user.isPresent()) {
            return user.get();
        }
        return null;

    }

    public ResultPaginationDTO getAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> pUser = this.userRepository.findAll(spec,pageable);
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pUser.getTotalElements());
        mt.setPages(pUser.getTotalPages());
        result.setMeta(mt);


        List<ResUserDTO> listUser = pUser.getContent()
                        .stream().map(item -> this.convertToResUserDTO(item)).collect(Collectors.toList()

                );
        result.setResult(listUser);
        return  result;
    }

    public User updateUser(User userUpdate) {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.handleGetUserByUserName(email);
        if(userUpdate != null) {
            currentUser.setAge(userUpdate.getAge());
            currentUser.setName(userUpdate.getName());
            currentUser.setAddress(userUpdate.getAddress());
            currentUser.setGender(userUpdate.getGender());

            if(userUpdate.getCompany() != null) {
                Optional<Company> company = this.companyRepository.findById(userUpdate.getCompany().getId());
                currentUser.setCompany(company.isPresent() ? company.get() : null);

            }
            if(userUpdate.getRole() != null) {
                Optional<Role> role = this.roleService.getARole(userUpdate.getRole().getId());
                currentUser.setRole(role.isPresent()  ? role.get() : null);
            }


            currentUser= this.userRepository.save(currentUser);
        }
        return currentUser;

    }
    public User updateUserByAdmin(User userUpdate) {
        User user = this.getUserById(userUpdate.getId());

        if(user != null && userUpdate != null) {
            user.setAge(userUpdate.getAge());
            user.setName(userUpdate.getName());
            user.setAddress(userUpdate.getAddress());
            user.setGender(userUpdate.getGender());

            if(userUpdate.getCompany() != null) {
                Optional<Company> company = this.companyRepository.findById(userUpdate.getCompany().getId());
                user.setCompany(company.isPresent() ? company.get() : null);

            }
            if(userUpdate.getRole() != null) {
                Optional<Role> role = this.roleService.getARole(userUpdate.getRole().getId());
                user.setRole(role.isPresent()  ? role.get() : null);
            }


            user= this.userRepository.save(user);
        }
        return user;

    }
    public void updateUserToken(String token,String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if(currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res  = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser companyUser = new ResCreateUserDTO.CompanyUser();
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setId(user.getId());
        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }

        return res;

    }
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res  = new ResUpdateUserDTO();
        ResUpdateUserDTO.CompanyUser companyUser  = new ResUpdateUserDTO.CompanyUser();
        ResUpdateUserDTO.RoleUser roleUser = new ResUpdateUserDTO.RoleUser();

        res.setAge(user.getAge());
        res.setName(user.getName());

        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());
        res.setId(user.getId());

        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
        if(user.getRole() != null) {
            roleUser.setId(user.getRole().getId());
            roleUser.setName(user.getRole().getName());
            res.setRole(roleUser);
        }
        return res;

    }
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res  = new ResUserDTO();
        ResUserDTO.CompanyUser companyUser  = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setGender(user.getGender());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setAddress(user.getAddress());
        res.setCreatedAt(user.getCreatedAt());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setEmail(user.getEmail());

        if(user.getCompany() != null) {
            companyUser.setId(user.getCompany().getId());
            companyUser.setName(user.getCompany().getName());
            res.setCompany(companyUser);
        }
        if(user.getRole() != null) {
            Optional<Role> role = this.roleService.getARole(user.getRole().getId());
            if (role.isPresent()) {
                roleUser.setId(role.get().getId());
                roleUser.setName(role.get().getName());
                res.setRole(roleUser);
            }

        }

        return res;

    }

    public User getUserByRefreshTokenAndEmail(String token,String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token,email);
    }


}
