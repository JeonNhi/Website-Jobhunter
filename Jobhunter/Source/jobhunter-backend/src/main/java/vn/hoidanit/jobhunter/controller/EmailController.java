package vn.hoidanit.jobhunter.controller;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.service.EmailService;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }


//    @GetMapping("/email")
//    @ApiMessage("Send a email")
//    @Scheduled(cron = "*/30 * * * * *")
//    @Transactional
    public void sendEmailScheduled() {
        try {
            this.subscriberService.sendSubscribersEmailJobs();
            System.out.println("Email đã được gửi thành công!");
        } catch (Exception e) {
            // Log lỗi nếu có
            System.err.println("Lỗi khi gửi email định kỳ: " + e.getMessage());
        }
    }
//    public String sendSimpleEmail() {
//        this.subscriberService.sendSubscribersEmailJobs();
////        this.emailService.sendMailFromTemplateSync("20211841@eaut.edu.vn","Testing email with template","job");
//        return "send email ";
//    }

}
