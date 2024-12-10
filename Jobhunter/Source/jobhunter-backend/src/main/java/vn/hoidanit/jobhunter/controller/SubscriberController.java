package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SercurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/subscribers")
public class SubscriberController {
    private final SubscriberService subscriberService;

    private final UserService userService;

    private final SkillService skillService;

    public SubscriberController(SubscriberService subscriberService, UserService userService, SkillService skillService) {
        this.subscriberService = subscriberService;
        this.userService = userService;
        this.skillService = skillService;
    }


    @PostMapping("")
    @ApiMessage("create a subscriber")
    public ResponseEntity<Subscriber> handleCreate(@RequestBody Subscriber dto) throws IdInvalidException {
        if(dto.getEmail() != null) {
            boolean isExistSubscriber = this.subscriberService.isEmailExist(dto.getEmail());
            if(isExistSubscriber) {
                throw new IdInvalidException("Email is exist!");
            }
        }
        Subscriber newSubscriber = this.subscriberService.save(dto);
        return new ResponseEntity<>(newSubscriber,newSubscriber != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

    }

    @PutMapping("")
    @ApiMessage("update a subscriber")
    public ResponseEntity<Subscriber> handleUpdate(@RequestBody Subscriber dto) throws IdInvalidException {
        Optional<Subscriber> opSubscriber = this.subscriberService.fetchById(dto.getId());
        if(opSubscriber.isEmpty()) {
            throw  new IdInvalidException("Subscriber không tồn tại");
        }


        Subscriber subscriber = this.subscriberService.update(dto,opSubscriber.get());
        return new ResponseEntity<>(subscriber,subscriber != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

    }
    @GetMapping("/{id}")
    @ApiMessage("get a subscriber")
    public ResponseEntity<Subscriber> fetchASubscriber(@RequestParam Long id) throws IdInvalidException {
        Optional<Subscriber> opSubscriber = this.subscriberService.fetchById(id);
        if(opSubscriber.isEmpty()) {
            throw new IdInvalidException("Subscriber is not found!");
        }
        return new ResponseEntity<>(opSubscriber.get(),opSubscriber.isPresent() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @GetMapping("")
    @ApiMessage("Fetch all subscriber")
    public ResponseEntity<ResultPaginationDTO> fetchAllSubscriber(@Filter Specification<Subscriber> spec, Pageable pageable) {
        ResultPaginationDTO result = this.subscriberService.paging(spec,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @DeleteMapping("/{id}")
    @ApiMessage("Delete a subscriber")
    public ResponseEntity<Void> deleteSubscriber(@RequestParam Long id) throws IdInvalidException {
        Optional<Subscriber> opSubscriber = this.subscriberService.fetchById(id);
        if(opSubscriber.isEmpty()) {
            throw new IdInvalidException("Subscriber is not found!");
        }
        return ResponseEntity.ok(null);
    }

    @PostMapping("/skills")
    @ApiMessage("Get Subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        return  ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }



}
