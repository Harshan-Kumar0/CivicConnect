package org.civicconnect.portal.controller;

import org.civicconnect.portal.model.Complaint;
import org.civicconnect.portal.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.civicconnect.portal.util.SentimentAnalyzer;

@Controller
public class PageController {

    @Autowired
    private ComplaintRepository complaintRepository;

    // 🏠 Changed from "/" → "/index" to avoid clash with splash screen
    @GetMapping("/index")
    public String home() {
        return "index";  // loads index.html
    }

//    @PostMapping("/submit")
//    public String submitComplaint(@ModelAttribute Complaint complaint) {
//        complaint.setStatus("Pending");
//        complaint.setSentiment(SentimentAnalyzer.analyze(complaint.getDescription()));
//        complaintRepository.save(complaint);
//        return "redirect:/view";
//    }

    @GetMapping("/view")
    public String viewComplaints(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "view";
    }
}
