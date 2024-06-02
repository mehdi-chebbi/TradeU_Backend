package com.twd.SpringSecurityJWT.scheduler;

import com.twd.SpringSecurityJWT.entity.Bien;
import com.twd.SpringSecurityJWT.entity.Sondage;
import com.twd.SpringSecurityJWT.service.IBienService;
import com.twd.SpringSecurityJWT.service.IBienServiceImp;
import com.twd.SpringSecurityJWT.service.ISondageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
@Slf4j
@Component
public class BadFeedCountScheduler {


    @Autowired
    private IBienServiceImp bienService;
    @Autowired

    private ISondageService sondageService;

    // Scheduled task to check and update the autorise attribute of Biens
    @Scheduled(fixedRate = 5000) // Run every 5 seconds
    public void updateAutoriseAttribute() {
        try {
            // Get all Biens with badfeedcount greater than five
            List<Bien> biensWithHighBadFeedCount = bienService.getBiensWithHighBadFeedCount();

            if (biensWithHighBadFeedCount.isEmpty()) {
                // Log a message when no Biens have exceeded the 5 badfeedcount
                log.info("No Biens have exceeded the 5 badfeedcount.");
            } else {
                // Update autorise attribute for each Bien
                for (Bien bien : biensWithHighBadFeedCount) {
                    bien.setAutorise(false);
                    bienService.updatebien(bien);
                }

                // Log success message
                log.info("Autorise attribute updated successfully for {} Biens.", biensWithHighBadFeedCount.size());
            }
        } catch (Exception e) {
            // Log error message
            log.error("Failed to update autorise attribute: {}", e.getMessage());
        }
    }
    @Scheduled(fixedRate = 60000) // Runs every minute, adjust as needed
    public void checkSondageEndDate() {
        try {
            Date currentDate = new Date();
            List<Sondage> sondages = sondageService.retrieveAllSondage();

            boolean sondageEnded = false;

            for (Sondage sondage : sondages) {
                Date endDate = sondage.getEndDate();
                if (endDate != null && currentDate.after(endDate)) {
                    sondage.setActive(false);
                    sondageService.addSondage(sondage);
                    sondageEnded = true;
                }
            }

            if (!sondageEnded) {
                log.info("No sondages have ended.");
            }
        } catch (Exception e) {
            log.error("An error occurred while processing sondages whose end date has passed.");
            e.printStackTrace();
        }
    }
    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}