package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import de.iani.cubesideutils.fabric.permission.PermissionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "xaero.common.HudMod", remap = false)
public abstract class MixinXaeroForceFairPlay {

    /**
     * @author fantahund
     * @reason XaeroMap force FairPlay
     */
    @Overwrite(remap = false)
    public boolean isFairPlay() {
        if (PermissionHandler.hasPermission("xareomap")) {
            return true;
        } else {
            return CubesideClientFabric.isXaeroFairPlay();
        }
    }
}