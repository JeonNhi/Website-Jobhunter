package vn.hoidanit.jobhunter.domain.response;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Skill;
import vn.hoidanit.jobhunter.util.constant.LevelEnum;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class ResUpdateJobDTO {
    private long id;
    private String name;
    private String location;
    private LevelEnum level;
    private double salary;
    private int quantity;
    private Instant startDate;
    private Instant endDate;

    private boolean active;

    private List<String> skills;
    private Instant updatedAt;
    private String updatedBy;

}
