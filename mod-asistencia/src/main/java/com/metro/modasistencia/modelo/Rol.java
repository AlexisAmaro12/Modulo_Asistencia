package com.metro.modasistencia.modelo;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Entity //Clase para indicar la entidad de rol y sus metodos getter, setter, la cual nos servira para la seguridad basada en roles
public class Rol implements Serializable {

        private static final long serialVersionUID = 6353963609310956029L;

        @Id
        @Column(name = "id_rol")
        @GeneratedValue(strategy= GenerationType.AUTO, generator="native")
        @GenericGenerator(name="native",strategy="native")
        private Long id;

        @Column(name = "rol_nombre")
        private String nombre;

        @Column(name = "descripcion_rol")
        private String descripcion;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setName(String nombre) {
            this.nombre = nombre;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((descripcion == null) ? 0 : descripcion.hashCode());
            result = prime * result + ((id == null) ? 0 : id.hashCode());
            result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Rol other = (Rol) obj;
            if (descripcion == null) {
                if (other.descripcion != null)
                    return false;
            } else if (!descripcion.equals(other.descripcion))
                return false;
            if (id == null) {
                if (other.id != null)
                    return false;
            } else if (!id.equals(other.id))
                return false;
            if (nombre == null) {
                if (other.nombre != null)
                    return false;
            } else if (!nombre.equals(other.nombre))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return nombre;
        }
}

