package com.auction.matchschedule.service.impl;

import java.awt.Color;
import java.io.ByteArrayOutputStream;

import java.time.LocalDate;

import java.time.LocalTime;

import java.time.format.DateTimeFormatter;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.TreeMap;

import java.util.HashMap;

import java.util.List;

import java.util.Locale;

import java.util.Map;

import java.util.Objects;

import java.util.UUID;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.openpdf.text.Document;

import org.openpdf.text.Element;

import org.openpdf.text.Font;

import org.openpdf.text.FontFactory;

import org.openpdf.text.Image;

import org.openpdf.text.PageSize;

import org.openpdf.text.Paragraph;

import org.openpdf.text.Phrase;

import org.openpdf.text.pdf.PdfPCell;

import org.openpdf.text.pdf.PdfPTable;

import org.openpdf.text.pdf.PdfWriter;
import com.auction.matchschedule.util.pdf.MatchSchedulePdfFooter;

import com.auction.matchschedule.dto.response.MatchScheduleResponse;

import com.auction.matchschedule.dto.response.MatchScheduleFixtureResponse;

import com.auction.matchschedule.service.MatchScheduleService;

import com.auction.matchschedule.service.MatchSchedulePdfService;

import com.auction.matchschedule.util.pdf.PdfImageUtil;

import com.auction.matchschedule.util.pdf.PdfStyleUtil;

import com.auction.tournamentteam.dto.TournamentTeamResponse;

import com.auction.tournamentteam.service.TournamentTeamService;

import com.auction.team.dto.TeamResponse;

import com.auction.team.service.TeamService;

@Service

