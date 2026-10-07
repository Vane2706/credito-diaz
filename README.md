# Sistema de Créditos - versión corregida

## Reglas implementadas

- Perfil de cliente con datos personales y documentación KYC.
- Actualización anual de datos personales y documentación.
- Recibo de luz/agua, DNI frontal/reverso, foto facial, foto de perfil y vivienda.
- Montos de crédito únicamente en múltiplos de S/ 100.
- Límite inicial de S/ 100.
- Modalidad semanal compuesta: 12 semanas, 20% de interés total.
- Modalidad mensual simple: 10% de interés mensual, capital en la última cuota.
- Bonificación al recomendante después de recibir el interés: mensual por cuota y semanal al liquidar.
- Garantía del 100% del saldo no pagado.
- Mora automática diaria y penalización de límite cada 3 días.
- Deudor eliminado por mora.
- Garante pierde score, directos máximos y puede ser eliminado después de 3 referidos morosos.
- Notificación al garante con 7 días para cubrir la garantía.
- Cobro primero desde bonos y luego ahorro.
- Si se utiliza el ahorro de garantía, el garante pasa a INACTIVO.
- Si la garantía vence sin cubrirse, el garante pasa a ELIMINADO_POR_MORA.
- Pago de cuotas con registro histórico en la entidad Pago.
- Garantía pagada marca el crédito como EJECUTADO_GARANTIA y liquida las cuotas pendientes mediante origen GARANTE.
- Autenticación real con Spring Security y autorización por roles.
- Archivos KYC protegidos; no se exponen como una carpeta pública.
- Uso de BigDecimal para importes monetarios.
- Activación por ahorro de S/ 100 durante un año.
- Rango LIDER preparado para la regla anual de crédito mínimo S/ 1,000.

## MLM

El documento entregado indica: "Agregar el MLM para la bonificación especificado en PDF". El PDF con las reglas exactas de niveles, porcentajes y profundidad no fue incluido en los archivos recibidos. Por eso no se inventaron porcentajes o niveles MLM que no estén respaldados por la documentación disponible. La relación patrocinador -> referido y la bonificación directa indicada en el documento sí están implementadas y el modelo queda preparado para incorporar el esquema MLM cuando se entregue el PDF.

## Primer arranque

1. Crear una base de datos MySQL llamada `credito`.
2. Revisar `application.yml` y, de preferencia, definir `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` como variables de entorno.
3. El proyecto usa `ddl-auto: update` para conservar datos durante desarrollo. Si vienes de la versión anterior y la estructura ya contiene datos incompatibles, realiza una migración controlada o crea una base nueva antes de probar esta versión.
4. Ejecutar con Java 17.
5. Abrir `http://localhost:5000/admin/registro` para crear el administrador inicial.
6. Iniciar sesión desde `http://localhost:5000/login`.

## Importante

El pago es actualmente un registro de pago confirmado dentro del sistema; no integra una pasarela bancaria/Yape/Plin real. Los métodos Yape, Plin y transferencia representan el medio declarado por el operador. Una integración real requiere API/proveedor y conciliación de operaciones.
