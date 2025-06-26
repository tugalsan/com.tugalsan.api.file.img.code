package com.tugalsan.api.file.img.code.server;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class TS_FileImageCodeQRUtils {

    public static BufferedImage createQR(String barcodeText) throws Exception {
        var barcodeWriter = new QRCodeWriter();
        var matrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public static BufferedImage createQRwithText(String barcodeText, String topText, String bottomText) throws WriterException, IOException {
        var barcodeWriter = new QRCodeWriter();
        var matrix = barcodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200);
        return modifiedQRCode(matrix, topText, bottomText);
    }

    public static BufferedImage modifiedQRCode(BitMatrix matrix, String topText, String bottomText) {
        //Here, we create a BufferedImage instance that temporarily holds the QR code by drawing its pixels on a Graphics2D object.
        var matrixWidth = matrix.getWidth();
        var matrixHeight = matrix.getHeight();
        var image = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
        var graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixHeight);
        graphics.setColor(Color.BLACK);
        for (var i = 0; i < matrixWidth; i++) {
            for (var j = 0; j < matrixHeight; j++) {
                if (matrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        //Next, let’s calculate the dimensions required to accommodate the text and QR code:
        var fontMetrics = graphics.getFontMetrics();
        int topTextWidth = fontMetrics.stringWidth(topText);
        int bottomTextWidth = fontMetrics.stringWidth(bottomText);
        int finalWidth = Math.max(matrixWidth, Math.max(topTextWidth, bottomTextWidth)) + 1;
        int finalHeight = matrixHeight + fontMetrics.getHeight() + fontMetrics.getAscent() + 1;

        //Finally, let’s create a new BufferedImage instance to hold the final image:
        var finalImage = new BufferedImage(finalWidth, finalHeight, BufferedImage.TYPE_INT_RGB);
        var finalGraphics = finalImage.createGraphics();
        finalGraphics.setColor(Color.WHITE);
        finalGraphics.fillRect(0, 0, finalWidth, finalHeight);
        finalGraphics.setColor(Color.BLACK);
        finalGraphics.drawImage(image, (finalWidth - matrixWidth) / 2, fontMetrics.getAscent() + 2, null);
        finalGraphics.drawString(topText, (finalWidth - topTextWidth) / 2, fontMetrics.getAscent() + 2);
        finalGraphics.drawString(bottomText, (finalWidth - bottomTextWidth) / 2, finalHeight - fontMetrics.getDescent() - 5);

        return finalImage;
    }
}
