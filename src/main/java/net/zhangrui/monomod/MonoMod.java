package net.zhangrui.monomod;

import net.fabricmc.api.ModInitializer;
import net.zhangrui.monomod.markets.MarketsMod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Very important comment
public class MonoMod implements ModInitializer {
	public static final String MOD_ID = "monomod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		MarketsMod markets = new MarketsMod();
		markets.initialize();

	}
}
