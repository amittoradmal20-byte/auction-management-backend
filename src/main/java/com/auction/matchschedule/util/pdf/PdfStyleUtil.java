package com.auction.matchschedule.util.pdf;
import java.awt.Color;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
public final class PdfStyleUtil {
    // ==========================================
    // 1. PRIVATE CONSTRUCTOR
    // ==========================================
    private PdfStyleUtil() {
        // Utility class
    }
    // ==========================================
    // 2. PRIMARY BRAND COLOURS
    // ==========================================
    public static final Color PRIMARY_NAVY =
            new Color(11, 20, 40);
    public static final Color SECONDARY_NAVY =
            new Color(23, 37, 84);
    public static final Color PREMIUM_GOLD =
            new Color(212, 175, 55);
    public static final Color LIGHT_GOLD =
            new Color(245, 231, 178);
    // ==========================================
    // 3. BACKGROUND COLOURS
    // ==========================================
    public static final Color WHITE =
            Color.WHITE;
    public static final Color LIGHT_SLATE =
            new Color(241, 245, 249);
    public static final Color LIGHT_BLUE =
            new Color(239, 246, 255);
    public static final Color BORDER_COLOR =
            new Color(203, 213, 225);
    // ==========================================
    // 4. TEXT COLOURS
    // ==========================================
    public static final Color PRIMARY_TEXT =
            new Color(30, 41, 59);
    public static final Color SECONDARY_TEXT =
            new Color(100, 116, 139);
    public static final Color SUCCESS_TEXT =
            new Color(22, 101, 52);
    public static final Color WARNING_TEXT =
            new Color(146, 64, 14);
    // ==========================================
    // 5. PAGE CONFIGURATION
    // ==========================================
    public static final float PAGE_MARGIN_LEFT = 36;
    public static final float PAGE_MARGIN_RIGHT = 36;
    public static final float PAGE_MARGIN_TOP = 36;
    public static final float PAGE_MARGIN_BOTTOM = 45;
    // ==========================================
    // 6. IMAGE CONFIGURATION
    // ==========================================
    public static final float TOURNAMENT_LOGO_SIZE = 110;
    public static final float SELECTED_TEAM_LOGO_SIZE = 85;
    public static final float TEAM_LOGO_SIZE = 40;
    // ==========================================
    // 7. FONT SIZE CONFIGURATION
    // ==========================================
    public static final float TITLE_FONT_SIZE = 22;
    public static final float SUBTITLE_FONT_SIZE = 14;
    public static final float TEAM_TITLE_FONT_SIZE = 18;
    public static final float SECTION_FONT_SIZE = 15;
    public static final float BODY_FONT_SIZE = 10;
    public static final float TABLE_HEADER_FONT_SIZE = 9;
    public static final float TABLE_BODY_FONT_SIZE = 9;
    public static final float SUMMARY_VALUE_FONT_SIZE = 22;
    public static final float SUMMARY_LABEL_FONT_SIZE = 10;
    public static final float NOTE_FONT_SIZE = 8;
    public static final float FOOTER_FONT_SIZE = 8;
    // ==========================================
    // 8. SPACING CONFIGURATION
    // ==========================================
    public static final float SMALL_SPACING = 5;
    public static final float MEDIUM_SPACING = 10;
    public static final float LARGE_SPACING = 15;
    public static final float SECTION_SPACING = 20;
    public static final float TABLE_CELL_PADDING = 6;
    public static final float HEADER_CELL_PADDING = 8;
    public static final float SUMMARY_CELL_PADDING = 12;
    // ==========================================
    // 9. CREATE REGULAR FONT
    // ==========================================
    public static Font regularFont(
            float size,
            Color color) {
        return FontFactory.getFont(
                FontFactory.HELVETICA,
                size,
                Font.NORMAL,
                color
        );
    }
    // ==========================================
    // 10. CREATE BOLD FONT
    // ==========================================
    public static Font boldFont(
            float size,
            Color color) {
        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                size,
                Font.NORMAL,
                color
        );
    }
    // ==========================================
    // 11. TOURNAMENT TITLE FONT
    // ==========================================
    public static Font tournamentTitleFont() {
        return boldFont(
                TITLE_FONT_SIZE,
                PREMIUM_GOLD
        );
    }
    // ==========================================
    // 12. TOURNAMENT SUBTITLE FONT
    // ==========================================
    public static Font subtitleFont() {
        return boldFont(
                SUBTITLE_FONT_SIZE,
                WHITE
        );
    }
    // ==========================================
    // 13. TEAM TITLE FONT
    // ==========================================
    public static Font teamTitleFont() {
        return boldFont(
                TEAM_TITLE_FONT_SIZE,
                PRIMARY_NAVY
        );
    }
    // ==========================================
    // 14. SECTION TITLE FONT
    // ==========================================
    public static Font sectionTitleFont() {
        return boldFont(
                SECTION_FONT_SIZE,
                PRIMARY_NAVY
        );
    }
    // ==========================================
    // 15. BODY FONT
    // ==========================================
    public static Font bodyFont() {
        return regularFont(
                BODY_FONT_SIZE,
                PRIMARY_TEXT
        );
    }
    // ==========================================
    // 16. SECONDARY BODY FONT
    // ==========================================
    public static Font secondaryBodyFont() {
        return regularFont(
                BODY_FONT_SIZE,
                SECONDARY_TEXT
        );
    }
    // ==========================================
    // 17. TABLE HEADER FONT
    // ==========================================
    public static Font tableHeaderFont() {
        return boldFont(
                TABLE_HEADER_FONT_SIZE,
                WHITE
        );
    }
    // ==========================================
    // 18. TABLE BODY FONT
    // ==========================================
    public static Font tableBodyFont() {
        return regularFont(
                TABLE_BODY_FONT_SIZE,
                PRIMARY_TEXT
        );
    }
    // ==========================================
    // 19. TABLE BOLD FONT
    // ==========================================
    public static Font tableBoldFont() {
        return boldFont(
                TABLE_BODY_FONT_SIZE,
                PRIMARY_NAVY
        );
    }
    // ==========================================
    // 20. SUMMARY VALUE FONT
    // ==========================================
    public static Font summaryValueFont() {
        return boldFont(
                SUMMARY_VALUE_FONT_SIZE,
                PRIMARY_NAVY
        );
    }
    // ==========================================
    // 21. SUMMARY LABEL FONT
    // ==========================================
    public static Font summaryLabelFont() {
        return regularFont(
                SUMMARY_LABEL_FONT_SIZE,
                SECONDARY_TEXT
        );
    }
    // ==========================================
    // 22. NOTE FONT
    // ==========================================
    public static Font noteFont() {
        return regularFont(
                NOTE_FONT_SIZE,
                SECONDARY_TEXT
        );
    }
    // ==========================================
    // 23. FOOTER FONT
    // ==========================================
    public static Font footerFont() {
        return regularFont(
                FOOTER_FONT_SIZE,
                SECONDARY_TEXT
        );
    }
    // ==========================================
    // 24. CREATE CENTERED PARAGRAPH
    // ==========================================
    public static Paragraph centeredParagraph(
            String text,
            Font font) {
        Paragraph paragraph = new Paragraph(
                text == null ? "" : text,
                font
        );
        paragraph.setAlignment(
                Element.ALIGN_CENTER
        );
        return paragraph;
    }
    // ==========================================
    // 25. CREATE SECTION TITLE
    // ==========================================
    public static Paragraph sectionTitle(
            String title) {
        Paragraph paragraph = new Paragraph(
                title == null ? "" : title,
                sectionTitleFont()
        );
        paragraph.setSpacingBefore(
                LARGE_SPACING
        );
        paragraph.setSpacingAfter(
                MEDIUM_SPACING
        );
        paragraph.setKeepTogether(true);
        return paragraph;
    }
    // ==========================================
    // 26. CREATE BASE CELL
    // ==========================================
    public static PdfPCell createBaseCell(
            Color backgroundColor) {
        PdfPCell cell = new PdfPCell();
        applyBaseCellStyle(
                cell,
                backgroundColor
        );
        return cell;
    }
    // ==========================================
    // 27. APPLY BASE CELL STYLE
    // ==========================================
    private static void applyBaseCellStyle(
            PdfPCell cell,
            Color backgroundColor) {
        cell.setBackgroundColor(
                backgroundColor == null
                        ? WHITE
                        : backgroundColor
        );
        cell.setBorderColor(
                BORDER_COLOR
        );
        cell.setBorderWidth(0.5f);
        cell.setPadding(
                TABLE_CELL_PADDING
        );
        cell.setHorizontalAlignment(
                Element.ALIGN_CENTER
        );
        cell.setVerticalAlignment(
                Element.ALIGN_MIDDLE
        );
    }
    // ==========================================
    // 28. CREATE TABLE HEADER CELL
    // ==========================================
    public static PdfPCell createHeaderCell(
            String text) {
        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text == null ? "" : text,
                        tableHeaderFont()
                )
        );
        applyBaseCellStyle(
                cell,
                SECONDARY_NAVY
        );
        cell.setPadding(
                HEADER_CELL_PADDING
        );
        cell.setBorderColor(
                PRIMARY_NAVY
        );
        return cell;
    }
    // ==========================================
    // 29. CREATE STANDARD TEXT CELL
    // ==========================================
    public static PdfPCell createTextCell(
            String text,
            boolean bold,
            Color backgroundColor) {
        Font font = bold
                ? tableBoldFont()
                : tableBodyFont();
        PdfPCell cell = new PdfPCell(
                new Phrase(
                        text == null ? "" : text,
                        font
                )
        );
        applyBaseCellStyle(
                cell,
                backgroundColor
        );
        return cell;
    }
    // ==========================================
    // 30. CREATE BORDERLESS CELL
    // ==========================================
    public static PdfPCell createBorderlessCell(
            Color backgroundColor) {
        PdfPCell cell = createBaseCell(
                backgroundColor
        );
        cell.setBorder(
                Rectangle.NO_BORDER
        );
        return cell;
    }
    // ==========================================
    // 31. CREATE BRANDING CELL
    // ==========================================
    public static PdfPCell createBrandingCell() {
        PdfPCell cell = createBaseCell(
                PRIMARY_NAVY
        );
        cell.setPadding(15);
        cell.setBorderColor(
                PREMIUM_GOLD
        );
        cell.setBorderWidth(1.5f);
        return cell;
    }
    // ==========================================
    // 32. CREATE SUMMARY CELL
    // ==========================================
    public static PdfPCell createSummaryCell(
            String label,
            long value) {
        PdfPCell cell = createBaseCell(
                LIGHT_SLATE
        );
        cell.setPadding(
                SUMMARY_CELL_PADDING
        );
        Paragraph valueParagraph =
                centeredParagraph(
                        String.valueOf(value),
                        summaryValueFont()
                );
        valueParagraph.setSpacingAfter(
                SMALL_SPACING
        );
        Paragraph labelParagraph =
                centeredParagraph(
                        label,
                        summaryLabelFont()
                );
        cell.addElement(valueParagraph);
        cell.addElement(labelParagraph);
        return cell;
    }
    // ==========================================
    // 33. RESOLVE ALTERNATE ROW COLOUR
    // ==========================================
    public static Color alternateRowColor(
            int rowIndex) {
        return rowIndex % 2 == 0
                ? WHITE
                : LIGHT_SLATE;
    }
    // ==========================================
    // 34. CREATE PLAYOFF CELL
    // ==========================================
    public static PdfPCell createPlayoffCell(
            String text,
            boolean bold) {
        return createTextCell(
                text,
                bold,
                LIGHT_BLUE
        );
    }
    // ==========================================
    // 35. CREATE GRAND FINAL CELL
    // ==========================================
    public static PdfPCell createGrandFinalCell(
            String text,
            boolean bold) {
        PdfPCell cell = createTextCell(
                text,
                bold,
                LIGHT_GOLD
        );
        cell.setBorderColor(
                PREMIUM_GOLD
        );
        cell.setBorderWidth(1);
        return cell;
    }

    // ==========================================
    // 36. PREMIUM DESIGN TOKENS
    // ==========================================

    public static final Color GOLD_HIGHLIGHT = new Color(255, 241, 179);
    public static final Color DEEP_GOLD = new Color(164, 123, 37);
    public static final Color PREMIUM_IVORY = new Color(255, 253, 245);
    public static final Color WARM_IVORY = new Color(243, 239, 229);
    public static final Color CHAMPAGNE_BORDER = new Color(224, 211, 173);
    public static final Color DARK_GOLD_TEXT = new Color(120, 85, 25);

    public static final float PREMIUM_CELL_PADDING = 4f;
    public static final float PREMIUM_SECTION_PADDING = 6f;
    public static final float PREMIUM_SUMMARY_PADDING = 6f;

    // 37. Premium section header (full-width table row)
    public static PdfPCell createPremiumSectionHeader(String title, int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException("Column count must be positive");
        }
        PdfPCell cell = createBaseCell(SECONDARY_NAVY);
        cell.setColspan(columns);
        cell.setPadding(PREMIUM_SECTION_PADDING);
        cell.setBorderColor(PREMIUM_GOLD);
        cell.setBorderWidth(0.75f);
        cell.setBorderWidthBottom(2f);
        Paragraph heading = new Paragraph(title == null ? "" : title,
                boldFont(12f, GOLD_HIGHLIGHT));
        heading.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(heading);
        return cell;
    }

    // 38. Premium information field (label above value)
    public static PdfPCell createPremiumInformationCell(String label, String value) {
        PdfPCell cell = createBaseCell(PREMIUM_IVORY);
        cell.setBorderColor(CHAMPAGNE_BORDER);
        cell.setPadding(PREMIUM_SECTION_PADDING);
        cell.setBorderWidthLeft(2f);
        cell.setBorderColorLeft(PREMIUM_GOLD);
        Paragraph labelParagraph = new Paragraph(label == null ? "" : label,
                regularFont(8f, DARK_GOLD_TEXT));
        labelParagraph.setAlignment(Element.ALIGN_LEFT);
        labelParagraph.setSpacingAfter(2f);
        Paragraph valueParagraph = new Paragraph(premiumDisplayValue(value),
                boldFont(10f, PRIMARY_NAVY));
        valueParagraph.setAlignment(Element.ALIGN_LEFT);
        cell.addElement(labelParagraph);
        cell.addElement(valueParagraph);
        return cell;
    }

    // 39. Premium summary card (supports unknown values as N/A)
    public static PdfPCell createPremiumSummaryCell(String label, String value,
            boolean highlighted) {
        PdfPCell cell = createBaseCell(highlighted ? LIGHT_GOLD : PRIMARY_NAVY);
        cell.setBorderColor(PREMIUM_GOLD);
        cell.setBorderWidth(0.75f);
        cell.setBorderWidthBottom(2f);
        cell.setPadding(PREMIUM_SUMMARY_PADDING);
        Paragraph number = centeredParagraph(premiumDisplayValue(value),
                boldFont(20f, highlighted ? PRIMARY_NAVY : GOLD_HIGHLIGHT));
        number.setSpacingAfter(2f);
        cell.addElement(number);
        cell.addElement(centeredParagraph(label,
                regularFont(9f, highlighted ? PRIMARY_NAVY : WHITE)));
        return cell;
    }

    public static PdfPCell createPremiumSummaryCell(String label, long value,
            boolean highlighted) {
        return createPremiumSummaryCell(label, String.valueOf(value), highlighted);
    }

    // 40. Premium fixture styles
    public static Color premiumAlternateRowColor(int rowIndex) {
        return rowIndex % 2 == 0 ? PREMIUM_IVORY : WARM_IVORY;
    }

    public static PdfPCell createPremiumTableHeader(String text) {
        PdfPCell cell = createHeaderCell(text);
        cell.setPadding(PREMIUM_CELL_PADDING);
        cell.setBorderColor(PREMIUM_GOLD);
        return cell;
    }

    public static PdfPCell createPremiumFixtureCell(String text, boolean bold,
            int rowIndex) {
        PdfPCell cell = createTextCell(text, bold, premiumAlternateRowColor(rowIndex));
        cell.setPadding(PREMIUM_CELL_PADDING);
        cell.setBorderColor(CHAMPAGNE_BORDER);
        return cell;
    }

    public static PdfPCell createPremiumMatchNumberCell(String number) {
        PdfPCell cell = new PdfPCell(new Phrase(premiumDisplayValue(number),
                boldFont(10f, GOLD_HIGHLIGHT)));
        applyBaseCellStyle(cell, PRIMARY_NAVY);
        cell.setPadding(PREMIUM_CELL_PADDING);
        cell.setBorderColor(PREMIUM_GOLD);
        return cell;
    }

    public static PdfPCell createPremiumVersusCell() {
        PdfPCell cell = new PdfPCell(new Phrase("VS", boldFont(10f, DARK_GOLD_TEXT)));
        applyBaseCellStyle(cell, LIGHT_GOLD);
        cell.setPadding(PREMIUM_CELL_PADDING);
        cell.setBorderColor(CHAMPAGNE_BORDER);
        return cell;
    }

    // 41. Gold accent row (no raster image required)
    public static PdfPCell createGoldAccentCell(int columns) {
        if (columns < 1) {
            throw new IllegalArgumentException("Column count must be positive");
        }
        PdfPCell cell = createBorderlessCell(PREMIUM_GOLD);
        cell.setColspan(columns);
        cell.setPadding(0f);
        cell.setFixedHeight(2f);
        return cell;
    }

    private static String premiumDisplayValue(String value) {
        return value == null || value.isBlank() ? "N/A" : value;
    }
}
