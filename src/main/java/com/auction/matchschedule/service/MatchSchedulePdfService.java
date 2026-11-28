package com.auction.matchschedule.service;

import java.time.LocalDate;
import java.util.UUID;

public interface MatchSchedulePdfService {

    // ==========================================
    // 1. GENERATE COMPLETE MATCH SCHEDULE PDF
    // ==========================================

    byte[] generateSchedulePdf(
            UUID scheduleId
    );

    // ==========================================
    // 2. GENERATE FILTERED MATCH SCHEDULE PDF
    // ==========================================

    byte[] generateSchedulePdf(
            UUID scheduleId,
            UUID tournamentTeamId,
            LocalDate matchDate
    );
}