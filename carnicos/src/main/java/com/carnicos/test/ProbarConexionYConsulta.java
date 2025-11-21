
package com.carnicos.test;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO;
import java.util.List;

public class ProbarConexionYConsulta {

    public static void main(String[] args) {
        System.out.println("Probando conexión y obtención de lista de carnes...\n");

        CarneDAO dao = new CarneDAO();

        try {
            List<Carne> lista = dao.obtenerCarnes();

            if (lista == null || lista.isEmpty()) {
                System.out.println("⚠️ No se encontraron registros en la tabla carnes.");
            } else {
                System.out.println("✅ Carnes obtenidas de la base de datos:");
                for (Carne c : lista) {
                    System.out.println("----------------------------------");
                    System.out.println("ID: " + c.getId());
                    System.out.println("Nombre: " + c.getNombre());
                    System.out.println("Clasificación: " + c.getCategoria());
                    System.out.println("Precio: " + c.getPrecio());
                    System.out.println("Descripción: " + c.getDescripcion());
                    System.out.println("Unidades: "+c.getStockActual());
                    System.out.println("Fecha de entrega: "+c.getDiasEntregaMax());
                    System.out.println("Imagen de fondo: "+c.getImagenUrl());
                    System.out.println("status: "+c.getActivo());
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error al obtener las carnes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}