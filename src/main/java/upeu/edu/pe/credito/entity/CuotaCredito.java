package upeu.edu.pe.credito.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "cuotas_credito")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CuotaCredito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credito_id", nullable = false)
    private Credito credito;

    private int numeroCuota;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal montoCuota = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal montoInteres = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal montoCapital = BigDecimal.ZERO;

    private LocalDate fechaVencimiento;
    private LocalDate fechaPagoReal;
    private boolean pagado = false;

    private int diasMora = 0;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal penalizacionMoraAcumulada = BigDecimal.ZERO;

    public BigDecimal getTarifaMoraDiaria() {
        if (credito == null || credito.getMontoPrestado() == null) return BigDecimal.ONE;
        BigDecimal monto = credito.getMontoPrestado();
        BigDecimal factor = monto.compareTo(new BigDecimal("500")) <= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(Math.ceil(monto.subtract(new BigDecimal("500")).doubleValue() / 500.0));
        if (credito.getModalidad() == ModalidadPago.MENSUAL_SIMPLE) {
            return new BigDecimal("2.00").add(factor.multiply(new BigDecimal("2.00")));
        }
        return BigDecimal.ONE.add(factor);
    }

    public BigDecimal getMontoTotalAPagar() {
        if (pagado) return montoCuota.setScale(2, RoundingMode.HALF_UP);
        return montoCuota.add(penalizacionMoraAcumulada).setScale(2, RoundingMode.HALF_UP);
    }

    public String getDetalleMoraCliente() {
        if (diasMora <= 0) return "Cuota al día - Sin penalizaciones.";
        return String.format("Atraso de %d día(s). Penalización acumulada: S/ %.2f (Tarifa %s: S/ %.2f/día por préstamo de S/ %.2f)",
                diasMora, penalizacionMoraAcumulada, credito != null && credito.getModalidad() == ModalidadPago.MENSUAL_SIMPLE ? "Mensual" : "Semanal",
                getTarifaMoraDiaria(), credito != null ? credito.getMontoPrestado() : BigDecimal.ZERO);
    }
}
