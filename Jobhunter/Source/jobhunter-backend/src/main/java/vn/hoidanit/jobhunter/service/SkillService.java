package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean isSkillExist(String skill) {
       return this.skillRepository.existsByName(skill);
    }

    public Skill handleCreateSkill(Skill dto) {
        return this.skillRepository.save(dto);
    }
    public Skill getById(long id) {
        Optional<Skill> skill = this.skillRepository.findById(id);
        if(skill.isPresent()) return skill.get();
        else return null;
    }

    public Skill updateSkill (Skill dto) {
        return this.skillRepository.save(dto);
    }

    public ResultPaginationDTO getAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pSkill = this.skillRepository.findAll(spec,pageable);
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        ResultPaginationDTO result = new ResultPaginationDTO();
        mt.setPage(pageable.getPageNumber() +1);
        mt.setPageSize(pageable.getPageSize());
        mt.setTotal(pSkill.getTotalElements());
        mt.setPages(pSkill.getTotalPages());

        result.setMeta(mt);

        result.setResult(pSkill.getContent());
        return result;
    }
    public List<Skill> findByIdIn(List<Long> listId) {
        return this.skillRepository.findByIdIn(listId);
    }
    public void delete(long id) {
        Skill currentSkill = this.getById(id);
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        currentSkill.getSubscribers().forEach(sub -> sub.getSkills().remove(currentSkill));
        this.skillRepository.delete(currentSkill);

    }

}
