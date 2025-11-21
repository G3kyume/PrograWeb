package com.carnicos.controlador;

import com.carnicos.model.Carne;
import com.carnicos.model.CarneDAO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/listarCarnesViejo")
public class ListarCarnesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Configuración de la respuesta
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 🔹 Configuración de CORS (Es correcta)
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // 1. 🔍 Obtener el parámetro de filtro
        // El JavaScript enviará: /listarCarnes?clasificacion=Res
        String clasificacion = request.getParameter("clasificacion");
        
        CarneDAO dao = new CarneDAO();
        List<Carne> lista;

        // 2. 🚦 Lógica de Filtrado (Nuevo)
        if (clasificacion != null && !clasificacion.trim().isEmpty() && !clasificacion.equalsIgnoreCase("Todos")) {
            // Si hay un parámetro válido (ej: "Res", "Pollo"), se filtra
            lista = dao.obtenerCarnesPorClasificacion(clasificacion);
            System.out.println("✅ [ListarCarnesServlet] Filtrando por clasificación: " + clasificacion);
        } else {
            // Si no hay parámetro o es "Todos", se obtienen todos
            lista = dao.obtenerCarnes();
            System.out.println("✅ [ListarCarnesServlet] Listando todas las carnes (sin filtro).");
        }

        // 3. 📄 Manejo de registros y mensajes
        if (lista == null || lista.isEmpty()) {
            System.out.println("⚠️ [ListarCarnesServlet] No se encontraron registros de carnes.");
        } 

        // 4. 📦 Convertir la lista a JSON
        // Gson Builder es correcto.
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(lista);

        // 5. 📤 Enviar el JSON al cliente
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        } catch (Exception e) {
            System.err.println("❌ [ListarCarnesServlet] Error al enviar JSON: " + e.getMessage());
        }
    }

    // El método doOptions es correcto y no necesita cambios.
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
