
package com.carnicos.conexion;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConexionPostgres {

    // Este nombre debe ser IDÉNTICO al que pusiste en el PASO 3
    private static final String JNDI_NAME = "jdbc/carnicos";
    
    public static Connection getConexion() {
        Connection conexion = null;
        try {
            // 1. Conectamos con el directorio del servidor (JNDI)
            Context ctx = new InitialContext();
            
            // 2. Buscamos el DataSource por su nombre
            DataSource ds = (DataSource) ctx.lookup(JNDI_NAME);
            
            // 3. Le pedimos una conexión al pool
            conexion = ds.getConnection();
            
            System.out.println("✅ Conexión obtenida desde Payara (Pool)");
            
        } catch (NamingException e) {
            System.err.println("❌ Error JNDI: No encuentro 'jdbc/carnicos'. Verifique Payara > Resources > JDBC Resources.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Error SQL: " + e.getMessage());
            e.printStackTrace();
        }
        return conexion;
    }
}
