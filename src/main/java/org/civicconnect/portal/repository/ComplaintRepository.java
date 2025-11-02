package org.civicconnect.portal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.civicconnect.portal.model.Complaint;
import org.springframework.stereotype.Repository;


public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    // ✅ Find complaint by ticket ID
    Complaint findByTicketId(String ticketId);
}
