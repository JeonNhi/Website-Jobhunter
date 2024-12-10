package vn.hoidanit.jobhunter.service;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJobDTO;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SubscriberService {
  private final SubscriberRepository subscriberRepository;

    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;
    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository, JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public boolean isEmailExist(String email) {
       return this.subscriberRepository.existsByEmail(email);
    }
    public Subscriber save(Subscriber dto) {
        if(!CollectionUtils.isEmpty(dto.getSkills())) {
            List<Long> listSkills = dto.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> listSkillDb = this.skillRepository.findByIdIn(listSkills);
            dto.setSkills(listSkillDb);

        }
        return this.subscriberRepository.save(dto);
    }
    public  Subscriber update(Subscriber sub,Subscriber subDB) {
        if(!CollectionUtils.isEmpty(sub.getSkills())) {
            List<Long> listSkills = sub.getSkills().stream().map(skill -> skill.getId()).collect(Collectors.toList());
            List<Skill> listSkillDb = this.skillRepository.findByIdIn(listSkills);
            subDB.setSkills(listSkillDb);
        }
        return this.subscriberRepository.save(subDB);

    }
    public Optional<Subscriber> fetchById(Long id) {
        Optional<Subscriber> OpSubscriber = this.subscriberRepository.findById(id);

        return OpSubscriber;
    }

    public void delete(Long id) {
        this.subscriberRepository.deleteById(id);
    }

    public ResultPaginationDTO paging(Specification<Subscriber> spec, Pageable pageable) {
        Page<Subscriber> pSubscriber = this.subscriberRepository.findAll(spec,pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPages(pSubscriber.getTotalPages());
        mt.setTotal(pSubscriber.getTotalElements());
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        result.setMeta(mt);
        result.setResult(pSubscriber.getContent());
        return result;
    }

    public ResEmailJobDTO convertToJobEmail(Job job) {
        ResEmailJobDTO resEmailJobDTO = new ResEmailJobDTO();
        resEmailJobDTO.setName(job.getName());
        resEmailJobDTO.setSalary(job.getSalary());
        resEmailJobDTO.setLevel(job.getLevel());
        resEmailJobDTO.setQuantity(job.getQuantity());
        resEmailJobDTO.setCompany(new ResEmailJobDTO.CompanyEmail((job.getCompany().getName())));
        List<Skill> skills = job.getSkills();
        List<ResEmailJobDTO.SkillEmail> skillEmails = skills.stream().map(skill -> new ResEmailJobDTO.SkillEmail(skill.getName())).collect(Collectors.toList());
        resEmailJobDTO.setSkills(skillEmails);
        return resEmailJobDTO;

    }

    public void sendSubscribersEmailJobs() {
        //lấy ra all subscriber;
        //lặp qua ,mỗi subscriber có skill đăng kí riêng từ đó lấy những job có những skill đó,
        //có job rồi thì sẽ convert và gửi mail những job đó
        List<Subscriber> allSubscribers = this.subscriberRepository.findAll();
        if(!CollectionUtils.isEmpty(allSubscribers)) {
            for (Subscriber sub : allSubscribers) {
                List<Skill> listSkill = sub.getSkills();
                if(!CollectionUtils.isEmpty(listSkill)) {
                    List<Job> listJob = this.jobRepository.findBySkillsIn(listSkill);
                    if(!CollectionUtils.isEmpty(listJob)) {
                        List<ResEmailJobDTO> res = listJob.stream().map(item -> this.convertToJobEmail(item)).collect(Collectors.toList());
                        this.emailService.sendMailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm đang chờ bạn,khám phá ngay",
                                "job", sub.getName(), res);
                    }
                }
            }
        }

    }

    public Subscriber findByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }
//    @Scheduled(cron = "*/10 * * * * *")
//    public void testCron() {
//        System.out.println("test cron");
//    }

}
