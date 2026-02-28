package proyecto.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.dto.CitaCompletaDTO;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.repository.PrecioRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PdfService {

    @Autowired
    private PrecioRepository precioRepository;

    // --- DATOS DEL ESTUDIO ---
    private static final String ESTUDIO_NOMBRE = "INK & CO S.L.";
    private static final String ESTUDIO_DIR1  = "Calle Me Falta un Tornillo, 5, Local Bajo";
    private static final String ESTUDIO_DIR2  = "47195 Arroyo de la Encomienda (Valladolid), España";
    private static final String ESTUDIO_TEL   = "TEL: 621 89 78 27";
    private static final String ESTUDIO_EMAIL = "EMAIL: tatusys@gmail.com";
    private static final String ESTUDIO_CIF   = "CIF: 12345678Z";

    // --- COLORES PALETA ---
    // #151515 header background
    private static final float[] COLOR_HEADER    = {0.082f, 0.082f, 0.082f};
    // #11C3B9 teal accent
    private static final float[] COLOR_TEAL      = {0.067f, 0.765f, 0.725f};
    // #2A2A2A dark gray text
    private static final float[] COLOR_DARK      = {0.165f, 0.165f, 0.165f};
    // #ECECEC light gray rows
    private static final float[] COLOR_ROW_ALT   = {0.925f, 0.925f, 0.925f};
    // #C62D49 red accent
    private static final float[] COLOR_RED       = {0.776f, 0.176f, 0.286f};

    public byte[] generarFacturaPdf(CitaCompletaDTO cita, String direccionFiscal) throws IOException {

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            float pageWidth   = PDRectangle.A4.getWidth();
            float pageHeight  = PDRectangle.A4.getHeight();
            float margin      = 50;
            float contentWidth = pageWidth - 2 * margin;

            PDType1Font fontBold    = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            // Precio base
            BigDecimal precioBase = BigDecimal.ZERO;
            Optional<Precio> precioBaseOpt = precioRepository.findByCategoriaAndValor(CategoriaEnum.BASE, "SERVICIO_BASE");
            if (precioBaseOpt.isPresent()) {
                precioBase = precioBaseOpt.get().getPrecioAdicional();
            }

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {

                // ─── CABECERA OSCURA ───
                float headerHeight = 90;
                float headerY = pageHeight - headerHeight;
                setColor(cs, COLOR_HEADER, true);
                cs.addRect(0, headerY, pageWidth, headerHeight);
                cs.fill();

                // Logo
                try (InputStream logoStream = getClass().getClassLoader().getResourceAsStream("static/logo-placeholder.png")) {
                	System.out.println(">>> Logo stream: " + logoStream);
                    if (logoStream != null) {
                        PDImageXObject logo = PDImageXObject.createFromByteArray(document, logoStream.readAllBytes(), "logo");
                        float logoH = 60;
                        float logoW = logo.getWidth() * (logoH / logo.getHeight());
                        cs.drawImage(logo, margin, headerY + 15, logoW, logoH);
                    }
                }

                // "FACTURA" en cabecera (derecha)
                setColor(cs, new float[]{1f, 1f, 1f}, false);
                cs.beginText();
                cs.setFont(fontBold, 26);
                float facturaTextW = getTextWidth(fontBold, 26, "FACTURA");
                cs.newLineAtOffset(pageWidth - margin - facturaTextW, headerY + 35);
                cs.showText("FACTURA");
                cs.endText();

                // Nº Cita y Fecha bajo "FACTURA"
                setColor(cs, COLOR_TEAL, false);
                cs.beginText();
                cs.setFont(fontRegular, 9);
                String nroCita = "N de Cita: #" + cita.getIdCita();
                cs.newLineAtOffset(pageWidth - margin - getTextWidth(fontRegular, 9, nroCita), headerY + 22);
                cs.showText(nroCita);
                cs.endText();

                cs.beginText();
                cs.setFont(fontRegular, 9);
                String fechaStr = "Fecha: " + cita.getFecha();
                cs.newLineAtOffset(pageWidth - margin - getTextWidth(fontRegular, 9, fechaStr), headerY + 10);
                cs.showText(fechaStr);
                cs.endText();

                float y = headerY - 20;

                // ─── DATOS ESTUDIO ───
                setColor(cs, COLOR_DARK, false);
                for (String linea : new String[]{ESTUDIO_NOMBRE, ESTUDIO_DIR1, ESTUDIO_DIR2, ESTUDIO_TEL, ESTUDIO_EMAIL, ESTUDIO_CIF}) {
                    cs.beginText();
                    cs.setFont(fontRegular, 8);
                    cs.newLineAtOffset(margin, y);
                    cs.showText(linea);
                    cs.endText();
                    y -= 12;
                }

                y -= 10;

                // ─── LÍNEA TEAL SEPARADORA ───
                setColor(cs, COLOR_TEAL, true);
                cs.setLineWidth(1.5f);
                cs.moveTo(margin, y);
                cs.lineTo(pageWidth - margin, y);
                cs.stroke();
                y -= 20;

                // ─── DATOS CLIENTE ───
                setColor(cs, COLOR_RED, false);
                cs.beginText();
                cs.setFont(fontBold, 10);
                cs.newLineAtOffset(margin, y);
                cs.showText("Cliente:");
                cs.endText();
                y -= 15;

                setColor(cs, COLOR_DARK, false);
                String nombreCompleto = cita.getClienteNombre() + " " + cita.getClienteApellido1()
                        + (cita.getClienteApellido2() != null ? " " + cita.getClienteApellido2() : "");

                for (String linea : new String[]{
                        nombreCompleto,
                        direccionFiscal != null ? direccionFiscal : "-",
                        cita.getClienteDocumentoIdentificacion() != null ? cita.getClienteDocumentoIdentificacion() : "-"
                }) {
                    cs.beginText();
                    cs.setFont(fontRegular, 10);
                    cs.newLineAtOffset(margin, y);
                    cs.showText(linea);
                    cs.endText();
                    y -= 15;
                }

                y -= 15;

                // ─── CABECERA TABLA ───
                float rowHeight = 22;
                float colPrecX  = pageWidth - margin - 70;

                setColor(cs, COLOR_HEADER, true);
                cs.addRect(margin, y - 5, contentWidth, rowHeight);
                cs.fill();

                setColor(cs, new float[]{1f, 1f, 1f}, false);
                cs.beginText();
                cs.setFont(fontBold, 10);
                cs.newLineAtOffset(margin + 5, y + 4);
                cs.showText("Descripcion del Servicio");
                cs.endText();

                cs.beginText();
                cs.setFont(fontBold, 10);
                cs.newLineAtOffset(colPrecX, y + 4);
                cs.showText("Precio");
                cs.endText();

                y -= rowHeight;

                // ─── FILAS TABLA ───
                String[][] filas = {
                    {"PRECIO BASE",                                              formatEur(precioBase)},
                    {safe(cita.getTipo()),       formatEur(cita.getPrecioTipo())},
                    {safe(cita.getZona()),       formatEur(cita.getPrecioZona())},
                    {safe(cita.getTamanio()),    formatEur(cita.getPrecioTamanio())},
                    {safe(cita.getDetalle()),    formatEur(cita.getPrecioDetalle())},
                    {safe(cita.getColoracion()), formatEur(cita.getPrecioColoracion())},
                    {safe(cita.getEstilo()),     formatEur(cita.getPrecioEstilo())},
                };

                boolean altRow = false;
                for (String[] fila : filas) {
                    if (altRow) {
                        setColor(cs, COLOR_ROW_ALT, true);
                        cs.addRect(margin, y - 5, contentWidth, rowHeight);
                        cs.fill();
                    }
                    setColor(cs, COLOR_DARK, false);
                    cs.beginText();
                    cs.setFont(fontRegular, 10);
                    cs.newLineAtOffset(margin + 5, y + 4);
                    cs.showText(fila[0]);
                    cs.endText();

                    cs.beginText();
                    cs.setFont(fontRegular, 10);
                    cs.newLineAtOffset(colPrecX, y + 4);
                    cs.showText(fila[1]);
                    cs.endText();

                    y -= rowHeight;
                    altRow = !altRow;
                }

                y -= 20;

                // ─── LÍNEA TEAL SEPARADORA ───
                setColor(cs, COLOR_TEAL, true);
                cs.setLineWidth(1f);
                cs.moveTo(margin, y);
                cs.lineTo(pageWidth - margin, y);
                cs.stroke();
                y -= 20;

                // ─── PIE: nota + totales ───
                setColor(cs, COLOR_DARK, false);
                cs.beginText();
                cs.setFont(fontRegular, 8);
                cs.newLineAtOffset(margin, y);
                cs.showText("Gracias por confiar en nuestro estudio de tatuajes.");
                cs.endText();

                float labelX = pageWidth - margin - 200;
                float valueX = pageWidth - margin;

                BigDecimal sinIva = nvl(cita.getPrecioSinIva());
                BigDecimal iva    = nvl(cita.getIva());
                BigDecimal total  = nvl(cita.getPrecioFinal());

                // Base imponible
                setColor(cs, COLOR_DARK, false);
                drawTotal(cs, fontBold, fontRegular, 10, labelX, valueX, y, "Base Imponible:", formatEur(sinIva));
                y -= 18;

                // IVA
                drawTotal(cs, fontBold, fontRegular, 10, labelX, valueX, y, "Cuota IVA 21%:", formatEur(iva));
                y -= 18;

                // TOTAL en teal
                setColor(cs, COLOR_TEAL, false);
                drawTotal(cs, fontBold, fontBold, 12, labelX, valueX, y, "TOTAL:", formatEur(total));
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);
            return baos.toByteArray();
        }
    }

    // ─── HELPERS ───

    private void setColor(PDPageContentStream cs, float[] rgb, boolean nonStroking) throws IOException {
        if (nonStroking) cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
        else             cs.setNonStrokingColor(rgb[0], rgb[1], rgb[2]);
    }

    private void drawTotal(PDPageContentStream cs, PDType1Font labelFont, PDType1Font valueFont,
                           int size, float labelX, float valueX, float y,
                           String label, String value) throws IOException {
        cs.beginText();
        cs.setFont(labelFont, size);
        cs.newLineAtOffset(labelX, y);
        cs.showText(label);
        cs.endText();

        cs.beginText();
        cs.setFont(valueFont, size);
        cs.newLineAtOffset(valueX - getTextWidth(valueFont, size, value), y);
        cs.showText(value);
        cs.endText();
    }

    private String formatEur(BigDecimal valor) {
        if (valor == null) return "0.00 EUR";
        return String.format("%.2f EUR", valor);
    }

    private String safe(String val) {
        return val != null ? val : "-";
    }

    private BigDecimal nvl(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private float getTextWidth(PDType1Font font, int size, String text) throws IOException {
        return font.getStringWidth(text) / 1000 * size;
    }
}