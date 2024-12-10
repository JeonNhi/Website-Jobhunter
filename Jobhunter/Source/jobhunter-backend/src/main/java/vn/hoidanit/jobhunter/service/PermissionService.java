package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

import java.util.Optional;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public  PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository=permissionRepository;
    }

    public Permission handleCreate(Permission p) {
       return this.permissionRepository.save(p);

    }

    public Optional<Permission> getAPermission(long id) {
        return  this.permissionRepository.findById(id);
    }

    public boolean isPermissionExist(Permission p) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(
                p.getModule(),
                p.getApiPath(),
                p.getMethod()
        );
    }

    public  boolean isSameName(Permission p) {
        return this.permissionRepository.existsByName(p.getName());
    }

    public Permission update(Permission p,Permission permissionDb) {
        permissionDb.setName(p.getName());
        permissionDb.setMethod(p.getMethod());
        permissionDb.setModule(p.getModule());
        return this.permissionRepository.save(permissionDb);

    }

    public ResultPaginationDTO getAllPermission(Specification spec, Pageable pageable) {
        ResultPaginationDTO dto = new ResultPaginationDTO();
        Page<Permission> pPermission = this.permissionRepository.findAll(spec,pageable);
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() +1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pPermission.getTotalElements());
        meta.setPages(pPermission.getTotalPages());
        dto.setMeta(meta);
        dto.setResult(pPermission.getContent());
        return dto;
    }

    public void delete(long id) {
        this.permissionRepository.deleteById(id);
    }
}
