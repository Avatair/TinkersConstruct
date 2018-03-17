package slimeknights.tconstruct.library.tools;

import net.minecraft.nbt.NBTTagCompound;

import slimeknights.tconstruct.library.materials.ExtraMaterialStats;
import slimeknights.tconstruct.library.materials.HandleMaterialStats;
import slimeknights.tconstruct.library.materials.HeadMaterialStats;
import slimeknights.tconstruct.library.utils.Tags;

public class ToolNBT {

	public int durability;
	public int harvestLevel;
	public float attack;
	public float speed; // mining speed
	public float attackSpeedMultiplier;
	public int modifiers; // free modifiers
	public int enchantability;

	private final NBTTagCompound parent;

	public ToolNBT() {
		durability = 0;
		harvestLevel = 0;
		attack = 0;
		speed = 0;
		attackSpeedMultiplier = 1;
		modifiers = ToolCore.DEFAULT_MODIFIERS;
		enchantability = 0;
		parent = new NBTTagCompound();
	}

	public ToolNBT(NBTTagCompound tag) {
		read(tag);
		parent = tag;
	}

	/** Initialize the stats with the heads. CALL THIS FIRST */
	public ToolNBT head(HeadMaterialStats... heads) {
		durability = 0;
		harvestLevel = 0;
		attack = 0;
		speed = 0;
		enchantability = 0;

		// average all stats
		for (HeadMaterialStats head : heads) {
			if (head != null) {
				durability += head.durability;
				attack += head.attack;
				speed += head.miningspeed;
				enchantability += head.enchantability;

				// use highest harvestlevel
				if (head.harvestLevel > harvestLevel) {
					harvestLevel = head.harvestLevel;
				}
			}
		}

		durability = Math.max(1, durability / heads.length);
		attack /= (float) heads.length;
		speed /= (float) heads.length;
		enchantability /= heads.length;

		return this;
	}

	/** Add stats from the accessoires. Call this second! */
	public ToolNBT extra(ExtraMaterialStats... extras) {
		int dur = 0;
		for (ExtraMaterialStats extra : extras) {
			if (extra != null) {
				dur += extra.extraDurability;
			}
		}
		this.durability += Math.round((float) dur / (float) extras.length);

		return this;
	}

	/** Calculate in handles. call this last! */
	public ToolNBT handle(HandleMaterialStats... handles) {
		// (Average Head Durability + Average Extra Durability) * Average Handle
		// Modifier + Average Handle Durability

		int dur = 0;
		float modifier = 0f;
		for (HandleMaterialStats handle : handles) {
			if (handle != null) {
				dur += handle.durability;
				modifier += handle.modifier;
			}
		}

		modifier /= (float) handles.length;
		this.durability = Math.round((float) this.durability * modifier);

		// add in handle durability change
		this.durability += Math.round((float) dur / (float) handles.length);

		this.durability = Math.max(1, this.durability);

		return this;
	}

	public void read(NBTTagCompound tag) {
		durability = tag.getInteger(Tags.DURABILITY);
		harvestLevel = tag.getInteger(Tags.HARVESTLEVEL);
		attack = (float) tag.getInteger(Tags.ATTACK) / Tags.FLOAT_ACCURACY;
		speed = (float) tag.getInteger(Tags.MININGSPEED) / Tags.FLOAT_ACCURACY;
		attackSpeedMultiplier = (float) tag.getInteger(Tags.ATTACKSPEEDMULTIPLIER) / Tags.FLOAT_ACCURACY;
		enchantability = tag.getInteger(Tags.ENCHANTABILITY);
		modifiers = tag.getInteger(Tags.FREE_MODIFIERS);
	}

	public void write(NBTTagCompound tag) {
		tag.setInteger(Tags.DURABILITY, durability);
		tag.setInteger(Tags.HARVESTLEVEL, harvestLevel);
		tag.setInteger(Tags.ATTACK, (int) (attack * Tags.FLOAT_ACCURACY));
		tag.setInteger(Tags.MININGSPEED, (int) (speed * Tags.FLOAT_ACCURACY));
		tag.setInteger(Tags.ATTACKSPEEDMULTIPLIER, (int) (attackSpeedMultiplier * Tags.FLOAT_ACCURACY));
		tag.setInteger(Tags.ENCHANTABILITY, enchantability);
		tag.setInteger(Tags.FREE_MODIFIERS, modifiers);
	}

	public NBTTagCompound get() {
		NBTTagCompound tag = parent.copy();
		write(tag);

		return tag;
	}

	// AUtogenerated equals and hashcode
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ToolNBT toolNBT = (ToolNBT) o;

		if (durability != toolNBT.durability) {
			return false;
		}
		if (harvestLevel != toolNBT.harvestLevel) {
			return false;
		}
		if (Float.compare(toolNBT.attack, attack) != 0) {
			return false;
		}
		if (Float.compare(toolNBT.speed, speed) != 0) {
			return false;
		}
		if (enchantability != toolNBT.enchantability) {
			return false;
		}
		return modifiers == toolNBT.modifiers;
	}

	@Override
	public int hashCode() {
		int result = durability;
		result = 31 * result + harvestLevel;
		result = 31 * result + (attack != +0.0f ? Float.floatToIntBits(attack) : 0);
		result = 31 * result + (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
		result = 31 * result + enchantability;
		result = 31 * result + modifiers;
		return result;
	}
}
