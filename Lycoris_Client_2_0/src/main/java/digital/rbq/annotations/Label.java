/*
 * Decompiled with CFR 0.150.
 */
package digital.rbq.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.FIELD, ElementType.TYPE})
public @interface Label {
    public String value();
}

