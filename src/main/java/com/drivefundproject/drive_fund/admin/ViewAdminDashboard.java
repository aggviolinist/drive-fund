package com.drivefundproject.drive_fund.admin;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ViewAdminDashboard {
    @GetMapping
    public String get(){
        return "GET:: Admin View";
    }
    @PostMapping
    public String post(){
        return "POST:: Admin Post";
    }
    @PutMapping
    public String put(){
        return "PUT:: Admin Put";
    }
    @DeleteMapping
    public String delete(){
        return "DELETE:: Admin Delete";
    }
    
}
