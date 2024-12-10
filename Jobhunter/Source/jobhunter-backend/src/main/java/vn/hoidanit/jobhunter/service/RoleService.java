package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository=permissionRepository;
    }
    public boolean isExistRole(String name) {
        return this.roleRepository.existsByName(name);
    }
    public Role save(Role dto) {
        if(dto.getPermissions() != null) {
            List<Long> permissionList = dto.getPermissions().stream().map(p -> p.getId()).collect(Collectors.toList());
            List<Permission> permissionsDb = this.permissionRepository.findByIdIn(permissionList);
            dto.setPermissions(permissionsDb);
        }
        return this.roleRepository.save(dto);
    }

    public Optional<Role> getARole(long id) {
        return this.roleRepository.findById(id);

    }

    public Role update(Role dto,Role roleDb) {
        if(dto.getPermissions() != null) {
            List<Long> permissonList = dto.getPermissions().stream().map(p -> p.getId()).collect(Collectors.toList());
            List<Permission> permissionsDb=this.permissionRepository.findByIdIn(permissonList);
            roleDb.setPermissions(permissionsDb);
        }
        roleDb.setName(dto.getName());
        roleDb.setActive(dto.isActive());
        roleDb.setDescription(dto.getDescription());

        return this.roleRepository.save(roleDb);
    }
    public ResultPaginationDTO getAllRole(Specification spec, Pageable pageable) {
       ResultPaginationDTO result = new ResultPaginationDTO();
       ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
       Page<Role> pRole = this.roleRepository.findAll(spec,pageable);
       meta.setPages(pRole.getTotalPages());
       meta.setTotal(pRole.getTotalElements());
       meta.setPage(pageable.getPageNumber() +1);
       meta.setPageSize(pageable.getPageSize());
       result.setResult(pRole.getContent());
       result.setMeta(meta);
       return result;
    }
    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

}
