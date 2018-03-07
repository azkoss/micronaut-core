package io.micronaut.annotation.processing;

import io.micronaut.context.annotation.Configuration;
import io.micronaut.inject.writer.BeanConfigurationWriter;
import io.micronaut.context.annotation.Configuration;
import io.micronaut.core.io.service.ServiceDescriptorGenerator;
import io.micronaut.inject.BeanConfiguration;
import io.micronaut.inject.annotation.AnnotationMetadataWriter;
import io.micronaut.inject.writer.BeanConfigurationWriter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.JavaFileObject;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;
import java.util.Set;

@SupportedAnnotationTypes({
    "io.micronaut.context.annotation.Configuration"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PackageConfigurationInjectProcessor extends AbstractInjectAnnotationProcessor {


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        AnnotationElementScanner scanner = new AnnotationElementScanner();
        Set<? extends Element> elements = roundEnv.getRootElements();
        ElementFilter.packagesIn(elements).forEach(element -> element.accept(scanner, element));
        return true;
    }

    class AnnotationElementScanner extends SimpleElementVisitor8<Object, Object> {
        @Override
        public Object visitPackage(PackageElement packageElement, Object p) {
            Object aPackage = super.visitPackage(packageElement, p);
            if (annotationUtils.hasStereotype(packageElement, Configuration.class)) {
                String packageName = packageElement.getQualifiedName().toString();
                BeanConfigurationWriter writer = new BeanConfigurationWriter(packageName, annotationUtils.getAnnotationMetadata(packageElement));
                try {
                    BeanDefinitionWriterVisitor visitor = new BeanDefinitionWriterVisitor(filer, getTargetDirectory().orElse(null));
                    writer.accept(visitor);
                } catch (IOException e) {
                    error("Unexpected error: %s", e.getMessage());
                }
            }
            return aPackage;
        }
    }
}
