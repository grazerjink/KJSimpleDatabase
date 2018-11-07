package com.winsonmac.kjsimplegenerator.processor;

import com.winsonmac.kjsimplegenerator.annotations.Entity;
import com.winsonmac.kjsimplegenerator.generators.EntityDaoCodeGenerator;
import com.winsonmac.kjsimplegenerator.generators.EntityManagerCodeGenerator;
import com.winsonmac.kjsimplegenerator.generators.ProcessProvider;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class KJDatabaseProcessor extends AbstractProcessor {

    private Filer filer; // help creating a new file tool
    private Messager messager;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        elements = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Entity.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        ProcessProvider provider = new ProcessProvider(roundEnvironment, filer, messager, elements);
        new EntityManagerCodeGenerator(provider).process();
        new EntityDaoCodeGenerator(provider).process();
        return true;
    }
}
