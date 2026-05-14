package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class ArchonRadiusTransmogEffect extends TransmogEffect {
    private static final int ELECTRICAL_YELLOW = 0xFFF15A;

    public static final ArchonRadiusTransmogEffect TYPE = new ArchonRadiusTransmogEffect(0.0F);

    private final float radius;

    public ArchonRadiusTransmogEffect(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    public MutableComponent getTooltip() {
        String formattedRadius = this.radius == (int) this.radius ? String.valueOf((int) this.radius) : String.valueOf(this.radius);
        return new TextComponent("+" + formattedRadius + " Radius to Archon").withStyle(Style.EMPTY.withColor(ELECTRICAL_YELLOW));
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        json.addProperty("radius", this.radius);
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        return new ArchonRadiusTransmogEffect(object.get("radius").getAsFloat());
    }
}