public class MatchSchedulePdfServiceImpl
        implements MatchSchedulePdfService {
    private static final Logger log =
            LoggerFactory.getLogger(
                    MatchSchedulePdfServiceImpl.class
            );
    // ==========================================
    // PDF CONFIGURATION
    // ==========================================
    private static final Color SUMMARY_NAVY = new Color(23, 37, 84);
    private static final Color SUMMARY_MUTED = new Color(71, 85, 105);
    private static final Color SUMMARY_BACKGROUND = new Color(241, 245, 249);

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy",
                    Locale.ENGLISH
            );
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern(
                    "hh:mm a",
                    Locale.ENGLISH
            );
    // ==========================================
    // DEPENDENCIES
    // ==========================================
    private final MatchScheduleService matchScheduleService;
    private final TournamentTeamService tournamentTeamService;
    private final TeamService teamService;
    private final PdfImageUtil pdfImageUtil;
    // ==========================================
    // CONSTRUCTOR
    // ==========================================
    public MatchSchedulePdfServiceImpl(
            MatchScheduleService matchScheduleService,
            TournamentTeamService tournamentTeamService,
            TeamService teamService,
            PdfImageUtil pdfImageUtil) {
        this.matchScheduleService = matchScheduleService;
        this.tournamentTeamService = tournamentTeamService;
        this.teamService = teamService;
        this.pdfImageUtil = pdfImageUtil;
    }
    // ==========================================
    // 1. GENERATE COMPLETE MATCH SCHEDULE PDF
    // ==========================================
    @Override
    public byte[] generateSchedulePdf(UUID scheduleId) {
        return generateSchedulePdf(
                scheduleId,
                null,
                null
        );
    }
    // ==========================================
    // 2. GENERATE FILTERED MATCH SCHEDULE PDF
    // ==========================================
    @Override
    public byte[] generateSchedulePdf(
            UUID scheduleId,
            UUID tournamentTeamId,
            LocalDate matchDate) {
        log.info(
                "Generating match schedule PDF. "
                + "ScheduleId={}, TournamentTeamId={}, MatchDate={}",
                scheduleId,
                tournamentTeamId,
                matchDate
        );
        // Retrieve existing match schedule
        MatchScheduleResponse schedule =
                matchScheduleService.getScheduleById(
                        scheduleId
                );
        // Validate and retrieve selected team
        TeamResponse selectedTeam =
                validateAndResolveSelectedTeam(
                        schedule,
                        tournamentTeamId
                );
        // Retrieve all generated fixtures
        List<MatchScheduleFixtureResponse> allFixtures =
                matchScheduleService.getFixtures(
                        scheduleId
                );
        // Apply optional filters
        List<MatchScheduleFixtureResponse> filteredFixtures =
                filterFixtures(
                        allFixtures,
                        tournamentTeamId,
                        matchDate
                );
        log.debug(
                "PDF fixture filtering completed. "
                + "ScheduleId={}, TotalFixtures={}, "
                + "FilteredFixtures={}",
                scheduleId,
                allFixtures.size(),
                filteredFixtures.size()
        );
        // Request-level image cache
        Map<String, Image> imageCache =
                new HashMap<>();
        // Initialize PDF document
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();
        Document document = new Document(
                PageSize.A4,
                PdfStyleUtil.PAGE_MARGIN_LEFT,
                PdfStyleUtil.PAGE_MARGIN_RIGHT,
                PdfStyleUtil.PAGE_MARGIN_TOP,
                Math.max(PdfStyleUtil.PAGE_MARGIN_BOTTOM, 64f)
        );
        try {
            PdfWriter writer = PdfWriter.getInstance(
                    document,
                    outputStream
            );
            Image tournamentWatermark = pdfImageUtil.loadImage(
                    schedule.getTournamentLogoUrl(), 260f, 260f, imageCache);
            writer.setPageEvent(new MatchSchedulePdfFooter(
                    resolveTournamentName(schedule), tournamentWatermark
            ));
            // PDF metadata
            document.addTitle(
                    resolvePdfTitle(
                            schedule,
                            selectedTeam,
                            matchDate
                    )
            );
            document.addSubject(
                    "Tournament Match Schedule"
            );
            document.addCreator(
                    "AI Cricket Auction Platform"
            );
            document.open();
            // Professional tournament and team branding
            if (selectedTeam != null) {
                addCompactTeamHeader(document, schedule, selectedTeam, matchDate, imageCache);
            } else {
                addTournamentHeader(document, schedule, selectedTeam, matchDate, imageCache);
            }
            addDocumentStatus(document, schedule);
            // Overall tournament information
            if (selectedTeam != null) {
                addCompactTeamSummary(document, schedule, filteredFixtures, matchDate, tournamentTeamId);
                addCompactTournamentOverview(document, schedule, allFixtures.size(), matchDate);
            } else {
                addScheduleInformation(document, schedule, allFixtures);
                addFilterInformation(document, filteredFixtures, selectedTeam, matchDate);
            }
            // Showcase only for the complete schedule.
            if (tournamentTeamId == null && matchDate == null) {
                addParticipatingTeams(document, allFixtures, imageCache);
                addPlayoffRoadmap(document, allFixtures);
            }
            // Filtered match fixtures
            addMatchFixtures(
                    document,
                    allFixtures,
                    filteredFixtures,
                    imageCache, tournamentTeamId
            );
            document.close();
            byte[] pdfBytes =
                    outputStream.toByteArray();
            log.info(
                    "Match schedule PDF generated successfully. "
                    + "ScheduleId={}, TotalFixtures={}, "
                    + "FilteredFixtures={}, PdfSize={}",
                    scheduleId,
                    allFixtures.size(),
                    filteredFixtures.size(),
                    pdfBytes.length
            );
            return pdfBytes;
        } catch (Exception ex) {
            log.error(
                    "Failed to generate match schedule PDF. "
                    + "ScheduleId={}",
                    scheduleId,
                    ex
            );
            throw new IllegalStateException(
                    "Unable to generate match schedule PDF.",
                    ex
            );
        } finally {
            if (document.isOpen()) {
                try {
                    document.close();
                } catch (Exception closeException) {
                    log.warn(
                            "Failed to close PDF document. "
                            + "ScheduleId={}",
                            scheduleId,
                            closeException
                    );
                }
            }
        }
    }
    // ==========================================
    // 3. VALIDATE AND RESOLVE SELECTED TEAM
    // ==========================================
    private TeamResponse validateAndResolveSelectedTeam(
            MatchScheduleResponse schedule,
            UUID tournamentTeamId) {
        if (tournamentTeamId == null) {
            return null;
        }
        TournamentTeamResponse tournamentTeam =
                tournamentTeamService.getTournamentTeam(
                        tournamentTeamId
                );
        if (!Objects.equals(
                schedule.getTournamentId(),
                tournamentTeam.getTournamentId())) {
            log.warn(
                    "Invalid tournament team selected for PDF. "
                    + "ScheduleTournamentId={}, "
                    + "SelectedTournamentTeamId={}, "
                    + "SelectedTeamTournamentId={}",
                    schedule.getTournamentId(),
                    tournamentTeamId,
                    tournamentTeam.getTournamentId()
            );
            throw new IllegalArgumentException(
                    "Selected team does not belong "
                    + "to this tournament."
            );
        }
        UUID teamId = tournamentTeam.getTeamId();
        if (teamId == null) {
            throw new IllegalStateException(
                    "Selected tournament team "
                    + "does not have linked team information."
            );
        }
        TeamResponse selectedTeam =
                teamService.getTeam(teamId);
        log.debug(
                "Tournament team validated successfully. "
                + "TournamentTeamId={}, TeamId={}, TeamName={}",
                tournamentTeamId,
                teamId,
                selectedTeam.getName()
        );
        return selectedTeam;
    }
    // ==========================================
    // 4. FILTER MATCH FIXTURES
    // ==========================================
    private List<MatchScheduleFixtureResponse> filterFixtures(
            List<MatchScheduleFixtureResponse> fixtures,
            UUID tournamentTeamId,
            LocalDate matchDate) {
        return fixtures.stream()
                .filter(fixture ->
                        tournamentTeamId == null
                        || tournamentTeamId.equals(
                                fixture.getHomeTournamentTeamId()
                        )
                        || tournamentTeamId.equals(
                                fixture.getAwayTournamentTeamId()
                        )
                )
                .filter(fixture ->
                        matchDate == null
                        || matchDate.equals(
                                fixture.getMatchDate()
                        )
                )
                .toList();
    }
    // ==========================================
    // 5. RESOLVE PDF METADATA TITLE
    // ==========================================
    private String resolvePdfTitle(
            MatchScheduleResponse schedule,
            TeamResponse selectedTeam,
            LocalDate matchDate) {
        StringBuilder title = new StringBuilder(
                resolveTournamentName(schedule)
        );
        if (selectedTeam != null) {
            title.append(" - ")
                    .append(
                            resolveSelectedTeamName(
                                    selectedTeam
                            )
                    );
        }
        title.append(" - Match Schedule");
        if (matchDate != null) {
            title.append(" - ")
                    .append(formatDate(matchDate));
        }
        return title.toString();
    }
    // ==========================================
    // 6. PROFESSIONAL TOURNAMENT HEADER
    // ==========================================
    private void addDocumentStatus(Document document, MatchScheduleResponse schedule)
            throws Exception {
        String status = safeText(schedule.getStatus()).toUpperCase(Locale.ROOT);
        String message;
        Color background;
        Color foreground;
        switch (status) {
            case "PUBLISHED" -> {
                message = "Official published schedule";
                background = new Color(220, 252, 231);
                foreground = new Color(22, 101, 52);
            }
            case "CANCELLED" -> {
                message = "Cancelled - schedule no longer valid";
                background = new Color(254, 226, 226);
                foreground = new Color(153, 27, 27);
            }
            default -> {
                message = "Not published - subject to change";
                background = PdfStyleUtil.LIGHT_GOLD;
                foreground = PdfStyleUtil.DARK_GOLD_TEXT;
            }
        }
        PdfPTable strip = new PdfPTable(2);
        strip.setWidthPercentage(100);
        strip.setWidths(new float[] { 3f, 2f });
        strip.setKeepTogether(true);
        strip.setSpacingAfter(5f);
        PdfPCell statusCell = PdfStyleUtil.createBaseCell(background);
        statusCell.setPadding(4f);
        statusCell.setBorderColor(PdfStyleUtil.PREMIUM_GOLD);
        statusCell.addElement(PdfStyleUtil.centeredParagraph(
                status.replace('_', ' '), PdfStyleUtil.boldFont(10f, foreground)));
        statusCell.addElement(PdfStyleUtil.centeredParagraph(
                message, PdfStyleUtil.regularFont(8f, foreground)));
        strip.addCell(statusCell);
        PdfPCell versionCell = PdfStyleUtil.createBrandingCell();
        versionCell.setPadding(4f);
        versionCell.addElement(PdfStyleUtil.centeredParagraph(
                "Schedule Version: " + safeText(schedule.getGenerationNumber()),
                PdfStyleUtil.boldFont(10f, PdfStyleUtil.GOLD_HIGHLIGHT)));
        versionCell.addElement(PdfStyleUtil.centeredParagraph(
                "Fixture generation number", PdfStyleUtil.regularFont(8f, PdfStyleUtil.WHITE)));
        strip.addCell(versionCell);
        document.add(strip);
    }

    private void addCompactTeamHeader(Document document, MatchScheduleResponse schedule,
            TeamResponse team, LocalDate matchDate, Map<String, Image> imageCache)
            throws Exception {
        PdfPTable header = new PdfPTable(3);
        header.setWidthPercentage(100);
        header.setWidths(new float[] {1.2f, 5f, 1.2f});
        header.setKeepTogether(true);
        header.setSpacingAfter(5f);
        header.addCell(createCompactBrandLogo(schedule.getTournamentLogoUrl(),
                "TOURNAMENT", imageCache));
        PdfPCell title = PdfStyleUtil.createBrandingCell();
        title.setPadding(6f);
        title.addElement(PdfStyleUtil.centeredParagraph(resolveTournamentName(schedule),
                PdfStyleUtil.boldFont(13f, PdfStyleUtil.GOLD_HIGHLIGHT)));
        title.addElement(PdfStyleUtil.centeredParagraph(resolveSelectedTeamName(team),
                PdfStyleUtil.boldFont(18f, PdfStyleUtil.WHITE)));
        title.addElement(PdfStyleUtil.centeredParagraph(resolveScheduleSubtitle(team, matchDate),
                PdfStyleUtil.boldFont(9f, PdfStyleUtil.LIGHT_GOLD)));
        addHeaderDateInformation(title, schedule, matchDate);
        header.addCell(title);
        header.addCell(createCompactBrandLogo(team.getLogoUrl(),
                hasText(team.getShortName()) ? team.getShortName() : "TEAM", imageCache));
        document.add(header);
    }

    private PdfPCell createCompactBrandLogo(String url, String fallback,
            Map<String, Image> imageCache) {
        PdfPCell cell = PdfStyleUtil.createBrandingCell();
        cell.setPadding(5f);
        Image logo = pdfImageUtil.loadImage(url, 58f, 58f, imageCache);
        if (logo != null) {
            logo.setAlignment(Element.ALIGN_CENTER);
            cell.addElement(logo);
        } else {
            cell.addElement(PdfStyleUtil.centeredParagraph(fallback,
                    PdfStyleUtil.boldFont(9f, PdfStyleUtil.GOLD_HIGHLIGHT)));
        }
        return cell;
    }

    private void addCompactTeamSummary(Document document, MatchScheduleResponse schedule,
            List<MatchScheduleFixtureResponse> fixtures, LocalDate matchDate,
            UUID tournamentTeamId) throws Exception {
        PdfPTable summary = new PdfPTable(12);
        summary.setWidthPercentage(100);
        summary.setKeepTogether(true);
        summary.setSpacingAfter(5f);
        summary.addCell(PdfStyleUtil.createPremiumSectionHeader("TEAM MATCH SUMMARY", 12));
        long league = fixtures.stream().filter(f -> "LEAGUE".equalsIgnoreCase(f.getMatchStage())).count();
        long playoff = fixtures.stream().filter(f -> isPlayoffStage(f.getMatchStage())).count();
        long playingDays = fixtures.stream()
                .map(MatchScheduleFixtureResponse::getMatchDate)
                .filter(date -> date != null)
                .distinct().count();
        addTeamSummaryCard(summary, "League Matches", league, false);
        addTeamSummaryCard(summary, "Playoff Matches", playoff, false);
        addTeamSummaryCard(summary, "Playing Days", playingDays, false);
        addTeamSummaryCard(summary, "Total Matches", fixtures.size(), true);
        if (fixtures.size() - league - playoff > 0) {
            addTeamSummaryNote(summary, "Other / Unclassified Matches: " + (fixtures.size() - league - playoff));
        }
        Comparator<MatchScheduleFixtureResponse> chronological = Comparator
                .comparing(MatchScheduleFixtureResponse::getMatchDate)
                .thenComparing(MatchScheduleFixtureResponse::getStartTime);
        List<MatchScheduleFixtureResponse> timed = fixtures.stream()
                .filter(f -> f.getMatchDate() != null && f.getStartTime() != null)
                .sorted(chronological).toList();
        summary.addCell(createTeamTimingCard("FIRST MATCH", timed.isEmpty() ? null : timed.getFirst(),
                tournamentTeamId, fixtures.isEmpty()));
        summary.addCell(createTeamTimingCard("LAST MATCH", timed.isEmpty() ? null : timed.getLast(),
                tournamentTeamId, fixtures.isEmpty()));
        Map<LocalDate, Long> daily = new TreeMap<>();
        long undated = 0;
        for (MatchScheduleFixtureResponse fixture : fixtures) {
            if (fixture.getMatchDate() == null) undated++;
            else daily.merge(fixture.getMatchDate(), 1L, Long::sum);
        }
        summary.addCell(PdfStyleUtil.createPremiumSectionHeader("DAILY MATCH DISTRIBUTION", 12));
        for (Map.Entry<LocalDate, Long> entry : daily.entrySet()) {
            addDailyDistributionRow(summary,
                    entry.getKey().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH)),
                    entry.getValue(), fixtures.size());
        }
        if (undated > 0) {
            addDailyDistributionRow(summary, "Date TBD", undated, fixtures.size());
        }
        if (fixtures.isEmpty()) {
            addTeamSummaryNote(summary, "No matches scheduled for the selected filter.");
        }
        document.add(summary);
    }

    private void addCompactTournamentOverview(Document document, MatchScheduleResponse schedule,
            int totalFixtures, LocalDate matchDate) throws Exception {
        PdfPTable overview = new PdfPTable(12);
        overview.setWidthPercentage(100);
        overview.setKeepTogether(true);
        overview.setSpacingAfter(5f);
        overview.addCell(PdfStyleUtil.createPremiumSectionHeader("TOURNAMENT OVERVIEW", 12));
        addTournamentOverviewCard(overview, "Teams", schedule.getTotalTeams(), false);
        addTournamentOverviewCard(overview, "League Matches", schedule.getTotalLeagueMatches(), false);
        addTournamentOverviewCard(overview, "Playoff Matches", schedule.getTotalPlayoffMatches(), false);
        addTournamentOverviewCard(overview, "Total Fixtures", totalFixtures, true);
        addOverviewInformation(overview, "Schedule Name", safeText(schedule.getName()));
        addOverviewInformation(overview, "Tournament Period",
                formatDate(schedule.getStartDate()) + " - " + formatDate(schedule.getEndDate()));
        addOverviewInformation(overview, "League Format", formatLabel(schedule.getLeagueFormat()));
        addOverviewInformation(overview, "Playoff Format", formatLabel(schedule.getPlayoffFormat()));
        addTeamSummaryNote(overview, "Selected Date: " + (matchDate == null ? "All Dates" : formatDate(matchDate))
                + " | Playoff fixtures included only for confirmed participants.");
        document.add(overview);
    }

    private void addTournamentOverviewCard(PdfPTable table, String label, Object value, boolean highlighted) {
        PdfPCell cell = createSummaryCell(label, value, highlighted);
        cell.setColspan(3);
        cell.setPadding(3f);
        table.addCell(cell);
    }

    private void addOverviewInformation(PdfPTable table, String label, String value) {
        PdfPCell cell = createInformationCell(label, value);
        cell.setColspan(6);
        cell.setPadding(3f);
        table.addCell(cell);
    }

    private void addDailyDistributionRow(PdfPTable table, String dateLabel,
            long count, long total) throws Exception {
        PdfPCell date = PdfStyleUtil.createBaseCell(PdfStyleUtil.PREMIUM_IVORY);
        date.setColspan(5);
        date.setPadding(3f);
        date.addElement(new Paragraph(dateLabel,
                PdfStyleUtil.boldFont(9f, PdfStyleUtil.PRIMARY_NAVY)));
        table.addCell(date);

        PdfPCell chart = PdfStyleUtil.createBaseCell(PdfStyleUtil.PREMIUM_IVORY);
        chart.setColspan(5);
        chart.setPadding(4f);
        double fraction = total > 0 ? Math.min(1d, Math.max(0d, (double) count / total)) : 0d;
        PdfPTable bar = new PdfPTable(fraction > 0d && fraction < 1d ? 2 : 1);
        bar.setWidthPercentage(100f);
        if (fraction > 0d && fraction < 1d) {
            bar.setWidths(new float[] {(float) fraction, (float) (1d - fraction)});
            bar.addCell(createDistributionBarSegment(PdfStyleUtil.PREMIUM_GOLD));
            bar.addCell(createDistributionBarSegment(PdfStyleUtil.CHAMPAGNE_BORDER));
        } else {
            bar.addCell(createDistributionBarSegment(fraction == 1d
                    ? PdfStyleUtil.PREMIUM_GOLD : PdfStyleUtil.CHAMPAGNE_BORDER));
        }
        chart.addElement(bar);
        chart.addElement(PdfStyleUtil.centeredParagraph(
                String.format(Locale.ENGLISH, "%.1f%%", fraction * 100d), PdfStyleUtil.noteFont()));
        table.addCell(chart);

        PdfPCell matches = PdfStyleUtil.createBaseCell(PdfStyleUtil.LIGHT_GOLD);
        matches.setColspan(2);
        matches.setPadding(3f);
        matches.addElement(PdfStyleUtil.centeredParagraph(Long.toString(count),
                PdfStyleUtil.boldFont(12f, PdfStyleUtil.PRIMARY_NAVY)));
        matches.addElement(PdfStyleUtil.centeredParagraph(count == 1 ? "Match" : "Matches",
                PdfStyleUtil.noteFont()));
        table.addCell(matches);
    }

    private PdfPCell createDistributionBarSegment(Color color) {
        PdfPCell segment = PdfStyleUtil.createBorderlessCell(color);
        segment.setPadding(0f);
        segment.setFixedHeight(7f);
        return segment;
    }

    private PdfPCell createTeamTimingCard(String label, MatchScheduleFixtureResponse fixture,
            UUID tournamentTeamId, boolean empty) {
        PdfPCell cell = PdfStyleUtil.createBaseCell(PdfStyleUtil.PREMIUM_IVORY);
        cell.setColspan(6);
        cell.setPadding(4f);
        cell.setBorderColor(PdfStyleUtil.PREMIUM_GOLD);
        cell.addElement(PdfStyleUtil.centeredParagraph(label,
                PdfStyleUtil.boldFont(9f, PdfStyleUtil.DARK_GOLD_TEXT)));
        if (fixture == null) {
            cell.addElement(PdfStyleUtil.centeredParagraph(empty ? "No matches" : "Timing TBD",
                    PdfStyleUtil.boldFont(13f, PdfStyleUtil.PRIMARY_NAVY)));
            return cell;
        }
        boolean home = tournamentTeamId.equals(fixture.getHomeTournamentTeamId());
        String opponent = home
                ? resolveTeamDisplayName(fixture.getAwayTeamName(), fixture.getAwayTeamShortName(), fixture.getAwayTeamSource())
                : resolveTeamDisplayName(fixture.getHomeTeamName(), fixture.getHomeTeamShortName(), fixture.getHomeTeamSource());
        cell.addElement(PdfStyleUtil.centeredParagraph("Match " + safeText(fixture.getMatchNumber())
                + " | " + formatDate(fixture.getMatchDate()), PdfStyleUtil.noteFont()));
        cell.addElement(PdfStyleUtil.centeredParagraph(formatTime(fixture.getStartTime()),
                PdfStyleUtil.boldFont(16f, PdfStyleUtil.PRIMARY_NAVY)));
        cell.addElement(PdfStyleUtil.centeredParagraph("vs " + opponent,
                PdfStyleUtil.boldFont(9f, PdfStyleUtil.PRIMARY_NAVY)));
        return cell;
    }

    private void addTeamSummaryCard(PdfPTable table, String label,
            long value, boolean highlighted) {
        PdfPCell cell = createSummaryCell(label, value, highlighted);
        cell.setColspan(3);
        cell.setPadding(4f);
        table.addCell(cell);
    }

    private void addTeamSummaryNote(PdfPTable table, String text) {
        PdfPCell cell = PdfStyleUtil.createBaseCell(PdfStyleUtil.PREMIUM_IVORY);
        cell.setColspan(12);
        cell.setPadding(4f);
        cell.setBorderColor(PdfStyleUtil.CHAMPAGNE_BORDER);
        Paragraph note = new Paragraph(text, PdfStyleUtil.noteFont());
        note.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(note);
        table.addCell(cell);
    }

    private void addTournamentHeader(
            Document document,
            MatchScheduleResponse schedule,
            TeamResponse selectedTeam,
            LocalDate matchDate,
            Map<String, Image> imageCache)
            throws Exception {
        // Create full-width branding table
        PdfPTable brandingTable = new PdfPTable(1);
        brandingTable.setWidthPercentage(100);
        brandingTable.setSpacingAfter(
                PdfStyleUtil.MEDIUM_SPACING
        );
        brandingTable.setKeepTogether(true);
        // Navy-blue background with gold border
        PdfPCell brandingCell =
                PdfStyleUtil.createBrandingCell();
        brandingCell.setPadding(8f);
        brandingCell.setBorderWidth(0.75f);
        brandingCell.setBorderColor(PdfStyleUtil.GOLD_HIGHLIGHT);
        // Add tournament logo
        addTournamentLogo(
                brandingCell,
                schedule,
                imageCache
        );
        // Add tournament name
        Paragraph tournamentTitle =
                PdfStyleUtil.centeredParagraph(
                        resolveTournamentName(schedule),
                        PdfStyleUtil.boldFont(22f, PdfStyleUtil.GOLD_HIGHLIGHT)
                );
        tournamentTitle.setSpacingAfter(
                PdfStyleUtil.SMALL_SPACING
        );
        brandingCell.addElement(
                tournamentTitle
        );
        // Add selected team branding when applicable
        if (selectedTeam != null) {
            addSelectedTeamBranding(
                    brandingCell,
                    selectedTeam,
                    imageCache
            );
        }
        // Lightweight vector accent, not a raster background.
        PdfPTable accent = new PdfPTable(1);
        accent.setWidthPercentage(72f);
        accent.setHorizontalAlignment(Element.ALIGN_CENTER);
        accent.addCell(PdfStyleUtil.createGoldAccentCell(1));
        brandingCell.addElement(accent);
        // Add dynamic schedule subtitle
        Paragraph subtitle =
                PdfStyleUtil.centeredParagraph(
                        resolveScheduleSubtitle(
                                selectedTeam,
                                matchDate
                        ),
                        PdfStyleUtil.boldFont(13f, PdfStyleUtil.WHITE)
                );
        subtitle.setSpacingBefore(
                PdfStyleUtil.SMALL_SPACING
        );
        subtitle.setSpacingAfter(
                PdfStyleUtil.SMALL_SPACING
        );
        brandingCell.addElement(subtitle);
        // Add schedule date information
        addHeaderDateInformation(
                brandingCell,
                schedule,
                matchDate
        );
        // Add branding cell to document
        PdfPTable innerTable = new PdfPTable(1);
        innerTable.setWidthPercentage(100);
        innerTable.addCell(brandingCell);
        PdfPCell outerFrame = PdfStyleUtil.createBrandingCell();
        outerFrame.setPadding(3f);
        outerFrame.addElement(innerTable);
        brandingTable.addCell(outerFrame);
        document.add(
                brandingTable
        );
    }
    // ==========================================
    // 7. ADD TOURNAMENT LOGO
    // ==========================================
    private void addTournamentLogo(
            PdfPCell brandingCell,
            MatchScheduleResponse schedule,
            Map<String, Image> imageCache) {
        Image tournamentLogo =
                pdfImageUtil.loadImage(
                        schedule.getTournamentLogoUrl(),
                        90f,
                        90f,
                        imageCache
                );
        if (tournamentLogo != null) {
            tournamentLogo.setAlignment(
                    Element.ALIGN_CENTER
            );
            tournamentLogo.setSpacingAfter(
                    PdfStyleUtil.SMALL_SPACING
            );
            brandingCell.addElement(
                    tournamentLogo
            );
        } else {
            Paragraph logoPlaceholder =
                    PdfStyleUtil.centeredParagraph(
                            "CRICKET TOURNAMENT",
                            PdfStyleUtil.boldFont(
                                    12,
                                    PdfStyleUtil.PREMIUM_GOLD
                            )
                    );
            logoPlaceholder.setSpacingAfter(
                    PdfStyleUtil.MEDIUM_SPACING
            );
            brandingCell.addElement(
                    logoPlaceholder
            );
        }
    }
    // ==========================================
    // 8. ADD SELECTED TEAM BRANDING
    // ==========================================
    private void addSelectedTeamBranding(
            PdfPCell brandingCell,
            TeamResponse selectedTeam,
            Map<String, Image> imageCache) {
        // Selected team logo
        Image selectedTeamLogo =
                pdfImageUtil.loadImage(
                        selectedTeam.getLogoUrl(),
                        PdfStyleUtil.SELECTED_TEAM_LOGO_SIZE,
                        PdfStyleUtil.SELECTED_TEAM_LOGO_SIZE,
                        imageCache
                );
        if (selectedTeamLogo != null) {
            selectedTeamLogo.setAlignment(
                    Element.ALIGN_CENTER
            );
            selectedTeamLogo.setSpacingBefore(
                    PdfStyleUtil.SMALL_SPACING
            );
            selectedTeamLogo.setSpacingAfter(
                    PdfStyleUtil.SMALL_SPACING
            );
            brandingCell.addElement(
                    selectedTeamLogo
            );
        } else {
            String placeholderText =
                    hasText(selectedTeam.getShortName())
                            ? selectedTeam.getShortName().trim()
                            : "TEAM";
            Paragraph placeholder =
                    PdfStyleUtil.centeredParagraph(
                            placeholderText,
                            PdfStyleUtil.boldFont(
                                    16,
                                    PdfStyleUtil.PREMIUM_GOLD
                            )
                    );
            placeholder.setSpacingBefore(
                    PdfStyleUtil.SMALL_SPACING
            );
            placeholder.setSpacingAfter(
                    PdfStyleUtil.SMALL_SPACING
            );
            brandingCell.addElement(
                    placeholder
            );
        }
        // Selected team name
        // White text for contrast on navy background
        Paragraph teamTitle =
                PdfStyleUtil.centeredParagraph(
                        resolveSelectedTeamName(
                                selectedTeam
                        ),
                        PdfStyleUtil.boldFont(
                                PdfStyleUtil.TEAM_TITLE_FONT_SIZE,
                                PdfStyleUtil.GOLD_HIGHLIGHT
                        )
                );
        teamTitle.setSpacingAfter(
                PdfStyleUtil.SMALL_SPACING
        );
        brandingCell.addElement(
                teamTitle
        );
    }
    // ==========================================
    // 9. RESOLVE SCHEDULE SUBTITLE
    // ==========================================
    private String resolveScheduleSubtitle(
            TeamResponse selectedTeam,
            LocalDate matchDate) {
        if (selectedTeam != null
                && matchDate != null) {
            return "TEAM MATCH SCHEDULE - SELECTED DATE";
        }
        if (selectedTeam != null) {
            return "TEAM MATCH SCHEDULE";
        }
        if (matchDate != null) {
            return "DATE-WISE MATCH SCHEDULE";
        }
        return "COMPLETE MATCH SCHEDULE";
    }
    // ==========================================
    // 10. ADD HEADER DATE INFORMATION
    // ==========================================
    private void addHeaderDateInformation(
            PdfPCell brandingCell,
            MatchScheduleResponse schedule,
            LocalDate matchDate) {
        String dateText;
        if (matchDate != null) {
            dateText = "Selected Date: "
                    + formatDate(matchDate);
        } else {
            LocalDate startDate =
                    schedule.getStartDate();
            LocalDate endDate =
                    schedule.getEndDate();
            if (startDate != null
                    && startDate.equals(endDate)) {
                dateText = "Schedule Date: "
                        + formatDate(startDate);
            } else {
                dateText = "Schedule Period: "
                        + formatDate(startDate)
                        + " - "
                        + formatDate(endDate);
            }
        }
        Paragraph dateParagraph =
                PdfStyleUtil.centeredParagraph(
                        dateText,
                        PdfStyleUtil.regularFont(
                                10,
                                PdfStyleUtil.LIGHT_GOLD
                        )
                );
        dateParagraph.setSpacingAfter(2f);
        brandingCell.addElement(
                dateParagraph
        );
    }
    // ==========================================
    // 11. ADD SCHEDULE INFORMATION
    // ==========================================
    private void addScheduleInformation(
            Document document,
            MatchScheduleResponse schedule,
            List<MatchScheduleFixtureResponse> fixtures)
            throws Exception {

        PdfPTable information = new PdfPTable(2);
        information.setWidthPercentage(100);
        information.setKeepTogether(true);
        information.setSpacingAfter(PdfStyleUtil.SMALL_SPACING);
        information.addCell(PdfStyleUtil.createPremiumSectionHeader(
                "TOURNAMENT SCHEDULE INFORMATION", 2));

        addInformationRow(information, "Schedule Name", safeText(schedule.getName()),
                "Status", safeText(schedule.getStatus()));
        addInformationRow(information, "Start Date", formatDate(schedule.getStartDate()),
                "End Date", formatDate(schedule.getEndDate()));
        addInformationRow(information, "League Format", formatLabel(schedule.getLeagueFormat()),
                "Playoff Format", formatLabel(schedule.getPlayoffFormat()));
        document.add(information);
        addTournamentSummary(document, schedule, fixtures);
    }

    private void addTournamentSummary(Document document,
            MatchScheduleResponse schedule,
            List<MatchScheduleFixtureResponse> fixtures) throws Exception {
        PdfPTable summary = new PdfPTable(4);
        summary.setWidthPercentage(100);
        summary.setKeepTogether(true);
        summary.setSpacingAfter(PdfStyleUtil.SMALL_SPACING);
        summary.addCell(PdfStyleUtil.createPremiumSectionHeader(
                "TOURNAMENT MATCH SUMMARY", 4));
        summary.addCell(createSummaryCell("Teams", schedule.getTotalTeams(), false));
        summary.addCell(createSummaryCell("League Matches", schedule.getTotalLeagueMatches(), false));
        summary.addCell(createSummaryCell("Playoff Matches", schedule.getTotalPlayoffMatches(), false));
        summary.addCell(createSummaryCell("Total Fixtures", fixtures.size(), true));
        document.add(summary);
    }

    // Use the generated fixture snapshot, not the current tournament roster.
    private record ShowcaseTeam(String name, String shortName, String logoUrl) {}

    private void addParticipatingTeams(Document document,
            List<MatchScheduleFixtureResponse> fixtures,
            Map<String, Image> imageCache) throws Exception {
        Map<UUID, ShowcaseTeam> teams = new HashMap<>();
        for (MatchScheduleFixtureResponse fixture : fixtures) {
            collectShowcaseTeam(teams, fixture.getHomeTournamentTeamId(),
                    fixture.getHomeTeamName(), fixture.getHomeTeamShortName(),
                    fixture.getHomeTeamLogoUrl());
            collectShowcaseTeam(teams, fixture.getAwayTournamentTeamId(),
                    fixture.getAwayTeamName(), fixture.getAwayTeamShortName(),
                    fixture.getAwayTeamLogoUrl());
        }
        if (teams.isEmpty()) {
            return;
        }
        List<ShowcaseTeam> ordered = new ArrayList<>(teams.values());
        ordered.sort(Comparator.comparing(ShowcaseTeam::name,
                String.CASE_INSENSITIVE_ORDER)
                .thenComparing(ShowcaseTeam::shortName));
        int columns = Math.min(6, ordered.size());
        PdfPTable showcase = new PdfPTable(columns);
        showcase.setWidthPercentage(100);
        showcase.setSpacingAfter(PdfStyleUtil.MEDIUM_SPACING);
        showcase.setKeepTogether(ordered.size() <= 6);
        showcase.addCell(PdfStyleUtil.createPremiumSectionHeader(
                "PARTICIPATING TEAMS", columns));
        PdfPCell subtitle = PdfStyleUtil.createBrandingCell();
        subtitle.setColspan(columns);
        subtitle.setPadding(3);
        subtitle.addElement(PdfStyleUtil.centeredParagraph(
                ordered.size() + " TEAMS - ONE CHAMPION",
                PdfStyleUtil.boldFont(10, PdfStyleUtil.LIGHT_GOLD)));
        showcase.addCell(subtitle);
        showcase.setHeaderRows(2);
        showcase.setSplitLate(true);
        for (ShowcaseTeam team : ordered) {
            PdfPCell cell = PdfStyleUtil.createBrandingCell();
            cell.setBackgroundColor(PdfStyleUtil.SECONDARY_NAVY);
            cell.setPadding(3);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            Image logo = pdfImageUtil.loadImage(team.logoUrl(), 45f, 45f, imageCache);
            if (logo != null) {
                logo.setAlignment(Element.ALIGN_CENTER);
                logo.setSpacingAfter(2);
                cell.addElement(logo);
            } else {
                Paragraph fallback = PdfStyleUtil.centeredParagraph(team.shortName(),
                        PdfStyleUtil.boldFont(12, PdfStyleUtil.LIGHT_GOLD));
                fallback.setSpacingBefore(8);
                fallback.setSpacingAfter(8);
                cell.addElement(fallback);
            }
            cell.addElement(PdfStyleUtil.centeredParagraph(team.name(),
                    PdfStyleUtil.boldFont(9, PdfStyleUtil.WHITE)));
            cell.addElement(PdfStyleUtil.centeredParagraph(team.shortName(),
                    PdfStyleUtil.regularFont(8, PdfStyleUtil.LIGHT_GOLD)));
            showcase.addCell(cell);
        }
        int remaining = (columns - ordered.size() % columns) % columns;
        if (remaining > 0) {
            PdfPCell filler = PdfStyleUtil.createBrandingCell();
            filler.setColspan(remaining);
            showcase.addCell(filler);
        }
        PdfPCell closing = PdfStyleUtil.createBrandingCell();
        closing.setColspan(columns);
        closing.setPadding(3);
        closing.addElement(PdfStyleUtil.centeredParagraph("THE BATTLE FOR GLORY",
                PdfStyleUtil.boldFont(9, PdfStyleUtil.LIGHT_GOLD)));
        showcase.addCell(closing);
        document.add(showcase);
    }

    // Qualification sources are taken from generated fixtures, never inferred
    // from fixed match numbers or a hardcoded playoff format.
    private void addPlayoffRoadmap(Document document,
            List<MatchScheduleFixtureResponse> fixtures) throws Exception {
        List<MatchScheduleFixtureResponse> playoffs = fixtures.stream()
                .filter(f -> isPlayoffStage(f.getMatchStage()))
                .sorted(Comparator.comparing(MatchScheduleFixtureResponse::getMatchNumber,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        if (playoffs.isEmpty()) {
            return;
        }
        PdfPTable roadmap = new PdfPTable(2);
        roadmap.setWidthPercentage(100);
        roadmap.setSpacingAfter(PdfStyleUtil.MEDIUM_SPACING);
        roadmap.setKeepTogether(playoffs.size() <= 4);
        roadmap.setSplitLate(true);
        roadmap.addCell(PdfStyleUtil.createPremiumSectionHeader(
                "ROAD TO THE GRAND FINAL", 2));
        roadmap.setHeaderRows(1);
        int pending = 0;
        for (int i = 0; i < playoffs.size(); i++) {
            MatchScheduleFixtureResponse fixture = playoffs.get(i);
            boolean finalMatch = "FINAL".equalsIgnoreCase(safeText(fixture.getMatchStage()));
            // Pair consecutive non-final cards; expand an unpaired card
            // to the full row instead of inserting a decorative empty cell.
            boolean nextIsNonFinal = i + 1 < playoffs.size()
                    && !"FINAL".equalsIgnoreCase(
                            safeText(playoffs.get(i + 1).getMatchStage()));
            boolean fullWidth = finalMatch || (pending == 0 && !nextIsNonFinal);
            PdfPCell cell = PdfStyleUtil.createBrandingCell();
            cell.setColspan(fullWidth ? 2 : 1);
            cell.setBackgroundColor(finalMatch ? PdfStyleUtil.LIGHT_GOLD
                    : PdfStyleUtil.SECONDARY_NAVY);
            cell.setPadding(7);
            Color titleColor = finalMatch ? PdfStyleUtil.PRIMARY_NAVY : PdfStyleUtil.LIGHT_GOLD;
            Color bodyColor = finalMatch ? PdfStyleUtil.PRIMARY_NAVY : PdfStyleUtil.WHITE;
            cell.addElement(PdfStyleUtil.centeredParagraph(
                    "MATCH " + safeText(fixture.getMatchNumber()) + " - "
                            + formatLabel(fixture.getMatchStage()),
                    PdfStyleUtil.boldFont(11, titleColor)));
            addRoadmapParticipant(cell, fixture.getHomeTeamName(),
                    fixture.getHomeTeamShortName(), fixture.getHomeTeamSource(), bodyColor);
            cell.addElement(PdfStyleUtil.centeredParagraph("VS",
                    PdfStyleUtil.boldFont(9, titleColor)));
            addRoadmapParticipant(cell, fixture.getAwayTeamName(),
                    fixture.getAwayTeamShortName(), fixture.getAwayTeamSource(), bodyColor);
            cell.addElement(PdfStyleUtil.centeredParagraph(
                    formatDate(fixture.getMatchDate()) + " | "
                            + formatTime(fixture.getStartTime()) + " - "
                            + formatTime(fixture.getEndTime()),
                    PdfStyleUtil.regularFont(8, bodyColor)));
            roadmap.addCell(cell);
            pending = fullWidth ? 0 : (pending + 1) % 2;
        }
        document.add(roadmap);
    }

    private void addRoadmapParticipant(PdfPCell cell, String name,
            String shortName, String source, Color color) {
        String participant = resolveTeamDisplayName(name, shortName, source);
        cell.addElement(PdfStyleUtil.centeredParagraph(participant,
                PdfStyleUtil.boldFont(10, color)));
        if ((hasText(name) || hasText(shortName)) && hasText(source)) {
            String qualification = formatParticipantSource(source);
            if (!qualification.equalsIgnoreCase(participant)
                    && !"To Be Decided".equals(qualification)) {
                cell.addElement(PdfStyleUtil.centeredParagraph(qualification,
                        PdfStyleUtil.regularFont(8, color)));
            }
        }
    }

    private void collectShowcaseTeam(Map<UUID, ShowcaseTeam> teams, UUID id,
            String name, String shortName, String logoUrl) {
        if (id == null) {
            return; // Unresolved playoff participants are not teams.
        }
        String displayName = hasText(name) ? name.trim()
                : hasText(shortName) ? shortName.trim() : "Team";
        String abbreviation = hasText(shortName) ? shortName.trim()
                : displayName.substring(0, Math.min(3, displayName.length()))
                        .toUpperCase(Locale.ENGLISH);
        teams.putIfAbsent(id, new ShowcaseTeam(displayName, abbreviation, logoUrl));
    }

    private void addInformationRow(PdfPTable table, String leftLabel,
            String leftValue, String rightLabel, String rightValue) {
        table.addCell(createInformationCell(leftLabel, leftValue));
        table.addCell(createInformationCell(rightLabel, rightValue));
    }

    private PdfPCell createInformationCell(String label, String value) {
        return PdfStyleUtil.createPremiumInformationCell(label, value);
    }

    // ==========================================
    // 12. ADD FILTER INFORMATION
    // ==========================================
    private void addFilterInformation(
            Document document,
            List<MatchScheduleFixtureResponse> filteredFixtures,
            TeamResponse selectedTeam,
            LocalDate matchDate)
            throws Exception {
        if (selectedTeam == null
                && matchDate == null) {
            return;
        }
        // Six columns allow two filter fields and three statistic cards
        // in a single compact, keep-together table.
        PdfPTable summary = new PdfPTable(6);
        summary.setWidthPercentage(100);
        summary.setKeepTogether(true);
        summary.setSpacingBefore(PdfStyleUtil.SMALL_SPACING);
        summary.setSpacingAfter(PdfStyleUtil.SMALL_SPACING);
        summary.addCell(PdfStyleUtil.createPremiumSectionHeader(
                "FILTERED MATCH SUMMARY", 6));
        String selectedTeamName =
                selectedTeam == null
                        ? "All Teams"
                        : resolveSelectedTeamName(
                                selectedTeam
                        );
        String selectedDate =
                matchDate == null
                        ? "All Dates"
                        : formatDate(matchDate);
        PdfPCell teamCell = createInformationCell("Selected Team", selectedTeamName);
        teamCell.setColspan(3);
        summary.addCell(teamCell);
        PdfPCell dateCell = createInformationCell("Selected Date", selectedDate);
        dateCell.setColspan(3);
        summary.addCell(dateCell);
        // Filtered league match count
        long filteredLeagueMatches =
                filteredFixtures.stream()
                        .filter(fixture ->
                                "LEAGUE".equalsIgnoreCase(
                                        fixture.getMatchStage()
                                )
                        )
                        .count();
        // Filtered playoff match count
        long filteredPlayoffMatches =
                filteredFixtures.stream()
                        .filter(fixture ->
                                isPlayoffStage(
                                        fixture.getMatchStage()
                                )
                        )
                        .count();
        long otherMatches =
                filteredFixtures.size()
                        - filteredLeagueMatches
                        - filteredPlayoffMatches;
        addFilteredSummaryCard(summary, "League Matches", filteredLeagueMatches, false);
        addFilteredSummaryCard(summary, "Playoff Matches", filteredPlayoffMatches, false);
        addFilteredSummaryCard(summary, "Total Matches", filteredFixtures.size(), true);
        if (otherMatches > 0) {
            addSummaryNote(summary, "Other / Unclassified Matches: " + otherMatches);
        }
        if (selectedTeam != null) {
            addSummaryNote(summary, "Playoff fixtures are included only when "
                    + "the selected team is confirmed as a participant.");
        }
        document.add(summary);
    }

    private void addFilteredSummaryCard(PdfPTable table, String label,
            long value, boolean highlighted) {
        PdfPCell cell = createSummaryCell(label, value, highlighted);
        cell.setColspan(2);
        table.addCell(cell);
    }

    private void addSummaryNote(PdfPTable table, String text) {
        PdfPCell cell = PdfStyleUtil.createBaseCell(PdfStyleUtil.PREMIUM_IVORY);
        cell.setColspan(6);
        cell.setPadding(4f);
        cell.setBorderColor(PdfStyleUtil.CHAMPAGNE_BORDER);
        Paragraph note = new Paragraph(text, PdfStyleUtil.noteFont());
        note.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(note);
        table.addCell(cell);
    }

    // ==========================================
    // 13. CREATE SUMMARY CELL
    // ==========================================
    private PdfPCell createSummaryCell(String label, Object value, boolean highlighted) {
        return PdfStyleUtil.createPremiumSummaryCell(label, safeText(value), highlighted);
    }

    // ==========================================
    // 14. ADD MATCH FIXTURES
    // ==========================================
    private void addMatchFixtures(
            Document document,
            List<MatchScheduleFixtureResponse> allFixtures,
            List<MatchScheduleFixtureResponse> fixtures,
            Map<String, Image> imageCache, UUID tournamentTeamId) throws Exception {

        // Day and section headers identify non-empty fixture tables.
        if (fixtures.isEmpty()) {
            document.add(PdfStyleUtil.sectionTitle("Match Fixtures"));
            document.add(new Paragraph(
                    "No match fixtures available for the selected schedule filters.",
                    PdfStyleUtil.bodyFont()));
            return;
        }

        // Day numbers are based on the complete schedule, not the filtered list.
        List<LocalDate> scheduleDates = allFixtures.stream()
                .map(MatchScheduleFixtureResponse::getMatchDate)
                .filter(Objects::nonNull).distinct().sorted().toList();

        Map<LocalDate, List<MatchScheduleFixtureResponse>> byDate = new TreeMap<>();
        List<MatchScheduleFixtureResponse> undated = new ArrayList<>();
        for (MatchScheduleFixtureResponse fixture : fixtures) {
            if (fixture.getMatchDate() == null) {
                undated.add(fixture);
            } else {
                byDate.computeIfAbsent(fixture.getMatchDate(),
                        date -> new ArrayList<>()).add(fixture);
            }
        }

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern(
                "EEEE, dd MMMM yyyy", Locale.ENGLISH);
        for (var entry : byDate.entrySet()) {
            int dayNumber = scheduleDates.indexOf(entry.getKey()) + 1;
            addDailyFixtureTable(document,
                    "DAY " + dayNumber + " - " + entry.getKey().format(dayFormatter),
                    entry.getValue(), imageCache, tournamentTeamId);
        }
        if (!undated.isEmpty()) {
            addDailyFixtureTable(document, "DATE TO BE CONFIRMED", undated, imageCache, tournamentTeamId);
        }
    }

    private void addDailyFixtureTable(
            Document document, String dayTitle,
            List<MatchScheduleFixtureResponse> fixtures,
            Map<String, Image> imageCache, UUID tournamentTeamId) throws Exception {

        long league = fixtures.stream().filter(f ->
                "LEAGUE".equalsIgnoreCase(f.getMatchStage())).count();
        long playoff = fixtures.stream().filter(f ->
                isPlayoffStage(f.getMatchStage())).count();
        long other = fixtures.size() - league - playoff;
        String summary = "League: " + league + " | Playoffs: " + playoff
                + " | Total: " + fixtures.size()
                + (other > 0 ? " | Other: " + other : "");

        List<MatchScheduleFixtureResponse> leagueFixtures = new ArrayList<>();
        List<MatchScheduleFixtureResponse> playoffFixtures = new ArrayList<>();
        List<MatchScheduleFixtureResponse> otherFixtures = new ArrayList<>();
        List<MatchScheduleFixtureResponse> finalFixtures = new ArrayList<>();

        for (MatchScheduleFixtureResponse fixture : fixtures) {
            if ("LEAGUE".equalsIgnoreCase(fixture.getMatchStage())) {
                leagueFixtures.add(fixture);
            } else if ("FINAL".equalsIgnoreCase(safeText(fixture.getMatchStage()))) {
                finalFixtures.add(fixture);
            } else if (isPlayoffStage(fixture.getMatchStage())) {
                playoffFixtures.add(fixture);
            } else {
                otherFixtures.add(fixture);
            }
        }

        boolean firstSection = true;
        if (!leagueFixtures.isEmpty()) {
            addFixtureSection(document, dayTitle, summary, "LEAGUE FIXTURES",
                    leagueFixtures, imageCache, firstSection, tournamentTeamId);
            firstSection = false;
        }
        if (!playoffFixtures.isEmpty()) {
            addFixtureSection(document, dayTitle, summary, "PLAYOFF FIXTURES",
                    playoffFixtures, imageCache, firstSection, tournamentTeamId);
            firstSection = false;
        }
        if (!otherFixtures.isEmpty()) {
            addFixtureSection(document, dayTitle, summary, "OTHER FIXTURES",
                    otherFixtures, imageCache, firstSection, tournamentTeamId);
            firstSection = false;
        }
        for (MatchScheduleFixtureResponse fixture : finalFixtures.stream()
                .sorted(Comparator.comparing(MatchScheduleFixtureResponse::getStartTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(MatchScheduleFixtureResponse::getMatchNumber,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .toList()) {
            if (tournamentTeamId == null) {
                addGrandFinalCard(document, dayTitle, summary, fixture, imageCache, firstSection);
            } else {
                addFixtureSection(document, dayTitle, summary, "GRAND FINAL",
                        List.of(fixture), imageCache, firstSection, tournamentTeamId);
            }
            firstSection = false;
        }

    }

    private void addGrandFinalCard(
            Document document, String dayTitle, String summary,
            MatchScheduleFixtureResponse fixture,
            Map<String, Image> imageCache, boolean firstSection) throws Exception {
        PdfPTable card = new PdfPTable(3);
        card.setWidthPercentage(100);
        card.setWidths(new float[]{ 4f, 1f, 4f });
        card.setKeepTogether(true);
        card.setSpacingBefore(5);
        card.setSpacingAfter(15);

        PdfPCell heading = PdfStyleUtil.createBrandingCell();
        heading.setColspan(3);
        if (firstSection) {
            heading.addElement(PdfStyleUtil.centeredParagraph(dayTitle,
                    PdfStyleUtil.boldFont(12, PdfStyleUtil.GOLD_HIGHLIGHT)));
            heading.addElement(PdfStyleUtil.centeredParagraph(summary,
                    PdfStyleUtil.regularFont(9, PdfStyleUtil.WHITE)));
        } else {
            heading.addElement(PdfStyleUtil.centeredParagraph(dayTitle,
                    PdfStyleUtil.regularFont(9, PdfStyleUtil.GOLD_HIGHLIGHT)));
        }
        heading.addElement(PdfStyleUtil.centeredParagraph(
                "MATCH " + safeText(fixture.getMatchNumber()),
                PdfStyleUtil.regularFont(11, PdfStyleUtil.WHITE)));
        heading.addElement(PdfStyleUtil.centeredParagraph("GRAND FINAL",
                PdfStyleUtil.boldFont(24, PdfStyleUtil.GOLD_HIGHLIGHT)));
        heading.setPadding(10);
        card.addCell(heading);
        card.addCell(PdfStyleUtil.createGoldAccentCell(3));

        card.addCell(createGrandFinalTeamCell(fixture.getHomeTeamName(),
                fixture.getHomeTeamShortName(), fixture.getHomeTeamLogoUrl(),
                fixture.getHomeTeamSource(), imageCache));
        PdfPCell versus = PdfStyleUtil.createBaseCell(PdfStyleUtil.LIGHT_GOLD);
        versus.setBorderColor(PdfStyleUtil.PREMIUM_GOLD);
        versus.setPadding(4);
        versus.addElement(PdfStyleUtil.centeredParagraph("VS",
                PdfStyleUtil.boldFont(18, PdfStyleUtil.DARK_GOLD_TEXT)));
        card.addCell(versus);
        card.addCell(createGrandFinalTeamCell(fixture.getAwayTeamName(),
                fixture.getAwayTeamShortName(), fixture.getAwayTeamLogoUrl(),
                fixture.getAwayTeamSource(), imageCache));

        PdfPCell details = PdfStyleUtil.createBrandingCell();
        details.setColspan(3);
        details.setPadding(8);
        details.addElement(PdfStyleUtil.centeredParagraph(formatDate(fixture.getMatchDate()),
                PdfStyleUtil.boldFont(12, PdfStyleUtil.WHITE)));
        details.addElement(PdfStyleUtil.centeredParagraph(
                formatTime(fixture.getStartTime()) + " - " + formatTime(fixture.getEndTime()),
                PdfStyleUtil.regularFont(11, PdfStyleUtil.WHITE)));
        details.addElement(PdfStyleUtil.centeredParagraph(
                "GROUND " + safeText(fixture.getGroundNumber()),
                PdfStyleUtil.boldFont(11, PdfStyleUtil.GOLD_HIGHLIGHT)));
        card.addCell(details);
        document.add(card);
    }

    private PdfPCell createGrandFinalTeamCell(
            String teamName, String teamShortName, String teamLogoUrl,
            String teamSource, Map<String, Image> imageCache) {
        PdfPCell cell = PdfStyleUtil.createBrandingCell();
        cell.setBackgroundColor(PdfStyleUtil.SECONDARY_NAVY);
        cell.setPadding(10);
        Image logo = pdfImageUtil.loadImage(teamLogoUrl, 75f, 75f, imageCache);
        if (logo != null) {
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.setSpacingAfter(8);
            cell.addElement(logo);
        } else {
            cell.addElement(PdfStyleUtil.centeredParagraph(
                    hasText(teamShortName) ? teamShortName.trim()
                            : hasText(teamName) ? "TEAM" : "Awaiting qualification",
                    PdfStyleUtil.regularFont(10, PdfStyleUtil.BORDER_COLOR)));
        }
        cell.addElement(PdfStyleUtil.centeredParagraph(
                resolveTeamDisplayName(teamName, teamShortName, teamSource),
                PdfStyleUtil.boldFont(13, PdfStyleUtil.WHITE)));
        return cell;
    }

    private void addFixtureSection(
            Document document, String dayTitle, String summary,
            String sectionTitle, List<MatchScheduleFixtureResponse> fixtures,
            Map<String, Image> imageCache, boolean firstSection, UUID tournamentTeamId) throws Exception {

        if (fixtures.isEmpty()) {
            return;
        }

        List<MatchScheduleFixtureResponse> sortedFixtures = fixtures.stream()
                .sorted(Comparator.comparing(MatchScheduleFixtureResponse::getStartTime,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(MatchScheduleFixtureResponse::getMatchNumber,
                                Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
        // League tables already identify the stage in their section heading.
        boolean showStage = !"LEAGUE FIXTURES".equals(sectionTitle);
        boolean teamView = tournamentTeamId != null;
        int columnCount = (showStage ? 7 : 6) - (teamView ? 2 : 0);
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidthPercentage(100);
        table.setWidths(teamView
                ? (showStage ? new float[]{1f, 1.6f, 4f, 2.2f, 1f}
                             : new float[]{1f, 4.8f, 2.2f, 1f})
                : showStage
                ? new float[] {0.85f, 1.45f, 2.1f, 0.65f, 2.1f, 1.55f, 1.05f}
                : new float[] {0.85f, 2.65f, 0.65f, 2.65f, 1.65f, 1.05f});
        table.setSpacingBefore(3);
        table.setSpacingAfter(8);
        // Full daily summary appears only in the first non-empty section.
        // Later sections retain compact date context even across page breaks.
        if (firstSection) {
            PdfPCell dayCell = PdfStyleUtil.createBrandingCell();
            dayCell.setColspan(columnCount);
            dayCell.setPadding(5);
            dayCell.addElement(PdfStyleUtil.centeredParagraph(dayTitle,
                    PdfStyleUtil.boldFont(12, PdfStyleUtil.GOLD_HIGHLIGHT)));
            dayCell.addElement(PdfStyleUtil.centeredParagraph(summary,
                    PdfStyleUtil.regularFont(9, PdfStyleUtil.WHITE)));
            table.addCell(dayCell);
        }
        PdfPCell sectionCell = PdfStyleUtil.createBaseCell(PdfStyleUtil.LIGHT_GOLD);
        sectionCell.setColspan(columnCount);
        sectionCell.setPadding(4);
        sectionCell.setBorderColor(PdfStyleUtil.PREMIUM_GOLD);
        sectionCell.setBorderWidth(0.75f);
        sectionCell.addElement(PdfStyleUtil.centeredParagraph(
                sectionTitle + " - " + fixtures.size()
                        + (fixtures.size() == 1 ? " MATCH" : " MATCHES"),
                PdfStyleUtil.boldFont(11, PdfStyleUtil.PRIMARY_NAVY)));
        if (!firstSection) {
            sectionCell.addElement(PdfStyleUtil.centeredParagraph(dayTitle,
                    PdfStyleUtil.regularFont(9, PdfStyleUtil.DARK_GOLD_TEXT)));
        }
        table.addCell(sectionCell);
        // Repeat the appropriate section context and column headings.
        table.setHeaderRows(firstSection ? 3 : 2);
        table.setSplitRows(true);
        table.setSplitLate(true);
        // Table headers
        addTableHeader(table, "Match");
        if (showStage) {
            addTableHeader(table, "Stage");
        }
        if (teamView) {
            addTableHeader(table, "Opponent");
        } else {
            addTableHeader(table, "Home Team");
            addTableHeader(table, "VS");
            addTableHeader(table, "Away Team");
        }
        addTableHeader(table, "Time");
        addTableHeader(table, "Ground");
        // Add fixture rows
        for (int rowIndex = 0; rowIndex < sortedFixtures.size(); rowIndex++) {
            MatchScheduleFixtureResponse fixture = sortedFixtures.get(rowIndex);
            if (teamView) {
                addOpponentFixtureRow(table, fixture, imageCache, rowIndex, showStage, tournamentTeamId);
            } else {
            addFixtureRow(
                    table,
                    fixture,
                    imageCache,
                    rowIndex,
                    showStage
            );
            }
        }
        document.add(table);
    }
    // ==========================================
    // 15. ADD TABLE HEADER
    // ==========================================
    private void addTableHeader(
            PdfPTable table,
            String headerText) {
        table.addCell(PdfStyleUtil.createPremiumTableHeader(headerText));
    }

    // ==========================================
    // 16. ADD FIXTURE ROW
    // ==========================================
    private void addFixtureRow(
            PdfPTable table,
            MatchScheduleFixtureResponse fixture,
            Map<String, Image> imageCache,
            int rowIndex, boolean showStage) {

        Color background = PdfStyleUtil.premiumAlternateRowColor(rowIndex);
        Color border = PdfStyleUtil.CHAMPAGNE_BORDER;
        Color stageColor = isPlayoffStage(fixture.getMatchStage())
                ? PdfStyleUtil.DARK_GOLD_TEXT : PdfStyleUtil.PRIMARY_NAVY;

        table.addCell(PdfStyleUtil.createPremiumMatchNumberCell(
                safeText(fixture.getMatchNumber())));
        if (showStage) {
            addStyledFixtureCell(table, createTextCell(formatLabel(fixture.getMatchStage()),
                    true, stageColor), background, border);
        }
        addStyledFixtureCell(table, createTeamCell(fixture.getHomeTeamName(),
                fixture.getHomeTeamShortName(), fixture.getHomeTeamLogoUrl(),
                fixture.getHomeTeamSource(), imageCache), background, border);
        table.addCell(PdfStyleUtil.createPremiumVersusCell());
        addStyledFixtureCell(table, createTeamCell(fixture.getAwayTeamName(),
                fixture.getAwayTeamShortName(), fixture.getAwayTeamLogoUrl(),
                fixture.getAwayTeamSource(), imageCache), background, border);
        String dateTime = formatTime(fixture.getStartTime()) + "\nto\n"
                + formatTime(fixture.getEndTime());
        table.addCell(PdfStyleUtil.createPremiumFixtureCell(dateTime, true, rowIndex));
        table.addCell(PdfStyleUtil.createPremiumFixtureCell(
                safeText(fixture.getGroundNumber()), true, rowIndex));
    }

    private void addOpponentFixtureRow(PdfPTable table,
            MatchScheduleFixtureResponse fixture, Map<String, Image> imageCache,
            int rowIndex, boolean showStage, UUID tournamentTeamId) {
        boolean home = tournamentTeamId.equals(fixture.getHomeTournamentTeamId());
        String name = home ? fixture.getAwayTeamName() : fixture.getHomeTeamName();
        String shortName = home ? fixture.getAwayTeamShortName() : fixture.getHomeTeamShortName();
        String logoUrl = home ? fixture.getAwayTeamLogoUrl() : fixture.getHomeTeamLogoUrl();
        String source = home ? fixture.getAwayTeamSource() : fixture.getHomeTeamSource();
        Color background = PdfStyleUtil.premiumAlternateRowColor(rowIndex);
        Color border = PdfStyleUtil.CHAMPAGNE_BORDER;
        table.addCell(PdfStyleUtil.createPremiumMatchNumberCell(safeText(fixture.getMatchNumber())));
        if (showStage) {
            addStyledFixtureCell(table, createTextCell(formatLabel(fixture.getMatchStage()),
                    true, PdfStyleUtil.DARK_GOLD_TEXT), background, border);
        }
        PdfPTable opponent = new PdfPTable(new float[]{1f, 4f});
        opponent.setWidthPercentage(100);
        PdfPCell logoCell = PdfStyleUtil.createBorderlessCell(background);
        logoCell.setPadding(1f);
        Image logo = pdfImageUtil.loadImage(logoUrl, 26f, 26f, imageCache);
        if (logo != null) {
            logo.setAlignment(Element.ALIGN_CENTER);
            logoCell.addElement(logo);
        } else {
            logoCell.addElement(PdfStyleUtil.centeredParagraph(
                    hasText(shortName) ? shortName : "-", PdfStyleUtil.regularFont(8, SUMMARY_MUTED)));
        }
        opponent.addCell(logoCell);
        PdfPCell nameCell = PdfStyleUtil.createBorderlessCell(background);
        nameCell.setPadding(2f);
        nameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        nameCell.addElement(new Paragraph(resolveTeamDisplayName(name, shortName, source),
                PdfStyleUtil.boldFont(9, SUMMARY_NAVY)));
        opponent.addCell(nameCell);
        PdfPCell cell = PdfStyleUtil.createBaseCell(background);
        cell.setPadding(2f);
        cell.addElement(opponent);
        addStyledFixtureCell(table, cell, background, border);
        table.addCell(PdfStyleUtil.createPremiumFixtureCell(
                formatTime(fixture.getStartTime()) + " - " + formatTime(fixture.getEndTime()), true, rowIndex));
        table.addCell(PdfStyleUtil.createPremiumFixtureCell(safeText(fixture.getGroundNumber()), true, rowIndex));
    }

    private void addStyledFixtureCell(PdfPTable table, PdfPCell cell,
            Color background, Color border) {
        cell.setBackgroundColor(background);
        cell.setBorderColor(border);
        cell.setBorderWidth(0.5f);
        table.addCell(cell);
    }

    // ==========================================
    // 17. CREATE STANDARD TEXT CELL
    // ==========================================
    private PdfPCell createTextCell(String text, boolean bold, Color color) {
        Font font = bold ? PdfStyleUtil.boldFont(9, color)
                : PdfStyleUtil.regularFont(9, color);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4);
        return cell;
    }
    // ==========================================
    // 18. CREATE TEAM CELL WITH LOGO
    // ==========================================
    private PdfPCell createTeamCell(
            String teamName,
            String teamShortName,
            String teamLogoUrl,
            String teamSource,
            Map<String, Image> imageCache) {
        PdfPCell cell = new PdfPCell();
        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );
        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );
        cell.setPadding(4);
        Font teamNameFont = PdfStyleUtil.boldFont(9, SUMMARY_NAVY);
        Font placeholderFont = PdfStyleUtil.regularFont(8, SUMMARY_MUTED);
        String displayName = resolveTeamDisplayName(
                teamName,
                teamShortName,
                teamSource
        );
        // Load actual team logo
        Image teamLogo = pdfImageUtil.loadImage(
                teamLogoUrl,
                PdfStyleUtil.TEAM_LOGO_SIZE,
                PdfStyleUtil.TEAM_LOGO_SIZE,
                imageCache
        );
        if (teamLogo != null) {
            teamLogo.setAlignment(
                    Element.ALIGN_CENTER
            );
            teamLogo.setSpacingAfter(2);
            cell.addElement(teamLogo);
        } else {
            String placeholderText = hasText(teamShortName)
                    ? teamShortName.trim()
                    : hasText(teamName) ? "TEAM" : hasText(teamSource) ? "Awaiting qualification" : "Pending team";
            Paragraph placeholder =
                    new Paragraph(
                            placeholderText,
                            placeholderFont
                    );
            placeholder.setAlignment(
                    Element.ALIGN_CENTER
            );
            placeholder.setSpacingAfter(2);
            cell.addElement(placeholder);
        }
        // Team display name
        Paragraph nameParagraph =
                new Paragraph(
                        displayName,
                        teamNameFont
                );
        nameParagraph.setAlignment(
                Element.ALIGN_CENTER
        );
        cell.addElement(nameParagraph);
        return cell;
    }
    // ==========================================
    // 19. RESOLVE TOURNAMENT NAME
    // ==========================================
    private String resolveTournamentName(
            MatchScheduleResponse schedule) {
        return hasText(schedule.getTournamentName())
                ? schedule.getTournamentName().trim()
                : "Cricket Tournament";
    }
    // ==========================================
    // 20. RESOLVE SELECTED TEAM NAME
    // ==========================================
    private String resolveSelectedTeamName(
            TeamResponse selectedTeam) {
        if (selectedTeam == null) {
            return "All Teams";
        }
        return resolveTeamDisplayName(
                selectedTeam.getName(),
                selectedTeam.getShortName(),
                null
        );
    }
    // ==========================================
    // 21. RESOLVE TEAM DISPLAY NAME
    // ==========================================
    private String resolveTeamDisplayName(
            String teamName,
            String teamShortName,
            String teamSource) {
        if (hasText(teamName)) {
            return teamName.trim();
        }
        if (hasText(teamShortName)) {
            return teamShortName.trim();
        }
        return formatParticipantSource(teamSource);
    }

    private String formatParticipantSource(String teamSource) {
        if (!hasText(teamSource)) {
            return "To Be Decided";
        }
        String source = teamSource.trim();
        String normalized = source.toUpperCase(Locale.ROOT)
                .replaceAll("[\\s_-]+", "_");
        if (normalized.matches("(?:LEAGUE_)?RANK_[0-9]+")) {
            return "League Rank " + normalized.substring(normalized.lastIndexOf('_') + 1);
        }
        if (normalized.matches("(?:WINNER|LOSER)_(?:OF_)?MATCH_[0-9]+")) {
            String result = normalized.startsWith("WINNER") ? "Winner" : "Loser";
            return result + " of Match " + normalized.substring(normalized.lastIndexOf('_') + 1);
        }
        if (normalized.equals("TBD") || normalized.equals("TO_BE_DECIDED")) {
            return "To Be Decided";
        }
        return source.replace('_', ' ');
    }

    // ==========================================
    // 22. CHECK PLAYOFF STAGE
    // ==========================================
    private boolean isPlayoffStage(String matchStage) {
        if (!hasText(matchStage)) {
            return false;
        }
        return switch (
                matchStage.trim()
                        .toUpperCase(Locale.ROOT)
        ) {
            case "SEMIFINAL",
                 "SEMIFINAL_1",
                 "SEMIFINAL_2",
                 "QUALIFIER_1",
                 "ELIMINATOR",
                 "QUALIFIER_2",
                 "FINAL" -> true;
            default -> false;
        };
    }
    // ==========================================
    // 23. FORMAT MATCH DATE
    // ==========================================
    private String formatDate(LocalDate date) {
        if (date == null) {
            return "TBD";
        }
        return date.format(DATE_FORMATTER);
    }
    // ==========================================
    // 24. FORMAT MATCH TIME
    // ==========================================
    private String formatTime(LocalTime time) {
        if (time == null) {
            return "TBD";
        }
        return time.format(TIME_FORMATTER);
    }
    // ==========================================
    // 25. FORMAT ENUM / SOURCE LABEL
    // ==========================================
    private String formatLabel(Object value) {
        if (value == null) {
            return "N/A";
        }
        String text = value.toString().trim();
        if (text.isBlank()) {
            return "N/A";
        }
        if ("FINAL".equalsIgnoreCase(text)) {
            return "GRAND FINAL";
        }
        return text.replace('_', ' ');
    }
    // ==========================================
    // 26. NULL-SAFE TEXT
    // ==========================================
    private String safeText(Object value) {
        if (value == null) {
            return "N/A";
        }
        String text = value.toString().trim();
        return text.isBlank()
                ? "N/A"
                : text;
    }
    // ==========================================
    // 27. CHECK TEXT AVAILABLE
    // ==========================================
    private boolean hasText(String value) {
        return value != null
                && !value.isBlank();
    }

}
