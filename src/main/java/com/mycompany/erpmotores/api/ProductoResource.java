package com.mycompany.erpmotores.api;

import com.mycompany.erpmotores.beans.CrearProductoRequest;
import com.mycompany.erpmotores.beans.MongoClass;
import com.mycompany.erpmotores.beans.Producto;
import com.mycompany.erpmotores.beans.Inventario;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.ObjectId;

@Path("productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {

    private MongoClass mongoDao = new MongoClass();

    @POST // Método para crear un nuevo producto y su registro de inventario inicial
    public Response crearProductoEInventario(CrearProductoRequest request) {
        
        Producto producto = request.getProducto();
        Inventario inventario = request.getInventarioInicial();

        // --- 1. VALIDACIÓN DE DATOS REQUERIDOS ---
        if (producto == null || inventario == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"El cuerpo de la petición debe contener 'producto' e 'inventarioInicial'.\"}").build();
        }
        if (producto.getNombre() == null || producto.getCodigoProducto() == null || producto.getPrecio() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Faltan campos obligatorios en el producto (nombre, codigoProducto, precio).\"}").build();
        }
        if (inventario.getStock() < 0) {
             return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"El stock inicial no puede ser negativo.\"}").build();
        }

        // --- 2. CREACIÓN DEL DOCUMENTO DE PRODUCTO ---
        
        // Crear un nuevo ObjectId para el producto
        ObjectId nuevoProductoId = new ObjectId();
        Date ahora = new Date();
        
        Document productoDoc = new Document("_id", nuevoProductoId)
                .append("codigo_producto", producto.getCodigoProducto())
                .append("nombre", producto.getNombre())
                .append("marca", producto.getMarca())
                .append("categoria", producto.getCategoria())
                .append("precio", producto.getPrecio())
                .append("ultima_actualizacion", ahora)
                .append("fecha_creacion", ahora);
        
        // Manejar el subdocumento 'caracteristicas' si existe
        if (producto.getCaracteristicas() != null && !producto.getCaracteristicas().isEmpty()) {
            productoDoc.append("caracteristicas", producto.getCaracteristicas());
        }

        mongoDao.insertarDocumento("productos", productoDoc);

        // --- 3. CREACIÓN DEL DOCUMENTO DE INVENTARIO ---
        
        Document inventarioDoc = new Document()
                .append("producto_id", nuevoProductoId)
                .append("stock", inventario.getStock())
                .append("ubicacion", inventario.getUbicacion() != null ? inventario.getUbicacion() : "N/A")
                .append("minimo", inventario.getMinimo())
                .append("maximo", inventario.getMaximo())
                .append("fecha_actualizacion", ahora);

        mongoDao.insertarDocumento("inventarios", inventarioDoc);
        
        // Devolvemos el documento de producto creado como confirmación
        Document respuesta = new Document()
            .append("producto_creado", productoDoc)
            .append("inventario_inicializado", inventarioDoc);

        return Response.status(Response.Status.CREATED).entity(respuesta).build();
    }
    
    // --- NUEVOS MÉTODOS GET PARA CONSULTA ---

    @GET // Método para obtener todos los productos
    public Response obtenerTodosLosProductos() {
        try {
            // Busca todos los documentos en la colección 'productos'
            List<Document> productos = mongoDao.buscarDocumentos("productos", new Document());
            
            // Si la lista está vacía, devuelve 200 con un array vacío
            if (productos.isEmpty()) {
                return Response.status(Response.Status.OK)
                        .entity("[]").build();
            }

            // Devuelve la lista de documentos (Mongo-Documents se serializan a JSON automáticamente)
            return Response.status(Response.Status.OK).entity(productos).build();
        } catch (Exception e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Ocurrió un error interno al consultar los productos.\"}").build();
        }
    }

    @GET
    @Path("{id}") // Método para obtener un producto por su ObjectId
    public Response obtenerProductoPorId(@PathParam("id") String idString) {
        
        // 1. Validar el formato del ID
        if (idString == null || !ObjectId.isValid(idString)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"ID de producto inválido. Debe ser un string hexadecimal de 24 caracteres.\"}").build();
        }
        
        try {
            ObjectId id = new ObjectId(idString);
            Document filtro = new Document("_id", id);
            
            // Busca el documento en la colección 'productos'
            Document productoDB = mongoDao.buscarUnDocumento("productos", filtro);

            if (productoDB == null) {
                // Producto no encontrado
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\": \"Producto con ID " + idString + " no encontrado.\"}").build();
            }

            // Devuelve el documento encontrado
            return Response.status(Response.Status.OK).entity(productoDB).build();
            
        } catch (Exception e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"error\": \"Ocurrió un error interno al consultar el producto.\"}").build();
        }
    }
}
