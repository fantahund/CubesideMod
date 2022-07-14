package de.fanta.cubeside.mixin;

import de.fanta.cubeside.CubesideClientFabric;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "xaero.minimap.XaeroMinimap", remap = false)
public abstract class MixinXaeroForceFairPlay {

    /**
     * @author fantahund
     * @reason XaeroMap force FairPlay
     */
    @Overwrite(remap = false)
    public boolean isFairPlay() {
        if (CubesideClientFabric.hasPermission("xareomap")) {
            return true;
        } else {
            return CubesideClientFabric.isXaeroFairPlay();
        }
    }
}