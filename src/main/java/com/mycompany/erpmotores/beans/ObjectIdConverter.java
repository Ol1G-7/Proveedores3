package com.mycompany.erpmotores.beans;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import org.bson.types.ObjectId;

@FacesConverter(value = "objectIdConverter")
public class ObjectIdConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.isEmpty() || !ObjectId.isValid(value)) {
            return null;
        }
        return new ObjectId(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof ObjectId) {
            return ((ObjectId) value).toHexString();
        } else {
            return String.valueOf(value);
        }
    }
}