package com.bitcoin.juwan.handleannotations;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

/**
 * author: liumengqiang
 * Date : 2019/10/26
 * Description : 使用BindView注解的相关类
 */
public class AnnotationClass {

    static  final class ClassTypeName {
        static final  ClassName viewClassName =  ClassName.get("android.view", "View");
        static final ClassName ViewFinderName = ClassName.get("com.bitcoin.juwan.applibrary", "ViewFinder");
        static final ClassName ViewBinderNamer = ClassName.get("com.bitcoin.juwan.applibrary", "ViewBinder");
        static final ClassName onClickName = ClassName.get("android.view.View", "OnClickListener");
    }

    String packageName; //类报名

    TypeElement typeElement; //

    List<BindViewField> bindViewFieldList; //成员变量使用BinView的集合

    MethodAnnotation methodAnnotation; //设置OnClick方法

    AnnotationClass(String packageName, TypeElement typeElement) {
        this.packageName = packageName;
        this.typeElement = typeElement;
        bindViewFieldList = new ArrayList<>();
    }

    void addField(BindViewField field) {
        bindViewFieldList.add(field);
    }

    JavaFile generateJavaFile() {
        //生成BindView方法
        MethodSpec.Builder bindViewMethodSpecBuilder = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(typeElement.asType()), "host")
                .addParameter(Object.class, "o")
                .addParameter(ClassTypeName.ViewFinderName, "viewFinder")
                .addStatement("this.host = host");

        //生成unBinderView方法
        MethodSpec.Builder unBindViewMethodSpecBuilder = MethodSpec.methodBuilder("unBindView")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(void.class)
                .addParameter(TypeName.get(typeElement.asType()), "host");

        for (BindViewField field : bindViewFieldList) {
            bindViewMethodSpecBuilder.addStatement("host.$N = ($T)(viewFinder.findView(o, $L))",
                    field.getFieldName(), ClassName.get(field.getTypeFieldName()), field.getResId());

            unBindViewMethodSpecBuilder.addStatement("host.$N = null", field.getFieldName());

            //是否需要生成onClick方法
            if(methodAnnotation != null && methodAnnotation.resIdIsNeedClick(field.getResId())) {
                bindViewMethodSpecBuilder.addStatement("host.$N.setOnClickListener(this)", field.getFieldName());
            }
        }

        unBindViewMethodSpecBuilder.addStatement("host = null");

        //是否需要生成onClick方法
        MethodSpec.Builder clickMethodSpecBuilder = null;
        if(methodAnnotation != null) {
            clickMethodSpecBuilder = MethodSpec.methodBuilder("onClick")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addParameter(ClassTypeName.viewClassName, "v");
            // 方法体代码块
            CodeBlock.Builder caseBlockBuild = CodeBlock.builder().beginControlFlow("switch (v.getId())");
            for (BindViewField field : bindViewFieldList){
                String methodName = methodAnnotation.getAnnotationMethodName();
                caseBlockBuild.add("case $L:\nhost.$N(v);\nbreak;\n", field.getResId(), methodName);
            }
            caseBlockBuild.add("default:\n").indent().unindent();
            caseBlockBuild.endControlFlow();
            clickMethodSpecBuilder.addCode(caseBlockBuild.build());
        }

        //生成类
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(typeElement.getSimpleName() + "$ViewBinder")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(bindViewMethodSpecBuilder.build())
                .addMethod(unBindViewMethodSpecBuilder.build())
                .addField(FieldSpec.builder(TypeName.get(typeElement.asType()), "host").build())
                .addSuperinterface(
                        ParameterizedTypeName.get(ClassTypeName.ViewBinderNamer, TypeName.get(typeElement.asType()))
                );
        if(methodAnnotation != null) { //该类是否需要添加onClick方法
            typeSpecBuilder.addSuperinterface(ClassTypeName.onClickName)
                    .addMethod(clickMethodSpecBuilder.build());
        }
        return JavaFile.builder(packageName, typeSpecBuilder.build()).build();
    }

    void setMethodAnnotation(ExecutableElement executableElement, int[] resIDArray) {
        methodAnnotation = new MethodAnnotation(executableElement, resIDArray);
    }
}
