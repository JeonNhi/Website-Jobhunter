package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.request.ReqCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResCompanyDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> saveCompany(@Valid @RequestBody ReqCompanyDTO companyDTO) {
        Company newCompany = this.companyService.handleCreate(companyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newCompany);

    }

    @GetMapping("/companies")
    @ApiMessage("fetch companies all")
    public  ResponseEntity<ResultPaginationDTO> getAllCompany(@Filter Specification<Company> spec, Pageable pageable) {
        ResultPaginationDTO result = this.companyService.fetchCompanies(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PutMapping("/companies")
    public ResponseEntity<ResCompanyDTO> updateCompany(@RequestBody Company c) {
        ResCompanyDTO res = this.companyService.updateCompany(c);
        return ResponseEntity.status(HttpStatus.OK).body(res);
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company by id")
    public ResponseEntity<ResCompanyDTO> fetchCompanyById(@PathVariable long id) throws IdInvalidException {
        ResCompanyDTO company = this.companyService.getCompanyById(id);
        if(company == null) throw  new IdInvalidException("Company is not found");
        return ResponseEntity.ok().body(company);


    }
}
