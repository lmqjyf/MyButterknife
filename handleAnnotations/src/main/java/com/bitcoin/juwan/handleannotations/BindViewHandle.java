package com.bitcoin.juwan.handleannotations;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.annotations.onClick;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * FileName：BindViewHandle
 * Create By：liumengqiang
 * Description：TODO
 */
@AutoService(Processor.class)
public class BindViewHandle extends AbstractProcessor {

    private Elements elementUtils;
    private Filer filer;
    private HashMap<String, AnnotationClass> annotationClassHashMap;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        annotationClassHashMap = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        annotationClassHashMap.clear();

        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            AnnotationClass annotationClass = getAnnotationClass(typeElement);
            BindViewField bindViewField = new BindViewField(elementUtils.getPackageOf(typeElement).getQualifiedName().toString(), element, element.getAnnotation(BindView.class).value());
            annotationClass.addField(bindViewField);
        }

        for(Element element : roundEnvironment.getElementsAnnotatedWith(onClick.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) element;
            AnnotationClass annotationClass = annotationClassHashMap.get(typeElement.getQualifiedName().toString());
            if(annotationClass != null) {
                annotationClass.setMethodAnnotation((ExecutableElement) element, executableElement.getAnnotation(onClick.class).value());
            } else {
                throw new IllegalArgumentException("需要设置BindView注释!");
            }
        }

        for (AnnotationClass annotationClass : annotationClassHashMap.values()) {
            try {
                annotationClass.generateJavaFile().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    private AnnotationClass getAnnotationClass(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        AnnotationClass annotationClass = annotationClassHashMap.get(qualifiedName);
        if (annotationClass == null) {
            annotationClass = new AnnotationClass(elementUtils.getPackageOf(typeElement).getQualifiedName().toString(), typeElement);
            annotationClassHashMap.put(qualifiedName, annotationClass);
        }
        return annotationClass;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set set = new LinkedHashSet();
        set.add(BindView.class.getCanonicalName());
        set.add(onClick.class.getCanonicalName());
        return set;
    }
}
