package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.response.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create jobs")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job dto) throws  IdInvalidException {
        ResCreateJobDTO newJob = this.jobService.handleCreateJob(dto);
        return new ResponseEntity(newJob,newJob != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
    @PutMapping("/jobs")
    @ApiMessage("Update jobs")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job dto) throws  IdInvalidException {
        Job currentJob  =this.jobService.getById(dto.getId());
        if(currentJob == null) {
            throw  new IdInvalidException("Job " + dto.getName() + " is not found!");
        }
        ResUpdateJobDTO updateJob = this.jobService.handleUpdateJob(dto,currentJob);
        return new ResponseEntity(updateJob,updateJob != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/jobs")
    @ApiMessage("/Fetch jobs")
    public ResponseEntity<ResultPaginationDTO> fetchAllJob(@Filter Specification<Job> spec, Pageable pageable) {
        ResultPaginationDTO result = this.jobService.getAllJobs(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job")
    public  ResponseEntity<Job> getAJob(@PathVariable long id) {
        Job  currentJob = this.jobService.getById(id);
        return new ResponseEntity<Job>(currentJob,currentJob != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable long id) throws IdInvalidException {
        Job currentJob = this.jobService.getById(id);
        if(currentJob == null) throw  new IdInvalidException("Job is not found");
        this.jobService.delete(id);
        return ResponseEntity.ok(null);
    }


}
