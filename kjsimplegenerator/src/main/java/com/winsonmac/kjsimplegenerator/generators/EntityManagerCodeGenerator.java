package com.winsonmac.kjsimplegenerator.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.winsonmac.kjsimplegenerator.annotations.Column;
import com.winsonmac.kjsimplegenerator.annotations.Entity;

import java.io.IOException;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

public class EntityManagerCodeGenerator extends BaseCodeGenerator {

    private String SPACE = " ";
    private String COMMA = ",";
    private String CLASS_NAME = "KJEntityManager";

    public EntityManagerCodeGenerator(ProcessProvider processProvider) {
        super(processProvider);
    }

    public void process() {
        try {
            TypeSpec.Builder entityManagerClass = TypeSpec
                    .classBuilder(CLASS_NAME)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            // 1- Find all annotated element
            for (Element element : provider.roundEnv.getElementsAnnotatedWith(Entity.class)) {

                // 2- Generate a class
                TypeElement typeElement = (TypeElement) element;
                Entity entityAnnotation = typeElement.getAnnotation(Entity.class);
                String tableName = entityAnnotation.tableName().isEmpty() ?
                        element.getSimpleName().toString() : entityAnnotation.tableName();

                // method create table
                entityManagerClass.addMethod(makeCreateTableMethod(tableName, element));
            }

            // 3- Write generated class to a file
            JavaFile.builder(ROOT_PACKAGE_NAME, entityManagerClass.build()).build().writeTo(provider.filer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MethodSpec makeCreateTableMethod(String tableName, Element element) {
        String methodName = "createTable" + capitalize(tableName);
        StringBuilder sb = new StringBuilder("CREATE TABLE ").append(tableName).append("(");
        List<? extends Element> subElements = element.getEnclosedElements();
        for (Element e : subElements) {

            boolean acceptable = e.getKind().isField() && e.getAnnotation(Column.class) != null;
            if (!acceptable) continue;

            VariableElement varEle = (VariableElement) e;
            Column column = varEle.getAnnotation(Column.class);

            String fieldString = getCreateFieldString(column, varEle);
            sb.append(fieldString);
        }

        String createSQL = sb.toString().substring(0, sb.length() - 2) + ");";
        if (!createSQL.contains("PRIMARY KEY")) {
            provider.messager.printMessage(Diagnostic.Kind.ERROR, "Table is not defined any primary key.", element);
            return null;
        } else if (createSQL.matches("(.*PRIMARY KEY.*){2}")) {
            provider.messager.printMessage(Diagnostic.Kind.ERROR, "Table needs only one field is defined primary key.", element);
            return null;
        } else {
            return MethodSpec
                    .methodBuilder(methodName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addParameter(ClassName.get("android.database.sqlite", "SQLiteDatabase"), "db")
                    .addStatement("$L.execSQL($S)", "db", createSQL)
                    .build();
        }
    }

    private String getCreateFieldString(Column column, Element element) {
        String type = element.asType().toString();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(!column.name().isEmpty() ? column.name() : element.getSimpleName().toString()).append(SPACE);
        stringBuilder.append(getFieldVariableType(type));
        stringBuilder.append(column.primaryKey() ? " PRIMARY KEY" : "");
        stringBuilder.append(column.autoIncrement() ? " AUTOINCREMENT" : "");
        stringBuilder.append(column.notNull() ? " NOT NULL" : "");
        stringBuilder.append(column.unique() ? " UNIQUE" : "");
        stringBuilder.append(!column.defaultValue().isEmpty() ? SPACE + "DEFAULT '" + column.defaultValue() + "'" : "");
        stringBuilder.append(!column.checkExpression().isEmpty() ? SPACE + "CHECK(" + column.checkExpression() + ")" : "");
        stringBuilder.append(COMMA);
        stringBuilder.append(SPACE);
        return stringBuilder.toString();
    }

    private String getFieldVariableType(String type) {

        if (type.contains("int") ||
                type.contains("long") ||
                type.contains("Integer") ||
                type.contains("Long") ||
                type.contains("Boolean") ||
                type.contains("boolean")) {

            return "INTEGER";
        }

        if (type.contains("double") ||
                type.contains("float") ||
                type.contains("Double") ||
                type.contains("Float") ||
                type.contains("Decimal")) {

            return "REAL";
        }

        if (type.contains("String") ||
                type.contains("CharSequence") ||
                type.contains("char") ||
                type.contains("Character")) {
            return "TEXT";
        }

        if (type.contains("byte") ||
                type.contains("Byte")) {
            return "BLOB";
        }

        return "TEXT";
    }
}
