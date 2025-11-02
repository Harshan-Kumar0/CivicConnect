package org.civicconnect.portal.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Only splash page handles "/"
    @GetMapping("/")
    public String splashPage() {
        return "splash";
    }
}
