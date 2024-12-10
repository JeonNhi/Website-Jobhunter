package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.persistence.Id;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("")
    @ApiMessage("create a role")
    public ResponseEntity<Role> handleCreate(@RequestBody Role dto) throws IdInvalidException {
//        boolean isExistRole = this.roleService.isExistRole(dto.getName());
//        if (isExistRole) {
//            throw  new IdInvalidException("Role is exist!");
//        }
        Role role = this.roleService.save(dto);
        return new ResponseEntity<>(role,role != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping("")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> handleUpdate(@RequestBody Role dto) throws IdInvalidException  {
//        boolean isExistRole = this.roleService.isExistRole(dto.getName());
//        if (isExistRole) {
//            throw  new IdInvalidException("Role has been duplicated!");
//        }
        Optional<Role> opRole = this.roleService.getARole(dto.getId());
        if(opRole.isEmpty()) {
            throw  new IdInvalidException("Role is not found!");
        }
        Role role = this.roleService.update(dto,opRole.get());
        return new ResponseEntity<>(role,role != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}")
    @ApiMessage("Get a role")
    public ResponseEntity<Role> fetchARole(@PathVariable long id) throws IdInvalidException {
        Optional<Role> role = this.roleService.getARole(id);
        if(role.isEmpty()) {
            throw  new IdInvalidException("Role is not found!");
        }
        return new ResponseEntity<>(role.get(),role.isPresent() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("")
    @ApiMessage("Get all role")
    public ResponseEntity<ResultPaginationDTO> fetchAllRole(@Filter Specification spec, Pageable pageable) {
        ResultPaginationDTO result = this.roleService.getAllRole(spec,pageable);
        return ResponseEntity.ok().body(result);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("delete a role")
    public ResponseEntity<Void> handleDeleteRole(@PathVariable long id) throws IdInvalidException {
        Optional<Role> role = this.roleService.getARole(id);
        if(role.isEmpty()) {
            throw  new IdInvalidException("Role is not found!");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }


}
