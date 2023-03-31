/*
 * Decompiled with CFR 0.150.
 */
package digital.rbq.utils.entity;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import digital.rbq.utils.entity.ICheck;

public final class EntityValidator {
    private final Set<ICheck> checks = new HashSet<ICheck>();

    public final boolean validate(Entity entity) {
        for (ICheck check : this.checks) {
            if (check.validate(entity)) continue;
            return false;
        }
        return true;
    }

    public void add(ICheck check) {
        this.checks.add(check);
    }
}

