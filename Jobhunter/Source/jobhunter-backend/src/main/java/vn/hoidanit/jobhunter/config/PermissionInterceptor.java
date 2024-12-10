package vn.hoidanit.jobhunter.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SercurityUtil;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;
import vn.hoidanit.jobhunter.util.error.PermissionException;

import java.util.List;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;
    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String path =(String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI =request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println("---> Pre Handler");
        System.out.println("path ="+path);
        System.out.println("requestURI ="+requestURI);
        System.out.println("httpMethod ="+httpMethod);

        String email = SercurityUtil.getCurrentUserLogin().isPresent() ? SercurityUtil.getCurrentUserLogin().get() : "";
        if(email != null && !email.isEmpty()) {
            User currentUser = userService.handleGetUserByUserName(email);
            if(currentUser != null) {
                Role role = currentUser.getRole();
                if(role != null) {
                    List<Permission> listPermission = role.getPermissions();
                    boolean isAlow = listPermission.stream().anyMatch(p -> p.getApiPath().equals(requestURI)
                            && p.getMethod().equals(httpMethod));
                    if(!isAlow) {
                        throw new PermissionException("Bạn không có quyền truy cập endpoint này!");
                    }
                }else {
                    throw new PermissionException("Bạn không có quyền truy cập endpoint này!");
                }
            }
        }
        return true;


    }
}
