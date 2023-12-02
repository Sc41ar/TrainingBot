package org.edu.metamodel;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import org.edu.entity.Occupation;

import javax.annotation.processing.Generated;
import javax.swing.text.Style;
import java.util.Date;

@Generated(value = "JPAMetaModelEntityProcessor")
@StaticMetamodel(Occupation_.class)
public class Occupation_ {
    public static volatile SingularAttribute<Occupation, Date> date;
    public static volatile SingularAttribute<Occupation, Long> id;
    public static volatile SingularAttribute<Occupation, String> occpationName;
    public static final String DATE = "date";
    public static final String ID = "id";
    public static final String OCCUPATIONNAME = "occupationName";
}