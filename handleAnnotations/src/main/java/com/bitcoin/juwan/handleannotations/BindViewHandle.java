package com.bitcoin.juwan.handleannotations;

import com.bitcoin.juwan.annotations.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
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

    static class AnnotationClass{

        String packageName;

        TypeElement typeElement;

        List<BindViewField> bindViewFieldList;

        AnnotationClass(String packageName, TypeElement typeElement) {
            this.packageName = packageName;
            this.typeElement = typeElement;
            bindViewFieldList = new ArrayList<>();
        }

        public void addField(BindViewField field) {
            bindViewFieldList.add(field);
        }

        public JavaFile generateJavaFile() {
            MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addAnnotation(Override.class)
                    .addParameter(TypeName.get(typeElement.asType()), "host")
                    .addParameter(Object.class, "o")
                    .addParameter(ClassName.get("com.bitcoin.juwan.applibrary", "ViewFinder"), "viewFinder");
            for(BindViewField field : bindViewFieldList) {
                bindViewMethodSpecBuilder.addStatement("host.$N = ($T)(viewFinder.findView(o, $N))",
                     field.getFieldName(), ClassName.get(field.getTypeFieldName()), typeElement.getAnnotation(BindView.class).value());
            }

            MethodSpec.Builder unBindViewMethodSpecBuilder = MethodSpec.methodBuilder("unBindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(TypeName.get(typeElement.asType()), "host");

            for(BindViewField field : bindViewFieldList) {
                unBindViewMethodSpecBuilder.addStatement("host.$N = null", field.getFieldName());
            }

            ClassName viewBinderClassName = ClassName.get("com.bitcoin.juwan.applibrary", "ViewBinder");
            TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "$ViewBinder")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindViewMethodSpecBuilder.build())
                    .addMethod(unBindViewMethodSpecBuilder.build())
                    .addSuperinterface(
                            ParameterizedTypeName.get(viewBinderClassName, TypeName.get(typeElement.asType()))
                    ).build();
            return JavaFile.builder(packageName, typeSpec).build();
        }
    }

    static class BindViewField {
        VariableElement elementField;

        int resId;

        BindViewField(Element element, int resId) {
            this.elementField = (VariableElement) element;
            this.resId = resId;
        }

        public int getResId() {
            return resId;
        }

        public String getFieldName() {
            return elementField.getSimpleName().toString();
        }

        public TypeMirror getTypeFieldName() {
            return this.elementField.asType();
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        annotationClassHashMap.clear();
        for(Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            AnnotationClass annotationClass = getAnnotationClass(typeElement);
            BindViewField bindViewField = new BindViewField(element, element.getAnnotation(BindView.class).value());
            annotationClass.addField(bindViewField);
        }

        for(AnnotationClass annotationClass : annotationClassHashMap.values()) {
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
        if(annotationClass == null) {
            annotationClass = new AnnotationClass(typeElement);
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
        return set;
    }
}
