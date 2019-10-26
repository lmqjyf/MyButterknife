package com.bitcoin.juwan.handleannotations;

import com.bitcoin.juwan.annotations.BindView;
import com.bitcoin.juwan.annotations.onClick;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
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

    static class AnnotationClass {

        String packageName;

        TypeElement typeElement;

        List<BindViewField> bindViewFieldList;

        List<Integer> fieldClickList;

        ExecutableElement onClickElement;

        AnnotationClass(String packageName, TypeElement typeElement) {
            this.packageName = packageName;
            this.typeElement = typeElement;
            bindViewFieldList = new ArrayList<>();
            fieldClickList = new ArrayList<>();
        }

        void setOnClickElement(ExecutableElement element) {
            this.onClickElement = element;
        }

        void addField(BindViewField field) {
            bindViewFieldList.add(field);
        }

        JavaFile generateJavaFile() {
            MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("bindView")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(void.class)
                    .addAnnotation(Override.class)
                    .addParameter(TypeName.get(typeElement.asType()), "host")
                    .addParameter(Object.class, "o")
                    .addParameter(ClassName.get("com.bitcoin.juwan.applibrary", "ViewFinder"), "viewFinder")
                    .addStatement("this.host = host");

            MethodSpec.Builder unBindViewMethodSpecBuilder = MethodSpec.methodBuilder("unBindView")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(TypeName.get(typeElement.asType()), "host");

            MethodSpec.Builder clickMethodSpecBuilder = null;
            for (BindViewField field : bindViewFieldList) {

                bindViewMethodSpecBuilder.addStatement("host.$N = ($T)(viewFinder.findView(o, $L))",
                        field.getFieldName(), ClassName.get(field.getTypeFieldName()), field.getResId());

                unBindViewMethodSpecBuilder.addStatement("host.$N = null", field.getFieldName());

                if(fieldClickList.contains(field.getResId())) {
                    clickMethodSpecBuilder = MethodSpec.methodBuilder("onClick")
                            .addModifiers(Modifier.PUBLIC)
                            .addAnnotation(Override.class)
                            .returns(void.class)
                            .addParameter(ClassName.get("android.view", "View"), "v");
                    bindViewMethodSpecBuilder.addStatement("host.$N.setOnClickListener(this)", field.getFieldName());
                }
            }

            unBindViewMethodSpecBuilder.addStatement("host = null");

            if(clickMethodSpecBuilder != null) {
                // 方法体代码块
                CodeBlock.Builder caseBlockBuild = CodeBlock.builder().beginControlFlow("switch (v.getId())");
                for (BindViewField field : bindViewFieldList){
                    String methodName = onClickElement.getSimpleName().toString();
                    caseBlockBuild.add("case $L:\nhost.$N(v);\nbreak;\n", field.getResId(), methodName);
                }
                caseBlockBuild.add("default:\n").indent().unindent();
                caseBlockBuild.endControlFlow();
                clickMethodSpecBuilder.addCode(caseBlockBuild.build());
            }

            ClassName viewBinderClassName = ClassName.get("com.bitcoin.juwan.applibrary", "ViewBinder");
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "$ViewBinder")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(bindViewMethodSpecBuilder.build())
                    .addMethod(unBindViewMethodSpecBuilder.build())
                    .addField(FieldSpec.builder(TypeName.get(typeElement.asType()), "host").build())
                    .addSuperinterface(
                            ParameterizedTypeName.get(viewBinderClassName, TypeName.get(typeElement.asType()))
                    );
            if(fieldClickList.size() != 0) {
                typeSpecBuilder.addSuperinterface(ClassName.get("android.view.View", "OnClickListener"))
                .addMethod(clickMethodSpecBuilder.build());
            }
            return JavaFile.builder(packageName, typeSpecBuilder.build()).build();
        }

        void fieldSetOnClick(int[] fieldClickIds) {
            if(fieldClickIds == null) {
                return;
            }

            for(int resId : fieldClickIds) {
                fieldClickList.add(resId);
            }
        }
    }

    static class BindViewField {
        Element elementField;

        int resId;

        boolean isSetClick = false;

        BindViewField(Element element, int resId) {
            this.elementField =  element;
            this.resId = resId;
        }

        int getResId() {
            return resId;
        }

        String getFieldName() {
            return elementField.getSimpleName().toString();
        }

        TypeMirror getTypeFieldName() {
            return this.elementField.asType();
        }

        public Element getElemetent() {
            return elementField;
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        annotationClassHashMap.clear();

        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            AnnotationClass annotationClass = getAnnotationClass(typeElement);
            BindViewField bindViewField = new BindViewField(element, element.getAnnotation(BindView.class).value());
            annotationClass.addField(bindViewField);
        }

        for(Element element : roundEnvironment.getElementsAnnotatedWith(onClick.class)) {
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            ExecutableElement executableElement = (ExecutableElement) element;
            AnnotationClass annotationClass = annotationClassHashMap.get(typeElement.getQualifiedName().toString());
            annotationClass.setOnClickElement(executableElement);
            if(annotationClass != null) {
                annotationClass.fieldSetOnClick(executableElement.getAnnotation(onClick.class).value());
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
