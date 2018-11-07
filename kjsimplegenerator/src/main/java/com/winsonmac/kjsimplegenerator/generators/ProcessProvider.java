package com.winsonmac.kjsimplegenerator.generators;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.util.Elements;

public class ProcessProvider {
    protected RoundEnvironment roundEnv;
    protected Filer filer;
    protected Messager messager;
    protected Elements elements;

    public ProcessProvider(RoundEnvironment roundEnvironment, Filer filer, Messager messager, Elements elements) {
        this.roundEnv = roundEnvironment;
        this.filer = filer;
        this.messager = messager;
        this.elements = elements;
    }

}
