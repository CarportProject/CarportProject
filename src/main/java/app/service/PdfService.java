package app.service;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

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
}