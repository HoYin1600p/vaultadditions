package io.github.a1qs.vaultadditions.vault.gear.effect;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class ZarithReputationTransmogEffect extends TransmogEffect {
    public static final ZarithReputationTransmogEffect TYPE = new ZarithReputationTransmogEffect(0);

    private final int amount;

    public ZarithReputationTransmogEffect(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public MutableComponent getTooltip() {
        return new TextComponent("")
                .append(new TextComponent((this.amount >= 0 ? "+" : "") + this.amount + " ").withStyle(ChatFormatting.AQUA))
                .append(new TextComponent("Zarith ").withStyle(ChatFormatting.DARK_PURPLE))
                .append(new TextComponent("Reputation").withStyle(ChatFormatting.AQUA));
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = withType();
        json.addProperty("amount", this.amount);
        return json;
    }

    @Override
    public TransmogEffect deserialize(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        return new ZarithReputationTransmogEffect(object.get("amount").getAsInt());
    }
}
