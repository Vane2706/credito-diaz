package upeu.edu.pe.credito.service;

import org.springframework.stereotype.Service;
import upeu.edu.pe.credito.entity.CuotaCredito;
import upeu.edu.pe.credito.entity.MetodoPago;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

@Service
public class ComprobanteVerificacionService {
    public Resultado verificar(byte[] imagen, String contentType, BigDecimal montoEsperado, MetodoPago metodo) {
        if (imagen == null || imagen.length == 0) return new Resultado(false, "No se recibió el comprobante.", "");
        try {
            String texto = ejecutarOcr(imagen, contentType);
            String normalizado = normalizar(texto);
            String monto = montoEsperado.setScale(2).toPlainString();
            boolean montoOk = normalizado.contains(normalizar(monto)) ||
                    normalizado.contains(normalizar(monto.replace('.', ','))) ||
                    normalizado.contains(normalizar("S/ " + monto));
            boolean beneficiarioOk;
            if (metodo == MetodoPago.TRANSFERENCIA) {
                beneficiarioOk = contieneNombre(normalizado, "DIAZ CULQUI EDER OMAR") ||
                        normalizado.contains("43502664174002") || normalizado.contains("00243510266417400266");
            } else if (metodo == MetodoPago.YAPE) {
                beneficiarioOk = contieneNombre(normalizado, "EDER DIA") || contieneNombre(normalizado, "DIAZ CULQUI EDER OMAR");
            } else {
                beneficiarioOk = true;
            }
            boolean valido = montoOk && beneficiarioOk;
            String detalle = !montoOk ? "El OCR no encontró el monto exacto de la cuota." :
                    !beneficiarioOk ? "No se pudo validar el beneficiario de destino." : "Monto y beneficiario encontrados en el comprobante.";
            return new Resultado(valido, detalle, texto);
        } catch (Exception e) {
            return new Resultado(false, "No fue posible realizar la verificación automática. El comprobante quedará para revisión manual.", "");
        }
    }

    private String ejecutarOcr(byte[] imagen, String contentType) throws Exception {
        Path tmp = Files.createTempFile("comprobante-", extension(contentType));
        Path out = Files.createTempFile("ocr-", "");
        Files.write(tmp, imagen);
        String exe = System.getenv().getOrDefault("TESSERACT_CMD", "tesseract");
        ProcessBuilder pb = new ProcessBuilder(exe, tmp.toString(), out.toString(), "-l", "spa+eng", "--psm", "6");
        pb.redirectErrorStream(true);
        Process process = pb.start();
        String log = new String(process.getInputStream().readAllBytes());
        int code = process.waitFor();
        Path txt = Path.of(out + ".txt");
        try {
            if (code != 0 || !Files.exists(txt)) throw new IllegalStateException(log);
            return Files.readString(txt);
        } finally {
            Files.deleteIfExists(tmp); Files.deleteIfExists(out); Files.deleteIfExists(txt);
        }
    }
    private String extension(String type) { return type != null && type.contains("png") ? ".png" : ".jpg"; }
    private boolean contieneNombre(String texto, String esperado) {
        String[] tokens = esperado.split(" ");
        int encontrados = 0;
        for (String t : tokens) if (texto.contains(normalizar(t))) encontrados++;
        return encontrados >= Math.max(2, tokens.length - 1);
    }
    private String normalizar(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9.,/ ]", " ").replaceAll("\\s+", " ").trim();
    }
    public record Resultado(boolean valido, String detalle, String textoDetectado) {}
}
