package com.beerstar.beerstar.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout; // Importar TableLayout
import android.widget.TableRow;    // Importar TableRow
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.beerstar.beerstar.R;
import com.beerstar.beerstar.response.responseCarrito;
import com.beerstar.beerstar.response.responseUsuario;
import com.beerstar.beerstar.response.responsePedido;
import com.beerstar.beerstar.response.responsePedidoItem;
import com.beerstar.beerstar.service.ApiService;
import com.beerstar.beerstar.service.RetrofitClient;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Phrase; // Importar Phrase para celdas de tabla PDF
import com.itextpdf.text.pdf.PdfPTable; // Importar PdfPTable para tabla PDF
import com.itextpdf.text.pdf.PdfPCell; // Importar PdfPCell para celdas de tabla PDF

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale; // Para String.format con Locale

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class fragmentDetallePedido extends Fragment {

    private static final String TAG = "fragmentDetallePedido";
    private TextView textUsuario, textTotal; // textArticulos ya no es necesario como TextView simple
    private Button buttonProcederPago, buttonAbrirPdf;
    private File pdfFile;
    private TableLayout tableLayoutArticulos; // Nueva referencia al TableLayout

    private List<responseCarrito> articulos;
    private responseUsuario usuario;

    public static fragmentDetallePedido newInstance(List<responseCarrito> articulos, responseUsuario usuario) {
        fragmentDetallePedido fragment = new fragmentDetallePedido();
        Bundle args = new Bundle();
        args.putSerializable("articulos", (java.io.Serializable) articulos);
        args.putSerializable("usuario", usuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_pedido, container, false);

        textUsuario = view.findViewById(R.id.textUsuario);
        // textArticulos = view.findViewById(R.id.textArticulos); // ¡Elimina esta línea!
        textTotal = view.findViewById(R.id.textTotal);
        buttonProcederPago = view.findViewById(R.id.buttonRealizarPedido);
        buttonAbrirPdf = view.findViewById(R.id.buttonAbrirPdf);
        tableLayoutArticulos = view.findViewById(R.id.tableLayoutArticulos); // Referencia al nuevo TableLayout

        if (getArguments() != null) {
            articulos = (List<responseCarrito>) getArguments().getSerializable("articulos");
            usuario = (responseUsuario) getArguments().getSerializable("usuario");
        }

        // Configuración del usuario
        if (usuario != null && usuario.getNombre() != null && !usuario.getNombre().isEmpty()) {
            textUsuario.setText("Usuario: " + usuario.getNombre());
        } else if (usuario != null && usuario.getEmail() != null && !usuario.getEmail().isEmpty()) {
            textUsuario.setText("Usuario: " + usuario.getEmail());
        } else {
            textUsuario.setText("Usuario: Desconocido");
        }

        // Llenar la tabla de artículos dinámicamente
        double subtotal = 0;
        if (articulos != null) {
            for (responseCarrito articulo : articulos) {
                // Crear una nueva fila para cada artículo
                TableRow row = new TableRow(getContext());
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);

                // TextView para el Nombre
                TextView tvNombre = new TextView(getContext());
                tvNombre.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)); // weight 1
                tvNombre.setText(articulo.getNombre());
                tvNombre.setPadding(4, 4, 4, 4);
                tvNombre.setTextColor(getResources().getColor(android.R.color.black));
                tvNombre.setTextSize(16);
                row.addView(tvNombre);


                TextView tvCantidad = new TextView(getContext());
                tvCantidad.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tvCantidad.setText(String.valueOf(articulo.getCantidad())); // CORRECTED LINE
                tvCantidad.setPadding(4, 4, 4, 4);
                tvCantidad.setGravity(View.TEXT_ALIGNMENT_CENTER); // Centrar cantidad
                tvCantidad.setTextColor(getResources().getColor(android.R.color.black));
                tvCantidad.setTextSize(16);
                row.addView(tvCantidad);

                // TextView para el Precio Unitario
                TextView tvPrecioUnitario = new TextView(getContext());
                tvPrecioUnitario.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tvPrecioUnitario.setText(String.format(Locale.getDefault(), "%.2f€", articulo.getPrecio()));
                tvPrecioUnitario.setPadding(4, 4, 4, 4);
                tvPrecioUnitario.setGravity(View.TEXT_ALIGNMENT_VIEW_END);

                tvPrecioUnitario.setTextColor(getResources().getColor(android.R.color.black));
                tvPrecioUnitario.setTextSize(16);
                row.addView(tvPrecioUnitario);

                // TextView para el Total por fila
                // TextView para el Total por fila
                double totalFila = articulo.getPrecio() * articulo.getCantidad();
                TextView tvTotalFila = new TextView(getContext());
                tvTotalFila.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                tvTotalFila.setText(String.format(Locale.getDefault(), "%.2f€", totalFila));
                tvTotalFila.setPadding(4, 4, 4, 4);
                tvTotalFila.setGravity(View.TEXT_ALIGNMENT_VIEW_END); // Alinear a la derecha
                tvTotalFila.setTypeface(null, android.graphics.Typeface.BOLD); // CORRECTED LINE
                tvTotalFila.setTextColor(getResources().getColor(android.R.color.black));
                tvTotalFila.setTextSize(16);
                row.addView(tvTotalFila);


                // Añadir la fila al TableLayout
                tableLayoutArticulos.addView(row);

                // Calcular subtotal general
                subtotal += totalFila;
            }
        }

        double iva = subtotal * 0.21;
        double totalConIva = subtotal + iva;

        textTotal.setText("Subtotal: " + String.format(Locale.getDefault(), "%.2f€", subtotal) +
                "\nIVA: " + String.format(Locale.getDefault(), "%.2f€", iva) +
                "\nTotal: " + String.format(Locale.getDefault(), "%.2f€", totalConIva));

        buttonProcederPago.setOnClickListener(v -> {
            realizarPedido();
        });

        double finalSubtotal = subtotal;
        double finalIva = iva;
        double finalTotalConIva = totalConIva;

        buttonAbrirPdf.setOnClickListener(v -> {
            generarPdf(usuario, articulos, finalSubtotal, finalIva, finalTotalConIva);
            abrirPdf();
        });

        return view;
    }

    private void realizarPedido() {
        if (usuario == null || articulos == null || articulos.isEmpty()) {
            Toast.makeText(getContext(), "No se puede realizar el pedido: faltan datos o el carrito está vacío.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<responsePedidoItem> pedidoItems = new ArrayList<>();
        for (responseCarrito carritoItem : articulos) {
            Long idProductoOLote = Long.valueOf(carritoItem.getId());
            int cantidad = carritoItem.getCantidad();
            double precioUnitario = carritoItem.getPrecio();

            String productType;
            if (carritoItem.getTipo() == responseCarrito.Tipo.PRODUCTO) {
                productType = "BEER";
            } else if (carritoItem.getTipo() == responseCarrito.Tipo.LOTE) {
                productType = "BATCH";
            } else {
                Log.e(TAG, "Tipo de producto desconocido en responseCarrito para item: " + carritoItem.getNombre());
                productType = "UNKNOWN";
            }

            responsePedidoItem item = new responsePedidoItem(
                    idProductoOLote,
                    carritoItem.getNombre(),
                    carritoItem.getDescripcion(),
                    carritoItem.getImagenUrl(),
                    cantidad,
                    precioUnitario,
                    productType
            );
            pedidoItems.add(item);
        }

        double totalAmount = 0;
        for (responseCarrito item : articulos) {
            totalAmount += item.getPrecio() * item.getCantidad();
        }

        String shippingAddress = (usuario.getDireccion() != null && !usuario.getDireccion().isEmpty()) ? usuario.getDireccion() : "Dirección no especificada";
        String paymentMethod = "Efectivo al Recibir";

        responsePedido nuevoPedido = new responsePedido();
        nuevoPedido.setUserId(usuario.getIdUsuario());
        nuevoPedido.setItems(pedidoItems);
        nuevoPedido.setShippingAddress(shippingAddress);
        nuevoPedido.setPaymentMethod(paymentMethod);
        nuevoPedido.setTotalAmount(totalAmount);
        nuevoPedido.setStatus("PENDING");

        ApiService apiService = RetrofitClient.getApiService();
        Call<responsePedido> call = apiService.crearPedido(nuevoPedido);

        call.enqueue(new Callback<responsePedido>() {
            @Override
            public void onResponse(Call<responsePedido> call, Response<responsePedido> response) {
                if (response.isSuccessful() && response.body() != null) {
                    responsePedido pedidoCreado = response.body();
                    Log.d(TAG, "Pedido creado exitosamente: " + pedidoCreado.toString());
                    Toast.makeText(getContext(), "Pedido realizado con éxito!", Toast.LENGTH_LONG).show();
                } else {
                    String errorBody = "Error desconocido";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al leer errorBody: " + e.getMessage());
                    }
                    Log.e(TAG, "Error al crear el pedido: " + response.code() + " - " + errorBody);
                    Toast.makeText(getContext(), "Error al realizar el pedido: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<responsePedido> call, Throwable t) {
                Log.e(TAG, "Fallo en la conexión al crear el pedido: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Error de red. Revisa tu conexión a internet.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void generarPdf(responseUsuario usuario, List<responseCarrito> articulos, double subtotal, double iva, double total) {
        try {
            File directorio = new File(requireContext().getExternalFilesDir(null), "facturas");
            if (!directorio.exists()) directorio.mkdirs();

            pdfFile = new File(directorio, "factura_pedido_" + System.currentTimeMillis() + ".pdf");

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            try {
                Bitmap bitmap = BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.logopro);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(byteArray);
                logo.scaleToFit(100, 100);
                logo.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception e) {
                document.add(new Paragraph("BeerStar"));
            }

            Paragraph titulo = new Paragraph("FACTURA DE COMPRA\n\n");
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(titulo);

            String cliente = (usuario != null && usuario.getNombre() != null) ? usuario.getNombre() : (usuario != null ? usuario.getDireccion() : "Desconocido");
            document.add(new Paragraph("Cliente: " + cliente));
            document.add(new Paragraph("Fecha: " + DateFormat.getDateTimeInstance().format(new Date())));
            document.add(new Paragraph("--------------------------------------------------------------------------------------------------------------------------------"));

            // --- INICIO: Crear la tabla para los artículos en el PDF ---
            document.add(new Paragraph("\nARTÍCULOS\n"));

            PdfPTable table = new PdfPTable(4); // 4 columnas: Nombre, Cantidad, Precio Unitario, Total
            table.setWidthPercentage(100); // La tabla ocupa el 100% del ancho del documento
            table.setSpacingBefore(10f); // Espacio antes de la tabla
            table.setSpacingAfter(10f); // Espacio después de la tabla

            // Encabezados de la tabla
            PdfPCell cellNombre = new PdfPCell(new Phrase("Nombre"));
            cellNombre.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            table.addCell(cellNombre);

            PdfPCell cellCantidad = new PdfPCell(new Phrase("Cantidad"));
            cellCantidad.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
            table.addCell(cellCantidad);

            PdfPCell cellPrecioUnitario = new PdfPCell(new Phrase("P. Unit."));
            cellPrecioUnitario.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            table.addCell(cellPrecioUnitario);

            PdfPCell cellTotal = new PdfPCell(new Phrase("Total"));
            cellTotal.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            table.addCell(cellTotal);

            // Añadir las filas de los artículos
            if (articulos != null) {
                for (responseCarrito articulo : articulos) {
                    table.addCell(new Phrase(articulo.getNombre()));
                    table.addCell(new Phrase(String.valueOf(articulo.getCantidad())));
                    table.addCell(new Phrase(String.format(Locale.getDefault(), "%.2f€", articulo.getPrecio())));
                    table.addCell(new Phrase(String.format(Locale.getDefault(), "%.2f€", articulo.getPrecio() * articulo.getCantidad())));
                }
            }
            document.add(table);
            // --- FIN: Crear la tabla para los artículos en el PDF ---

            document.add(new Paragraph("\nResumen de pago"));
            document.add(new Paragraph("Subtotal: " + String.format(Locale.getDefault(), "%.2f€", subtotal)));
            document.add(new Paragraph("IVA (21%): " + String.format(Locale.getDefault(), "%.2f€", iva)));
            document.add(new Paragraph("TOTAL A PAGAR: " + String.format(Locale.getDefault(), "%.2f€", total)));
            document.add(new Paragraph("\n--------------------------------------------------------------------------------------------------------------------------------"));

            Paragraph gracias = new Paragraph("¡Gracias por comprar en BeerStar!");
            gracias.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(gracias);

            document.close();
            Toast.makeText(getContext(), "PDF generado correctamente", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error generando PDF: ", e);
            Toast.makeText(getContext(), "Error generando PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void abrirPdf() {
        if (pdfFile != null && pdfFile.exists()) {
            Uri uri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".provider",
                    pdfFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Abrir con..."));
                } else {
                    Toast.makeText(getContext(), "No hay una aplicación instalada para abrir PDFs.", Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error al abrir PDF: ", e);
                Toast.makeText(getContext(), "Error al abrir PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "El archivo PDF no se ha generado aún", Toast.LENGTH_SHORT).show();
        }
    }
}