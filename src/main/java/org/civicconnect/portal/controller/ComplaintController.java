package org.civicconnect.portal.controller;

import org.civicconnect.portal.model.Complaint;
import org.civicconnect.portal.repository.ComplaintRepository;
import org.civicconnect.portal.service.EmailService;
import org.civicconnect.portal.util.SentimentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ComplaintController {

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private EmailService emailService;

    /**
     * ✅ Handles complaint submission from HTML form
     * Uses @ModelAttribute to bind form fields to the Complaint entity
     */
    @PostMapping("/submit")
    public String submitComplaint(@ModelAttribute Complaint complaint, Model model) {

        // Set default status
        complaint.setStatus("Pending");

        // Generate unique Ticket ID
        String ticket = "CC-" + System.currentTimeMillis();
        complaint.setTicketId(ticket);

        // Run Sentiment Analysis
        String sentiment = SentimentAnalyzer.analyze(complaint.getDescription());
        complaint.setSentiment(sentiment);

        // Save complaint to DB
        Complaint savedComplaint = complaintRepository.save(complaint);
        System.out.println("✅ Complaint saved with Ticket ID: " + ticket);

        // ✅ Attempt to send confirmation email if provided
        if (complaint.getCitizenEmail() != null && !complaint.getCitizenEmail().trim().isEmpty()) {
            try {
                System.out.println("📧 Attempting to send confirmation email...");

                emailService.sendEmail(
                        complaint.getCitizenEmail(),
                        "CivicConnect - Complaint Registered Successfully",
                        String.format("""
                                Dear %s,

                                Thank you for reaching out to CivicConnect. Your complaint has been successfully registered.

                                🧾 Ticket ID: %s
                                🏷️ Category: %s
                                📋 Status: %s
                                

                                You can track the progress of your complaint anytime using this Ticket ID.

                                Regards,
                                CivicConnect Team
                                """,
                                complaint.getCitizenName(),
                                ticket,
                                complaint.getCategory(),
                                complaint.getStatus()
                        )
                );
                System.out.println("✅ Email sent successfully to: " + complaint.getCitizenEmail());
            } catch (Exception e) {
                System.err.println("❌ Failed to send email: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ No citizen email provided — skipping mail send.");
        }

        // Pass ticket info to success page
        model.addAttribute("ticketId", ticket);
        model.addAttribute("citizenName", complaint.getCitizenName());
        return "success"; // render success.html
    }

    /**
     * ✅ API: Get all complaints (for admin/citizen view)
     */
    @GetMapping("/api/complaints")
    @ResponseBody
    public Iterable<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    /**
     * ✅ API: Get a single complaint by ID
     */
    @GetMapping("/api/complaints/{id}")
    @ResponseBody
    public Complaint getComplaintById(@PathVariable Long id) {
        return complaintRepository.findById(id).orElse(null);
    }

    /**
     * ✅ API: Update complaint status (admin)
     */
    @PutMapping("/api/complaints/{id}/status")
    @ResponseBody
    public Complaint updateStatus(@PathVariable Long id, @RequestParam String status) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setStatus(status);
            complaintRepository.save(complaint);
        }
        return complaint;
    }

    /**
     * ✅ API: Delete complaint (admin)
     */
    @DeleteMapping("/api/complaints/{id}")
    @ResponseBody
    public void deleteComplaint(@PathVariable Long id) {
        complaintRepository.deleteById(id);
    }

    /**
     * ✅ API: Find complaint by Ticket ID
     */
    @GetMapping("/api/complaints/ticket/{ticketId}")
    @ResponseBody
    public Complaint getComplaintByTicket(@PathVariable String ticketId) {
        return complaintRepository.findByTicketId(ticketId);
    }
}
