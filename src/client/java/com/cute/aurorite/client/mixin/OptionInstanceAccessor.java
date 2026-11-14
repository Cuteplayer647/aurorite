package com.cute.aurorite.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.OptionInstance;

/**
 * Raw access to an option's stored value.
 *
 * <p>{@code OptionInstance.set} validates against the option's value set, which
 * caps gamma at 1.0. Writing the field directly is the only way to push it past
 * that cap for a full-bright mode.
 */
@Mixin(OptionInstance.class)
public interface OptionInstanceAccessor {

	@Accessor("value")
	void aurorite$setRawValue(Object value);

	@Accessor("value")
	Object aurorite$getRawValue();
}
