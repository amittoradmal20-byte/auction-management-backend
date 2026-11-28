package com.auction.matchschedule.util.pdf;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Image;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Phrase;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.ColumnText;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfGState;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfTemplate;
import org.openpdf.text.pdf.PdfWriter;

public final class MatchSchedulePdfFooter extends PdfPageEventHelper {

    private static final float FONT_SIZE = 8f;
    private static final float TOTAL_WIDTH = 30f;
    private static final float PAGE_BORDER_INSET = 18f;
    private static final float INNER_BORDER_GAP = 4f;
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    private final String tournamentName;
    private final String generatedAt;
    private final Image tournamentWatermark;
    private PdfTemplate totalPages;
    private BaseFont pageFont;
    private int lastPageNumber;

    public MatchSchedulePdfFooter(String tournamentName) {
        this(tournamentName, null);
    }

    public MatchSchedulePdfFooter(String tournamentName, Image tournamentWatermark) {
        this.tournamentWatermark = tournamentWatermark;
        this.tournamentName = tournamentName == null ? "Cricket Tournament" : tournamentName;
        this.generatedAt = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(TIMESTAMP_FORMAT) + " IST";
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        pageFont = PdfStyleUtil.tableBoldFont().getCalculatedBaseFont(false);
        totalPages = writer.getDirectContent().createTemplate(TOTAL_WIDTH, 12f);
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte background = writer.getDirectContentUnder();
        drawPageBorder(background, document.getPageSize());
        lastPageNumber = writer.getPageNumber();
        PdfContentByte canvas = writer.getDirectContent();
        drawTournamentWatermark(canvas, document);
        float left = document.left();
        float right = document.right();
        // Keep the footer above the inner border without changing body pagination.
        float bottom = document.bottom();
        float lineY = bottom - 8f;
        float textY = bottom - 21f;
        float totalX = right - TOTAL_WIDTH - 8f;
        String pagePrefix = "Page " + lastPageNumber + " of";
        // Reserve an explicit space: ColumnText may trim trailing whitespace.
        float pageNumberGap = pageFont.getWidthPoint(" ", FONT_SIZE);
        float prefixRight = totalX - pageNumberGap;
        float prefixWidth = pageFont.getWidthPoint(pagePrefix, FONT_SIZE);
        float titleWidth = prefixRight - prefixWidth - left - 20f;

        canvas.saveState();
        try {
            canvas.setColorFill(PdfStyleUtil.PRIMARY_NAVY);
            canvas.rectangle(left, bottom - 38f, right - left, 30f);
            canvas.fill();
            canvas.setColorStroke(PdfStyleUtil.PREMIUM_GOLD);
            canvas.setLineWidth(1.5f);
            canvas.moveTo(left, lineY);
            canvas.lineTo(right, lineY);
            canvas.stroke();

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase(fitTitle(titleWidth), PdfStyleUtil.boldFont(FONT_SIZE, PdfStyleUtil.GOLD_HIGHLIGHT)),
                    left + 8f, textY, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                    new Phrase("Rising Star Cricket Association Platform", PdfStyleUtil.regularFont(FONT_SIZE, PdfStyleUtil.WHITE)),
                    left + 8f, textY - 11f, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                    new Phrase("Generated: " + generatedAt, PdfStyleUtil.regularFont(FONT_SIZE, PdfStyleUtil.WHITE)),
                    right - 8f, textY - 11f, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                    new Phrase(pagePrefix, PdfStyleUtil.boldFont(FONT_SIZE, PdfStyleUtil.GOLD_HIGHLIGHT)),
                    prefixRight, textY, 0);
            canvas.addTemplate(totalPages, totalX, textY);
        } finally {
            canvas.restoreState();
        }
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        totalPages.beginText();
        totalPages.setFontAndSize(pageFont, FONT_SIZE);
        totalPages.setColorFill(PdfStyleUtil.GOLD_HIGHLIGHT);
        totalPages.setTextMatrix(0, 0);
        totalPages.showText(Integer.toString(lastPageNumber));
        totalPages.endText();
    }

    // Draw a faint foreground watermark and reuse the cached image resource.
    private void drawTournamentWatermark(PdfContentByte canvas, Document document) {
        if (tournamentWatermark == null) {
            return;
        }
        Image logo = Image.getInstance(tournamentWatermark);
        logo.scaleToFit(260f, 260f);
        logo.setAbsolutePosition(
                (document.left() + document.right() - logo.getScaledWidth()) / 2f,
                (document.bottom() + document.top() - logo.getScaledHeight()) / 2f);
        canvas.saveState();
        try {
            PdfGState transparency = new PdfGState();
            transparency.setFillOpacity(0.08f);
            canvas.setGState(transparency);
            canvas.addImage(logo);
        } catch (DocumentException ex) {
            throw new IllegalStateException("Unable to draw tournament watermark", ex);
        } finally {
            canvas.restoreState();
        }
    }

    // Draw in the page margin without changing the document flow.
    private void drawPageBorder(PdfContentByte canvas, Rectangle page) {
        float left = page.getLeft() + PAGE_BORDER_INSET;
        float right = page.getRight() - PAGE_BORDER_INSET;
        float bottom = page.getBottom() + PAGE_BORDER_INSET;
        float top = page.getTop() - PAGE_BORDER_INSET;

        canvas.saveState();
        try {
            canvas.setColorStroke(PdfStyleUtil.DEEP_GOLD);
            canvas.setLineWidth(1.2f);
            canvas.rectangle(left, bottom, right - left, top - bottom);
            canvas.stroke();

            canvas.setColorStroke(PdfStyleUtil.PREMIUM_GOLD);
            canvas.setLineWidth(0.5f);
            canvas.rectangle(left + INNER_BORDER_GAP, bottom + INNER_BORDER_GAP,
                    right - left - 2 * INNER_BORDER_GAP,
                    top - bottom - 2 * INNER_BORDER_GAP);
            canvas.stroke();

            drawCorner(canvas, left + 4f, top - 4f, 1f, -1f);
            drawCorner(canvas, right - 4f, top - 4f, -1f, -1f);
            drawCorner(canvas, left + INNER_BORDER_GAP, bottom + INNER_BORDER_GAP, 1f, 1f);
            drawCorner(canvas, right - INNER_BORDER_GAP, bottom + INNER_BORDER_GAP, -1f, 1f);
        } finally {
            canvas.restoreState();
        }
    }

    private void drawCorner(PdfContentByte canvas, float x, float y,
            float dx, float dy) {
        canvas.setColorStroke(PdfStyleUtil.DEEP_GOLD);
        canvas.setLineWidth(2f);
        canvas.moveTo(x, y + dy * 18f);
        canvas.lineTo(x, y);
        canvas.lineTo(x + dx * 18f, y);
        canvas.stroke();

        canvas.setColorStroke(PdfStyleUtil.PREMIUM_GOLD);
        canvas.setLineWidth(0.8f);
        canvas.moveTo(x + dx * 4f, y + dy * 12f);
        canvas.lineTo(x + dx * 4f, y + dy * 4f);
        canvas.lineTo(x + dx * 12f, y + dy * 4f);
        canvas.stroke();
    }

    private String fitTitle(float maxWidth) {
        String title = tournamentName.replaceAll("\\s+", " ").trim();
        if (pageFont.getWidthPoint(title, FONT_SIZE) <= maxWidth) {
            return title;
        }
        while (!title.isEmpty() && pageFont.getWidthPoint(title + "...", FONT_SIZE) > maxWidth) {
            title = title.substring(0, title.offsetByCodePoints(title.length(), -1));
        }
        return title + "...";
    }
}
