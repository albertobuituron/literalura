package com.albertobuituron.literalura.services;

public interface IConvierteDatos
{
    <T> T obtenerDatos(String json, Class<T> tClass);
}
