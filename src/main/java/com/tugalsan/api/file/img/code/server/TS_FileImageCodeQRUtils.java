package com.tugalsan.api.file.img.code.server;

import module com.tugalsan.api.function;
import module com.google.zxing;
import module com.google.zxing.javase;
import module java.desktop;

public class TS_FileImageCodeQRUtils {
    
    private TS_FileImageCodeQRUtils(){
        
    }

    private static BitMatrix toMatrix(int width, int height, String barcodeText) {
        return TGS_FuncMTCUtils.call(() -> {
            return new QRCodeWriter().encode(barcodeText, BarcodeFormat.QR_CODE, width, height);
        });
    }

    public static BufferedImage toQR(int width, int height, String barcodeText) {
        var qrMatrix = toMatrix(width, height, barcodeText);
        return MatrixToImageWriter.toBufferedImage(qrMatrix);
    }

    public static BufferedImage toQRwithLabels(int width, int height, String barcodeText, String lblTop, String lblBottom) {
        var qrMatrix = toMatrix(width, height, barcodeText);
        var qrWidth = qrMatrix.getWidth();
        var qrHeight = qrMatrix.getHeight();

        //Here, we create a BufferedImage instance that temporarily holds the QR code by drawing its pixels on a Graphics2D object.
        var qrImage = new BufferedImage(qrWidth, qrHeight, BufferedImage.TYPE_INT_RGB);
        var qrImageGraphics = qrImage.createGraphics();
        qrImageGraphics.setColor(Color.WHITE);
        qrImageGraphics.fillRect(0, 0, qrWidth, qrHeight);
        qrImageGraphics.setColor(Color.BLACK);
        for (var i = 0; i < qrWidth; i++) {
            for (var j = 0; j < qrHeight; j++) {
                if (qrMatrix.get(i, j)) {
                    qrImageGraphics.fillRect(i, j, 1, 1);
                }
            }
        }
        //Next, let’s calculate the dimensions required to accommodate the text and QR code:
        var fontMetrics = qrImageGraphics.getFontMetrics();
        int textWidthTop = fontMetrics.stringWidth(lblTop);
        int textWidthBottom = fontMetrics.stringWidth(lblBottom);

        //Finally, let’s create a new BufferedImage instance to hold the final image:
        int finWidth = Math.max(qrWidth, Math.max(textWidthTop, textWidthBottom)) + 1;
        int finHeight = qrHeight + fontMetrics.getHeight() + fontMetrics.getAscent() + 1;
        var finImage = new BufferedImage(finWidth, finHeight, BufferedImage.TYPE_INT_RGB);
        var finImageGraphics = finImage.createGraphics();
        finImageGraphics.setColor(Color.WHITE);
        finImageGraphics.fillRect(0, 0, finWidth, finHeight);
        finImageGraphics.setColor(Color.BLACK);
        finImageGraphics.drawImage(qrImage, (finWidth - qrWidth) / 2, fontMetrics.getAscent() + 2, null);
        finImageGraphics.drawString(lblTop, (finWidth - textWidthTop) / 2, fontMetrics.getAscent() + 2);
        finImageGraphics.drawString(lblBottom, (finWidth - textWidthBottom) / 2, finHeight - fontMetrics.getDescent() - 5);

        return finImage;
    }
}
