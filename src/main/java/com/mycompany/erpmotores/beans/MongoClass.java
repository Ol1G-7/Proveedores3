package com.mycompany.erpmotores.beans;

// Archivo: MongoDAO.java
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase genérica de Acceso a Datos (DAO) para interactuar con MongoDB.
 * Permite realizar operaciones CRUD en cualquier colección.
 */
public class MongoClass {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    /**
     * Constructor que inicializa la conexión a la base de datos.
     * @param uri La cadena de conexión de MongoDB.
     * @param dbName El nombre de la base de datos.
     */
    public MongoClass() {
        String uri = "mongodb://localhost:27017/";
        String dbName = "Proveedores3";
        this.mongoClient = MongoClients.create(uri);
        this.database = mongoClient.getDatabase(dbName);
        System.out.println("Conexión a la base de datos '" + dbName + "' establecida.");
    }

    /**
     * Inserta un nuevo documento en la colección especificada.
     * @param collectionName El nombre de la colección.
     * @param documento El documento a insertar.
     * @return El ObjectId del documento insertado.
     */
    public ObjectId insertarDocumento(String collectionName, Document documento) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.insertOne(documento);
        return documento.getObjectId("_id");
    }

    /**
     * Busca y devuelve el primer documento que coincide con un filtro.
     * @param collectionName El nombre de la colección.
     * @param filtro El filtro de búsqueda.
     * @return El primer documento encontrado o null si no hay coincidencias.
     */
    public Document buscarUnDocumento(String collectionName, Document filtro) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(filtro).first();
    }
    
    /**
     * Busca y devuelve todos los documentos que coinciden con un filtro.
     * @param collectionName El nombre de la colección.
     * @param filtro El filtro de búsqueda.
     * @return Una lista de documentos.
     */
    public List<Document> buscarDocumentos(String collectionName, Document filtro) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        List<Document> documentos = new ArrayList<>();
        collection.find(filtro).into(documentos);
        return documentos;
    }

    /**
     * Actualiza el primer documento que coincide con un filtro.
     * @param collectionName El nombre de la colección.
     * @param filtro El filtro para encontrar el documento a actualizar.
     * @param actualizacion El documento con los campos a actualizar (ej. usando $set).
     * @return El número de documentos modificados.
     */
    public long actualizarUnDocumento(String collectionName, Document filtro, Document actualizacion) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        UpdateResult result = collection.updateOne(filtro, actualizacion);
        return result.getModifiedCount();
    }

    /**
     * Elimina el primer documento que coincide con un filtro.
     * @param collectionName El nombre de la colección.
     * @param filtro El filtro para encontrar el documento a eliminar.
     * @return El número de documentos eliminados.
     */
    public long eliminarUnDocumento(String collectionName, Document filtro) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        DeleteResult result = collection.deleteOne(filtro);
        return result.getDeletedCount();
    }

    /**
     * Cierra la conexión del cliente de MongoDB.
     */
    public void cerrarConexion() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("Conexión a MongoDB cerrada.");
        }
    }
    public static void main(String[] args) {
         String uri = "mongodb://localhost:27017/";
        MongoClass dao = new MongoClass();

        try {
            // --- TRABAJANDO CON LA COLECCIÓN 'usuarios' ---
            System.out.println("\n--- Operaciones en 'usuarios' ---");
            Document filtroAdmin = new Document("usuario", "admin");
            Document admin = dao.buscarUnDocumento("usuarios", filtroAdmin);

            if (admin != null) {
                System.out.println("Usuario encontrado: " + admin.toJson());
            } else {
                System.out.println("Usuario 'admin' no encontrado.");
                System.out.println(admin);
            }

            // --- TRABAJANDO CON LA COLECCIÓN 'productos' ---
            System.out.println("\n--- Operaciones en 'productos' ---");
            
            // 1. Insertar un producto nuevo
            Document nuevoProducto = new Document("nombre", "Laptop Gamer")
                                        .append("precio", 120.50)
                                        .append("stock", 50);
            ObjectId idProducto = dao.insertarDocumento("productos", nuevoProducto);
            System.out.println("Producto insertado con ID: " + idProducto);

            // 2. Buscar el producto que acabamos de insertar
            Document filtroProducto = new Document("_id", idProducto);
            Document productoEncontrado = dao.buscarUnDocumento("productos", filtroProducto);
            System.out.println("Producto encontrado: " + productoEncontrado.toJson());

            // 3. Actualizar el producto
            Document actualizacion = new Document("$set", new Document("precio", 150.00));
            long modificados = dao.actualizarUnDocumento("productos", filtroProducto, actualizacion);
            System.out.println("Documentos modificados: " + modificados);
            
            // 4. Eliminar el producto
            long eliminados = dao.eliminarUnDocumento("productos", filtroProducto);
            System.out.println("Documentos eliminados: " + eliminados);


        } catch (Exception e) {
            System.err.println("Ocurrió un error: " + e.getMessage());
        } finally {
            // Es crucial cerrar la conexión al final
            dao.cerrarConexion();
        }
    
    }
}
