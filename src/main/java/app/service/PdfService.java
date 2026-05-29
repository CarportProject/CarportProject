package app.service;

import app.entities.Material;
import app.entities.MaterialListEntry;
import app.entities.Order;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class PdfService {

    public byte[] svgToPdf(String svgContent) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PDFTranscoder transcoder = new PDFTranscoder();

        TranscoderInput input = new TranscoderInput(
                new ByteArrayInputStream(svgContent.getBytes(StandardCharsets.UTF_8))
        );
        TranscoderOutput output = new TranscoderOutput(outputStream);

        transcoder.transcode(input, output);

        return outputStream.toByteArray();
    }

    public byte[] tableToPdf(String htmlTable) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlTable);
        renderer.layout();
        renderer.createPDF(outputStream);
        return outputStream.toByteArray();
    }

    public byte[] mergePdfs(byte[] pdf1, byte[] pdf2) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PDFMergerUtility merger = new PDFMergerUtility();
        merger.addSource(new RandomAccessReadBuffer(pdf1));
        merger.addSource(new RandomAccessReadBuffer(pdf2));
        merger.setDestinationStream(outputStream);
        merger.mergeDocuments(null);


        return outputStream.toByteArray();
    }

    public String buildMaterialListHtml(List<MaterialListEntry> entries) {
        StringBuilder rows = new StringBuilder();

        for (MaterialListEntry entry : entries) {
            rows.append("<tr>")
                    .append("<td>").append(entry.material().getWidth()).append(" x ").append(entry.material().getHeight()).append(" mm.")
                    .append(entry.material().getName()).append("</td>")
                    .append("<td>").append(entry.length() / 10).append("</td>")
                    .append("<td>").append(entry.amount()).append("</td>")
                    .append("<td>stk</td>")
                    .append("<td>").append(entry.description()).append("</td>")
                    .append("</tr>");
        }

        return """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
                <html xmlns="http://www.w3.org/1999/xhtml">
                <body>
                    <h2>Stykliste</h2>
                    <table border="1" cellpadding="5" cellspacing="0" width="100%%">
                        <tr>
                            <th>Materiale</th>
                            <th>Længde</th>
                            <th>Antal</th>
                            <th>Enhed</th>
                            <th>Beskrivelse</th>
                        </tr>
                        %s
                    </table>
                </body>
                </html>
                """.formatted(rows.toString());
    }

}