package com.chenxuan.compiler;

import com.chenxuan.annotation.Hello;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
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
import javax.lang.model.util.Elements;

@AutoService(Processor.class)
public class CommonProcessor extends AbstractProcessor {
    //保存生成的java文件
    private Filer filer;
    //工具类，获取java类文件
    private Elements elementUtils;

    //初始化赋值
    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
    }

    //返回支持处理的注解类型
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(Hello.class.getName());
        return types;
    }

    //返回支持的java版本号
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        //获取所有源码文件
        Set<? extends Element> elements = roundEnvironment.getRootElements();
        for (Element element : elements) {
            //跳过非class
            if (!(element instanceof TypeElement)) {
                continue;
            }
            //转为class类型
            TypeElement typeElement = (TypeElement) element;
            //生成的文件类名
            String targetClassName = "Hello$" + element.getSimpleName();

            //创建方法
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder("hello")
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(TypeName.VOID);

            //获取类注解
            Hello typeElementAnnotation = typeElement.getAnnotation(Hello.class);
            if (typeElementAnnotation != null) {
                methodSpecBuilder.addStatement("$T.out.println($S)", System.class, "Hello, " + typeElementAnnotation.value() + "!");
            }

            //获取类中所有元素
            List<? extends Element> members = elementUtils.getAllMembers(typeElement);
            //类中含有注解元素
            List<Element> annotationMembers = new ArrayList<>();
            for (Element member : members) {
                Hello hello = member.getAnnotation(Hello.class);
                if (hello != null) {
                    annotationMembers.add(member);
                    //方法内容
                    methodSpecBuilder.addStatement("$T.out.println($S)", System.class, "Hello, " + hello.value() + "!");
                }
            }
            if (annotationMembers.isEmpty()) {
                continue;
            }

            //创建类
            TypeSpec typeSpec = TypeSpec
                    .classBuilder(targetClassName)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .addMethod(methodSpecBuilder.build())
                    .build();
            //创建java文件
            JavaFile javaFile = JavaFile
                    .builder("com.chenxuan.androidapt", typeSpec)
                    .build();
            try {
                //保存java类文件
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}