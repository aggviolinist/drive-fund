package com.drivefundproject.drive_fund.savingsplan;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
/////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////Experimental websocket controller//////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////
@Controller
@CrossOrigin("*")
@RequestMapping("/web/sockets/old")
public class NotificationController {

    @GetMapping("/messages")
    public String getTemplate(Model model){
        return "index";
    }
    
    @MessageMapping("/sendMessaging")
    @SendTo("/topic/notifications")
    public String sendMessage(String message){
        System.out.println("Message" + message);
        return message;
    }
}
