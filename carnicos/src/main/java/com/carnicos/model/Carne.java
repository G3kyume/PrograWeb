package com.carnicos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.persistence.*;

@Entity
@Table(name = "productos_carnicos")
public class Carne implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "codigo_sku", unique = true)
    private String codigoSku;

    private String descripcion;

    private BigDecimal precio;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "imagen_url")
    private String imagenUrl;

    @Column(name = "dias_entrega_min")
    private Integer diasEntregaMin;

    @Column(name = "dias_entrega_max")
    private Integer diasEntregaMax;

    @Column(name = "activo")
    private Boolean activo;

    // RELACIÓN CON CATEGORÍA
    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Carne() {}

    // --- GETTERS Y SETTERS ESTANDARIZADOS ---
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Corrección: De getSKU a getCodigoSku
    public String getCodigoSku() { return codigoSku; }
    public void setCodigoSku(String codigoSku) { this.codigoSku = codigoSku; }

    public String getDescripcion() { return descripcion; } // Corrección: getDescripcion
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    // Corrección: De getStock a getStockActual
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    // Corrección: De getImagenURL a getImagenUrl (CamelCase estricto)
    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public Integer getDiasEntregaMin() { return diasEntregaMin; }
    public void setDiasEntregaMin(Integer diasEntregaMin) { this.diasEntregaMin = diasEntregaMin; }

    public Integer getDiasEntregaMax() { return diasEntregaMax; }
    public void setDiasEntregaMax(Integer diasEntregaMax) { this.diasEntregaMax = diasEntregaMax; }

    // Corrección: De getStatus a getActivo
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}