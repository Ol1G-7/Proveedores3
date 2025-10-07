/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.erpmotores.beans;

import jakarta.json.bind.adapter.JsonbAdapter;
import org.bson.types.ObjectId;

public class ObjectIdAdapter implements JsonbAdapter<ObjectId, String> {

    @Override
    public String adaptToJson(ObjectId objectId) throws Exception {
        // Convierte el ObjectId de Java a una cadena de texto JSON
        return objectId.toHexString();
    }

    @Override
    public ObjectId adaptFromJson(String s) throws Exception {
        // Convierte la cadena de texto JSON a un ObjectId de Java
        return new ObjectId(s);
    }
}