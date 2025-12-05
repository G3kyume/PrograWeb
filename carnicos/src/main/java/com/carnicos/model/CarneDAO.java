package com.carnicos.model;

import com.carnicos.conexion.ConexionPostgres;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarneDAO {

    // SQL CONSTANTE: Lo definimos aquí para no repetirlo.
    // NOTA EL JOIN: Unimos productos (p) con categorias (c) para traer el nombre de la categoría.
    private static final String SELECT_BASE
            = "SELECT p.id, p.nombre, p.descripcion, p.precio, p.stock_actual, "
            + "       p.imagen_url, p.dias_entrega_min, p.dias_entrega_max, p.codigo_sku, "
            + "       c.id AS id_categoria, c.nombre AS nombre_categoria "
            + "FROM productos_carnicos p "
            + "JOIN categorias c ON p.categoria_id = c.id ";

    /**
     * Helper: Mapea un ResultSet a un objeto Carne actualizado.
     */
    private Carne mapearCarne(ResultSet rs) throws SQLException {
        Carne c = new Carne();

        // 1. Datos Básicos
        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setPrecio(rs.getBigDecimal("precio"));
        c.setCodigoSku(rs.getString("codigo_sku"));

        // 2. Datos actualizados (Nuevos nombres de columna)
        c.setStockActual(rs.getInt("stock_actual"));
        c.setDiasEntregaMin(rs.getInt("dias_entrega_min"));
        c.setDiasEntregaMax(rs.getInt("dias_entrega_max"));

        // 3. Mapeo de la Categoría (Objeto Completo)
        Categoria cat = new Categoria();
        cat.setId(rs.getInt("id_categoria"));
        cat.setNombre(rs.getString("nombre_categoria"));
        c.setCategoria(cat); // Asignamos el objeto categoría al producto

        // 4. Manejo seguro de imagen
        String imagen = rs.getString("imagen_url");
        if (imagen == null || imagen.trim().isEmpty()) {
            c.setImagenUrl("imgProductos/default.jpg");
        } else {
            c.setImagenUrl(imagen);
        }

        return c;
    }

    // --------------------------------------------------------------------------
    // 1. Método para OBTENER TODAS las carnes
    // --------------------------------------------------------------------------
    public List<Carne> obtenerCarnes() {
        String sql = SELECT_BASE + "ORDER BY p.id ASC;";
        return ejecutarConsulta(sql, null);
    }

    // --------------------------------------------------------------------------
    // 2. Método para FILTRAR carnes por CLASIFICACION (Nombre de Categoría)
    // --------------------------------------------------------------------------
    public List<Carne> obtenerCarnesPorClasificacion(String nombreCategoria) {
        // Filtramos por el nombre de la categoría (tabla 'c')
        String sql = SELECT_BASE + "WHERE c.nombre = ? ORDER BY p.id ASC;";
        return ejecutarConsulta(sql, nombreCategoria);
    }

    // --------------------------------------------------------------------------
    // 3. Método para buscar por NOMBRE (LIKE %texto%)
    // --------------------------------------------------------------------------
    public List<Carne> buscarPorNombre(String nombre) {
        String sql = SELECT_BASE
                + "WHERE LOWER(p.nombre) LIKE LOWER(?) "
                + "ORDER BY p.id ASC;";

        return ejecutarConsulta(sql, "%" + nombre + "%");
    }

    // Método compatibilidad: listar() llama a obtenerCarnes()
    public List<Carne> listar() {
        return obtenerCarnes();
    }

    // --------------------------------------------------------------------------
    // 4. Método para INSERTAR un producto nuevo
    // --------------------------------------------------------------------------
    public boolean insertarCarne(Carne c) {
        String sql = "INSERT INTO productos_carnicos "
                + "(nombre, descripcion, precio, stock_actual, imagen_url, dias_entrega_min, dias_entrega_max, categoria_id, codigo_sku) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionPostgres.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.setBigDecimal(3, c.getPrecio());
            ps.setInt(4, c.getStockActual());
            ps.setString(5, c.getImagenUrl());
            ps.setInt(6, c.getDiasEntregaMin());
            ps.setInt(7, c.getDiasEntregaMax());
            ps.setInt(8, c.getCategoria().getId());
            ps.setString(9, c.getCodigoSku());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error al insertar: " + e.getMessage());

            // MANEJO DE VALIDACIONES DE LA BASE DE DATOS
            if (e.getSQLState().equals("23505")) { // Código 23505 = Dato duplicado (Unique Constraint)
                System.err.println("⚠️ Error: Ya existe un producto con ese Nombre o SKU.");
            } else if (e.getSQLState().equals("23514")) { // Código 23514 = Check Violation
                System.err.println("⚠️ Error: Precio negativo o fechas inválidas.");
            }

            return false;
        }
    }

    // --------------------------------------------------------------------------
    // 3. Método general de ejecución
    // --------------------------------------------------------------------------
    private List<Carne> ejecutarConsulta(String sql, String parametro) {
        List<Carne> lista = new ArrayList<>();

        try (Connection conn = ConexionPostgres.getConexion(); PreparedStatement ps = conn.prepareStatement(sql)) {

            // Si hay parámetro (filtro), lo asignamos
            if (parametro != null) {
                ps.setString(1, parametro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCarne(rs));
                }
            }

            System.out.println("✅ [CarneDAO] Consulta exitosa. Registros: " + lista.size());

        } catch (SQLException e) {
            System.err.println("❌ [CarneDAO] Error SQL: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }
}
