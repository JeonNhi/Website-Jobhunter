package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.CompanyRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository,
                          UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository =userRepository;
    }

    public Company handleCreate(ReqCompanyDTO c) {
        Company newCompany = new Company();
        newCompany.setLogo(c.getLogo());
        newCompany.setAddress(c.getAddress());
        newCompany.setDescription(c.getDescription());
        newCompany.setName(c.getName());
        this.companyRepository.save(newCompany);
        return newCompany;

    }

    public ResultPaginationDTO fetchCompanies(Specification<Company> spec, Pageable pageable){

        Page<Company> pCompany = this.companyRepository.findAll(spec,pageable);
        ResultPaginationDTO result  = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setTotal(pCompany.getTotalElements());
        meta.setPages(pCompany.getTotalPages());
        result.setMeta(meta);
        List<ResCompanyDTO> listCompany = pCompany.getContent().stream().map(c -> this.convertResCompanyDTO(c)).collect(Collectors.toList());
        result.setResult(listCompany);
        return result;
    }

    public ResCompanyDTO getCompanyById(long id) {
        Optional<Company> company = this.companyRepository.findById(id);
        if(company.isPresent()) {
            Company currentCompany = company.get();
            ResCompanyDTO dto = convertResCompanyDTO(currentCompany);

            return dto;
        }
        return null;
    }
    public ResCompanyDTO convertResCompanyDTO(Company c) {
        ResCompanyDTO dto = new ResCompanyDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        if(!CollectionUtils.isEmpty(c.getJobs())) {
            List<Job> jobs = c.getJobs();
            Set<Skill> skills = new HashSet<>();
            for (Job job : jobs) {
                skills.addAll(job.getSkills());

            }
            dto.setJobs(jobs);
            dto.setSkills(new ArrayList<>(skills));

        }
        dto.setDescription(c.getDescription());
        dto.setAddress(c.getAddress());
        dto.setLogo(c.getLogo());
        dto.setUpdatedBy(c.getUpdatedBy());
        dto.setUpdatedAt(c.getUpdatedAt());
        dto.setCreatedBy(c.getCreatedBy());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }

    public ResCompanyDTO updateCompany(Company c) {
        Optional<Company> opCompany = this.companyRepository.findById(c.getId());
        if(opCompany.isPresent()) {
            Company currentCompany = opCompany.get();
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            currentCompany.setLogo(c.getLogo());
            this.companyRepository.save(currentCompany);
            ResCompanyDTO dto = this.convertResCompanyDTO(currentCompany);
            return  dto;
        }
        return  null;
    }

    public void deleteCompany(long id) {
        Optional<Company> currentCompany = this.companyRepository.findById(id);
        if(currentCompany.isPresent()) {
            List<User> users = this.userRepository.findByCompany(currentCompany.get());
            this.userRepository.deleteAll(users);
            this.companyRepository.deleteById(currentCompany.get().getId());
        }
    }

}
