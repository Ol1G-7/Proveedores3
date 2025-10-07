package com.mycompany.erpmotores.api;

import com.mycompany.erpmotores.beans.DetalleVenta;
import com.mycompany.erpmotores.beans.MongoClass;
import com.mycompany.erpmotores.beans.Venta;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;

@Path("ventas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VentaResource {

    private MongoClass mongoDao = new MongoClass();

    @POST // Método para crear una nueva venta
    public Response crearVenta(Venta ventaRecibida) {
        
        // --- 0. VALIDACIÓN DE FORMATO DE IDs (GRACEFUL FAIL) ---
        // Chequea si el 'usuarioId' es null. Esto ocurre si el ObjectIdAdapter falló en la conversión,
        // lo que significa que el string de entrada no era de 24 caracteres hexadecimales.
        if (ventaRecibida.getUsuarioId() == null) {
            // Devolver un error JSON con el estado 400 (Bad Request)
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"El campo 'usuarioId' no tiene un formato de ObjectId válido (debe ser un string hexadecimal de 24 caracteres).\"}")
                    .build();
        }

        // --- 1. VALIDACIÓN Y CÁLCULO ---
        if (ventaRecibida.getDetalle() == null || ventaRecibida.getDetalle().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"La venta debe tener al menos un producto.\"}").build();
        }

        double totalCalculado = 0;
        List<Document> detalleDocs = new ArrayList<>();

        for (DetalleVenta item : ventaRecibida.getDetalle()) {
            
            // Validar que el ID del producto es válido (chequeando el null que devuelve el adaptador)
            if (item.getProductoId() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Uno de los 'productoId' en el detalle no tiene un formato de ObjectId válido (debe ser un string hexadecimal de 24 caracteres).\"}")
                        .build();
            }
            
            // Buscar el producto en la base de datos para obtener el precio real
            Document filtroProducto = new Document("_id", item.getProductoId());
            Document productoDB = mongoDao.buscarUnDocumento("productos", filtroProducto);

            if (productoDB == null) {
                // El ID tiene formato válido, pero el producto no existe en la base de datos.
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"error\": \"Producto no encontrado con ID: " + item.getProductoId().toHexString() + "\"}").build();
            }

            // Validar stock en inventario
            Document filtroInventario = new Document("producto_id", item.getProductoId());
            Document inventarioDB = mongoDao.buscarUnDocumento("inventarios", filtroInventario);
            int stockActual = inventarioDB != null ? inventarioDB.getInteger("stock", 0) : 0;

            if (stockActual < item.getCantidad()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"error\": \"Stock insuficiente para: " + productoDB.getString("nombre") + " (Stock actual: " + stockActual + ")\"}").build();
            }

            // Calcular subtotal y sumar al total
            double precioReal = productoDB.get("precio", Number.class).doubleValue();
            double subtotal = precioReal * item.getCantidad();
            totalCalculado += subtotal;

            // Crear el subdocumento para el detalle de la venta
            Document detalleDoc = new Document()
                    .append("producto_id", item.getProductoId())
                    .append("nombre", productoDB.getString("nombre"))
                    .append("cantidad", item.getCantidad())
                    .append("precio_unitario", precioReal)
                    .append("subtotal", subtotal);
            detalleDocs.add(detalleDoc);
        }

        // --- 2. ACTUALIZACIÓN DE INVENTARIO ---
        // Esta parte solo se ejecuta si todas las validaciones previas pasaron exitosamente.
        for (DetalleVenta item : ventaRecibida.getDetalle()) {
            Document filtroInventario = new Document("producto_id", item.getProductoId());
            Document actualizacionStock = new Document("$inc", new Document("stock", -item.getCantidad()));
            mongoDao.actualizarUnDocumento("inventarios", filtroInventario, actualizacionStock);
        }

        // --- 3. CREACIÓN DEL DOCUMENTO DE VENTA ---
        String folio = "VTA-" + System.currentTimeMillis();

        Document ventaDoc = new Document()
                .append("folio_venta", folio)
                .append("fecha_venta", new Date())
                .append("usuario_id", ventaRecibida.getUsuarioId())
                .append("total", totalCalculado)
                .append("status", "Completada")
                .append("detalle", detalleDocs);
        
        mongoDao.insertarDocumento("ventas", ventaDoc);
        
        // Devolvemos el documento creado como confirmación
        return Response.status(Response.Status.CREATED).entity(ventaDoc).build();
    }
    
    // Aquí puedes añadir los métodos GET para consultar ventas
}
