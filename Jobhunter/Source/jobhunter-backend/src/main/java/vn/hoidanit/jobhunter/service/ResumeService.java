package vn.hoidanit.jobhunter.service;

import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Resume;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.*;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.util.SercurityUtil;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    private final  JobService jobService;

    private  final UserService userService;

    @Autowired
    FilterBuilder fb;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;


    public ResumeService(ResumeRepository resumeRepository,
                         JobService jobService,
                         UserService userService ) {
        this.resumeRepository = resumeRepository;
        this.jobService =jobService;
        this.userService =userService;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if(resume.getUser() == null || resume.getJob() == null) {
            return false;
        }
        User user = this.userService.getUserById(resume.getUser().getId());
        if (user == null) return  false;

        Job job = this.jobService.getById(resume.getJob().getId());
        if(job == null) return false;

        return true;

    }

    public ResCreateResumeDTO handleCreateResume(Resume resume) {
        resume = this.resumeRepository.save(resume);
        ResCreateResumeDTO newResume = new ResCreateResumeDTO();
        newResume.setId(resume.getId());
        newResume.setCreatedAt(resume.getCreatedAt());
        newResume.setCreatedBy(resume.getCreatedBy());
        return newResume;

    }
    public Optional<Resume> getById(long id) {
        return this.resumeRepository.findById(id);
    }

    public  ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO resumeDTO = new ResFetchResumeDTO();
        resumeDTO.setId(resume.getId());
        resumeDTO.setEmail(resume.getEmail());
        resumeDTO.setStatus(resume.getStatus());
        resumeDTO.setUrl(resume.getUrl());
        resumeDTO.setCreatedAt(resume.getCreatedAt());
        resumeDTO.setCreatedBy(resume.getCreatedBy());
        resumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resumeDTO.setUpdatedBy(resume.getUpdatedBy());
        resumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        resumeDTO.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(),resume.getJob().getName()));
        resumeDTO.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(),resume.getUser().getName()));

        return resumeDTO;

    }

    public ResUpdateResumeDTO handleUpdateResume(Resume resume)  {
        resume = this.resumeRepository.save(resume);
        ResUpdateResumeDTO updateResumeDTO = new ResUpdateResumeDTO();
        updateResumeDTO.setId(resume.getId());
        updateResumeDTO.setUpdatedBy(resume.getUpdatedBy());
        updateResumeDTO.setUpdatedAt(resume.getUpdatedAt());

        return updateResumeDTO;
    }

    public ResultPaginationDTO getAllResumes(Specification spec,Pageable pageable) {
        ResultPaginationDTO result = new ResultPaginationDTO();
        Page<Resume> pResumes = this.resumeRepository.findAll(spec,pageable);
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pResumes.getTotalElements());
        mt.setPages(pResumes.getTotalPages());
        result.setMeta(mt);
        List<ResFetchResumeDTO> fetchResume = pResumes.getContent().stream().map(item ->this.getResume(item)).collect(Collectors.toList());
        result.setResult(fetchResume);
        return result;

    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        FilterNode node =filterParser.parse("email='"+email+"'");
        FilterSpecification<Resume> spec =filterSpecificationConverter.convert(node);
        Page<Resume> pResumes= this.resumeRepository.findAll(spec,pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pResumes.getTotalElements());
        mt.setPages(pResumes.getTotalPages());
        result.setMeta(mt);
        List<ResFetchResumeDTO> fetchResume = pResumes.getContent().stream().map(item ->this.getResume(item)).collect(Collectors.toList());
        result.setResult(fetchResume);
        return result;
    }


        public void delete(long id) {
        this.resumeRepository.deleteById(id);
    }



}
