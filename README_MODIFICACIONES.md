# Créditos Simples - modificaciones implementadas

## 1. KYC en la aprobación de créditos
El ADMIN puede ver recibo, DNI frontal/reverso, facial, perfil y vivienda desde la solicitud.
Las imágenes nuevas se guardan como BLOB directamente en MySQL; ya no se genera una ruta de archivo.

## 2. Desembolso
Cuenta bancaria: BCP, BBVA, Interbank, Scotiabank, Banco de la Nación, Mibanco y Banco Falabella.
Se valida longitud de cuenta por banco y CCI de 20 dígitos.
Billetera: Yape, Plin u Otros (el usuario escribe el nombre).

## 3. Registro por pasos
El registro está dividido en 5 pasos. Las imágenes tienen vista previa antes de finalizar y quedan guardadas en BD al crear el usuario.

## 4. Pago con comprobante
El cliente entra a "Pagar cuota", ve la cuota y los datos de pago del ADMIN, carga un comprobante y obtiene vista previa.
El comprobante se guarda como BLOB.
El sistema intenta hacer OCR usando Tesseract para comprobar monto y beneficiario. Si no puede validar, marca el pago como REVISION_MANUAL.
La cuota NO se marca pagada al enviar el comprobante. Solo el ADMIN puede aprobarlo.
Un rechazo no paga la cuota y permite enviar otro comprobante.

## QR de Yape
Reemplaza el archivo:
`src/main/resources/static/images/yape-qr.png`
por tu QR real manteniendo ese nombre.

## OCR en Windows
Instala Tesseract OCR y asegúrate de que `tesseract.exe` esté en PATH. Si está en otra ubicación, define la variable de entorno:
`TESSERACT_CMD=C:\\Program Files\\Tesseract-OCR\\tesseract.exe`

El proyecto intenta usar los idiomas `spa+eng`.

## Configuración de BD
`spring.jpa.hibernate.ddl-auto=update` está activo. Al iniciar, Hibernate agregará las nuevas columnas/tablas. En una base existente, conserva un respaldo antes de ejecutar la actualización.

## 10. Modalidades semanales CREDI DIAZ S.A.
La modalidad semanal permite exactamente 6, 12 o 20 semanas:
- 6 semanas: 11% de interés total.
- 12 semanas: 20% de interés total.
- 20 semanas: 30% de interés total.
El monto de la cuota se mantiene uniforme para el plazo elegido. La distribución de capital/interés usa los porcentajes proporcionados por CREDI DIAZ como pesos crecientes y realiza el ajuste final de centavos para que la suma del capital sea exactamente el principal prestado. Los porcentajes no se muestran al cliente.

## 11. Amortización
El cliente puede solicitar la amortización del crédito desde su crédito activo/en mora. El sistema calcula únicamente el capital de las cuotas pendientes y excluye los intereses futuros. La solicitud requiere comprobante (Yape o transferencia BCP) y queda PENDIENTE_APROBACION o REVISION_MANUAL; el crédito no se liquida hasta que ADMIN apruebe.
Si el cliente intenta amortizar después de la fecha de vencimiento de la última cuota ya pagada, primero debe pagar completa la siguiente cuota; después puede amortizar las cuotas posteriores. Si la siguiente cuota se paga ese mismo día, la amortización vuelve a quedar disponible.
ADMIN puede amortizar directamente desde su panel. En efectivo no necesita comprobante y la liquidación es inmediata; en Yape/Plin/transferencia se exige comprobante.

## 12. Garantía del patrocinador
El deudor ya no acepta la garantía desde la solicitud. Cuando el patrocinador es CLIENTE, al solicitarse el crédito se crea una garantía PENDIENTE_ACEPTACION y el patrocinador recibe una notificación del navegador para aceptar o rechazar. ADMIN no necesita aceptar garantías de sus referidos directos: quedan aceptadas automáticamente. ADMIN puede ver el estado de la garantía en la solicitud y no puede activar un crédito patrocinado por un cliente hasta que la garantía esté aceptada.

## 13. Límite de referidos y notificaciones
Los clientes tienen un máximo de 3 referidos directos. La validación se realiza también contra el número real de referidos, por lo que clientes antiguos no pueden superar el nuevo máximo.
Se agregó un sistema de notificaciones del navegador mediante consulta periódica para avisar al ADMIN sobre nuevas solicitudes, pagos pendientes, amortizaciones pendientes y garantías; y al patrocinador sobre nuevas solicitudes de garantía.
