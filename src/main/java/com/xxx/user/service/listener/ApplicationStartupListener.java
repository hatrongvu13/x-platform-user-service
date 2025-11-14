package com.xxx.user.service.listener;

import com.xxx.user.service.services.role.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener {

    private final RoleService roleService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("ApplicationStartupListener init Role and permission");
        roleService.initRole();
    }
}
