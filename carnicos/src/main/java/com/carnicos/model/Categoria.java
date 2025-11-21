package com.carnicos.model;

import java.io.Serializable;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias")
public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    // Esto es opcional: Permite obtener la lista de productos de una categoría
    @OneToMany(mappedBy = "categoria")
    private List<Carne> Carnes;

    // Constructor vacío (Obligatorio para JPA)
    public Categoria() {}

    // Getters y Setters (Generar con Alt+Insert en NetBeans)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDesc() { return descripcion; }
    public void setDesc(String descripcion) { this.descripcion = descripcion; }
}