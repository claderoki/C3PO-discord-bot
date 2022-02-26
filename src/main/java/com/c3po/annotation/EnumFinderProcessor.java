package com.c3po.annotation;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("com.c3po.annotation.EnumFinder")
public class EnumFinderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        Collection<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(EnumFinder.class);
//        List<TypeElement> types = ElementFilter.typesIn(annotatedElements);
//        String a = "";
        return true;
    }
}