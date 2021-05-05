package apple.voltskiya.custom_mobs.mobs.utils;

import net.minecraft.server.v1_16_R3.*;

public class UtilsAttribute {
    public static void fillAttributes(AttributeMapBase attributeMap, AttributeProvider attributeProvider) {
        for (GenericAttribute attributeGeneric : GenericAttribute.values()) {
            final AttributeModifiable attribute = new AttributeModifiable(attributeGeneric.get(), (o) -> {
            });
            for (AttributeModifiable s : attributeMap.b()) {
                System.out.println(s.getAttribute().getName() + " " + s.getValue());
            }


            attributeMap.getAttributes().add(attribute);

//            // if the attribute exists, set it
//            if (attributeProvider.c(attributeGeneric.get())) {
//                attribute.setValue(attributeProvider.a(attributeGeneric.get()));
//                System.out.println(attributeGeneric.get().getName());
//                attributeMap.b(attributeProvider);
//                //                System.out.println(attributeMap.a(attributeGeneric.get()).getAttribute().getName() + " " + attributeMap.a(attributeGeneric.get()).getValue());
//            }
        }
    }

    public enum GenericAttribute {
        MAX_HEALTH(GenericAttributes.MAX_HEALTH),
        FOLLOW_RANGE(GenericAttributes.FOLLOW_RANGE),
        KNOCKBACK_RESISTANCE(GenericAttributes.KNOCKBACK_RESISTANCE),
        MOVEMENT_SPEED(GenericAttributes.MOVEMENT_SPEED),
        FLYING_SPEED(GenericAttributes.FLYING_SPEED),
        ATTACK_DAMAGE(GenericAttributes.ATTACK_DAMAGE),
        ATTACK_KNOCKBACK(GenericAttributes.ATTACK_KNOCKBACK),
        ATTACK_SPEED(GenericAttributes.ATTACK_SPEED),
        ARMOR(GenericAttributes.ARMOR),
        ARMOR_TOUGHNESS(GenericAttributes.ARMOR_TOUGHNESS),
        LUCK(GenericAttributes.LUCK),
        SPAWN_REINFORCEMENTS(GenericAttributes.SPAWN_REINFORCEMENTS),
        JUMP_STRENGTH(GenericAttributes.JUMP_STRENGTH);
        private final AttributeBase attributeBase;

        GenericAttribute(AttributeBase attributeBase) {
            this.attributeBase = attributeBase;
        }

        public AttributeBase get() {
            return attributeBase;
        }
    }
}
