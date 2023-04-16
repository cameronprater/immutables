package org.immutables.value.processor.meta;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.util.ArrayList;
import java.util.List;

public final class NestedEnum {
    public final String name;
    public final String interfaceClause;
    public final List<Constant> constants;
    public final String type;
    public final boolean hasValue;
    public final String valueType;

    private NestedEnum(String name, String interfaceClause, List<Constant> constants, String type, boolean hasValue, String valueType) {
        this.name = name;
        this.interfaceClause = interfaceClause;
        this.constants = constants;
        this.type = type;
        this.hasValue = hasValue;
        this.valueType = valueType;
    }

    static NestedEnum from(TypeElement element) {
        String name = element.getSimpleName().toString();
        String interfaceClause = "";
        if (!element.getInterfaces().isEmpty()) {
            interfaceClause = " implements " + element.getInterfaces().get(0).toString();
        }

        List<Constant> constants = new ArrayList<>();
        for (VariableElement field : ElementFilter.fieldsIn(element.getEnclosedElements())) {
            if (field.getKind() != ElementKind.ENUM_CONSTANT) {
                continue;
            }

            constants.add(new Constant(field.getSimpleName().toString(), !field.getAnnotationMirrors().isEmpty()));
        }
        String type = element.getQualifiedName().toString();

        boolean hasValue = false;
        String valueType = "";

        for (ExecutableElement method: ElementFilter.methodsIn(element.getEnclosedElements())) {
            if (!method.getSimpleName().contentEquals("getValue")) {
                continue;
            }

            hasValue = true;
            valueType = method.getReturnType().toString();
        }

        return new NestedEnum(name, interfaceClause, constants, type, hasValue, valueType);
    }

    public static final class Constant {
        public final String name;
        public final boolean unknown;

        private Constant(String name, boolean unknown) {
            this.name = name;
            this.unknown = unknown;
        }
    }
}
