package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.iani.cubesideutils.fabric.permission.PermissionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "xaero.common.settings.ModSettings", remap = false)
public class MixinXaeroDisableCaveMap {

    /**
     * @author fantahund
     * @reason Disable CaveMap in Normal XeroMap Version
     */
    @Overwrite(remap = false)
    public boolean caveMapsDisabled() {
        if (PermissionHandler.hasPermission("xareomap")) {
            return true;
        } else {
            return CubesideClientFabric.isXaeroFairPlay();
        }
    }
}
