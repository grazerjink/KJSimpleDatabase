package com.winsonmac.kjsimplegenerator.generators;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import com.winsonmac.kjsimplegenerator.annotations.Entity;
import com.winsonmac.kjsimplegenerator.annotations.Column;

import java.io.IOException;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class EntityDaoCodeGenerator extends BaseCodeGenerator {

    private TypeName kjManagerClass = ClassName.get(ROOT_PACKAGE_NAME, "KJDatabaseManager");
    private String dbManager = "dbManager";
    private String saveMethod = "save";
    private String findMethod = "find";

    public EntityDaoCodeGenerator(ProcessProvider processProvider) {
        super(processProvider);
    }

    public void process() {
        try {
            /*
             * 1- Find all annotated element
             */
            for (Element element : provider.roundEnv.getElementsAnnotatedWith(Entity.class)) {

                /*
                 * 2- Generate a class
                 */
                TypeElement typeElement = (TypeElement) element;
                Entity entityAnnotation = typeElement.getAnnotation(Entity.class);
                String tableName = entityAnnotation.tableName().isEmpty() ? typeElement.getSimpleName().toString() : entityAnnotation.tableName();

                String className = "KJ" + capitalize(tableName) + "DAO";

                TypeSpec.Builder daoClass = TypeSpec
                        .classBuilder(className)
                        .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                        .addField(FieldSpec
                                .builder(kjManagerClass, dbManager, Modifier.STATIC, Modifier.PRIVATE)
                                .initializer("$T.getInstance()", kjManagerClass)
                                .build())
//                        .addMethod(makeFindMethod(element))
                        .addMethod(makeSaveMethod(element));


                /*
                 * 3- Write generated class to a file
                 */
                JavaFile.builder(ROOT_PACKAGE_NAME, daoClass.build()).build().writeTo(provider.filer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MethodSpec makeSaveMethod(Element element) {
        Entity entityAnnotation = element.getAnnotation(Entity.class);
        String className = element.getSimpleName().toString();
        String packageName = provider.elements.getPackageOf(element).getQualifiedName().toString();
        String tableName = entityAnnotation.tableName().isEmpty() ? className : entityAnnotation.tableName();
        TypeName contentValue = ClassName.get("android.content", "ContentValues");
        String contentVar = "contentValue";
        String entityVar = "entity";
        String isExistVar = "isExist";
        String entityIdColumnName = "";
        String getEntityIdMethod = "";

        MethodSpec.Builder methodSpec = MethodSpec
                .methodBuilder(saveMethod)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(packageName, className), entityVar)
                .returns(TypeName.BOOLEAN)
                .addStatement("$T $L = new $T()", contentValue, contentVar, contentValue);

        for (Element subElement : element.getEnclosedElements()) {

            boolean acceptable = subElement.getKind().isField() && subElement.getAnnotation(Column.class) != null;
            if (!acceptable) continue;

            VariableElement varEle = (VariableElement) subElement;
            Column column = varEle.getAnnotation(Column.class);
            String fieldName = varEle.getSimpleName().toString();
            String columnName = !column.name().isEmpty() ? column.name() : fieldName;
            methodSpec.addStatement("$L.put($S, $L.get$L())", contentVar, columnName, entityVar, capitalize(fieldName));

            if (column.primaryKey()) {
                entityIdColumnName = columnName;
                getEntityIdMethod = capitalize(fieldName);
            }
        }
        methodSpec.addStatement("boolean $L = $L.isExist($L.get$L(),$S,$S)", isExistVar, dbManager, entityVar, getEntityIdMethod, entityIdColumnName, tableName);
        methodSpec.addCode("if($L) {", isExistVar);
        methodSpec.addStatement("\nreturn $L.updateSingle($L, $L.get$L(), $S, $S) > 0 ", dbManager, contentVar, entityVar, getEntityIdMethod, entityIdColumnName, tableName);
        methodSpec.addCode("} else {");
        methodSpec.addStatement("\nreturn $L.insertSingle($L, $S) != -1", dbManager, contentVar, tableName);
        methodSpec.addCode("}\n");
        return methodSpec.build();
    }
}
