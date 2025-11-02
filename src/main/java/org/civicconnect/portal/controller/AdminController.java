package org.civicconnect.portal.controller;

import org.civicconnect.portal.model.Complaint;
import org.civicconnect.portal.repository.ComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "admin";
    }

    @PostMapping("/resolve/{id}")
    public String resolveComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setStatus("Resolved");
            complaintRepository.save(complaint);
        }
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String deleteComplaint(@PathVariable Long id) {
        complaintRepository.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/stats")
    public String viewStats(Model model) {
        long total = complaintRepository.count();
        long resolved = complaintRepository.findAll().stream()
                .filter(c -> "Resolved".equalsIgnoreCase(c.getStatus()))
                .count();
        long pending = total - resolved;

        // Category count (for chart)
        var categoryMap = complaintRepository.findAll().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        c -> c.getCategory(),
                        java.util.stream.Collectors.counting()
                ));

        model.addAttribute("total", total);
        model.addAttribute("resolved", resolved);
        model.addAttribute("pending", pending);
        model.addAttribute("categoryMap", categoryMap);

        return "stats";
    }

    @GetMapping("/map")
    public String viewMap(Model model) {
        model.addAttribute("complaints", complaintRepository.findAll());
        return "map";
    }


}


