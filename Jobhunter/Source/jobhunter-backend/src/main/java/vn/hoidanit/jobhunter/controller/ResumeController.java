package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import jakarta.validation.Valid;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.*;
import vn.hoidanit.jobhunter.domain.response.*;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SercurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    private final UserService userService;

    private final FilterBuilder filterBuilder;

    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService,
                            UserService userService,
                            FilterBuilder filterBuilder,
                            FilterSpecificationConverter filterSpecificationConverter
    ) {
        this.resumeService = resumeService;
        this.userService =userService;
        this.filterBuilder=filterBuilder;
        this.filterSpecificationConverter=filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resumes")
    public ResponseEntity<Resume> createResume( @RequestBody Resume dto) throws  IdInvalidException {
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(dto);
        if(!isIdExist) {
            throw  new IdInvalidException("User id/Job id không tồn tại");
        }
        ResCreateResumeDTO newResume = this.resumeService.handleCreateResume(dto);
        return new ResponseEntity(newResume,newResume != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/resumes")
    @ApiMessage("Update resumes")
    public ResponseEntity<ResUpdateResumeDTO> updateResume( @RequestBody Resume dto) throws  IdInvalidException {
        Optional<Resume> currentResume  =this.resumeService.getById(dto.getId());
        if(currentResume.isEmpty()) {
            throw  new IdInvalidException("Resume  is not found!");
        }
        Resume resumeUpdate = currentResume.get();
        resumeUpdate.setStatus(dto.getStatus());
        ResUpdateResumeDTO updateResume = this.resumeService.handleUpdateResume(resumeUpdate);
        return new ResponseEntity(updateResume,updateResume != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/resumes")
    @ApiMessage("/Fetch resumes")
    public ResponseEntity<ResultPaginationDTO> fetchAllResume(@Filter Specification<Resume> spec, Pageable pageable) {
        List<Long> allJobs = new ArrayList<>();
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        User currentUser = this.userService.handleGetUserByUserName(email);
        if(currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if(userCompany != null) {
                List<Job> jobsCompany =userCompany.getJobs();
                if(jobsCompany != null && !jobsCompany.isEmpty()) {
                    allJobs = jobsCompany.stream().map(job -> job.getId()).collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobsInSpec = filterSpecificationConverter.convert(filterBuilder.field("job").in(filterBuilder.input(allJobs)).get());
        Specification<Resume> finalSpec = currentUser.getRole().getName().toUpperCase().equals("SUPER ADMIN") ? spec : spec.and(jobsInSpec) ;

        // filterBuilder.field("job"): Tạo bộ lọc dựa trên trường job của bảng Resume.
        // .in(filterBuilder.input(allJobs)): Đây là cách bạn lọc để chỉ lấy những Resume ứng tuyển vào các công việc có id trong danh sách allJobs.
        //   Sau đó bạn chuyển đổi nó thành một đối tượng Specification<Resume> thông qua filterSpecificationConverter.

        ResultPaginationDTO result = this.resumeService.getAllResumes(finalSpec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume")
    public  ResponseEntity<ResFetchResumeDTO> getAResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> resume = this.resumeService.getById(id);
        if(resume.isEmpty()) {
            throw  new IdInvalidException("Resume is not found");
        }

        ResFetchResumeDTO currentResume = this.resumeService.getResume(resume.get());
        return new ResponseEntity<ResFetchResumeDTO>(currentResume,currentResume != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable long id) throws IdInvalidException {
        Optional<Resume> currentResume = this.resumeService.getById(id);
        if(currentResume.isEmpty()) throw  new IdInvalidException("Resume is not found");
        this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Get list resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {
        return ResponseEntity.ok().body(this.resumeService.fetchResumeByUser(pageable));
    }



}
